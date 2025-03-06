package io.mars.ai.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.mars.ai.domain.AiDocumentInfo;
import io.mars.ai.domain.AiQuery;
import io.mars.ai.services.AiServices;
import io.mars.support.MarsUserSession;
import io.mars.support.util.SecurityUtil;
import io.vertigo.ai.llm.LlmChat;
import io.vertigo.ai.llm.LlmManager;
import io.vertigo.ai.llm.model.VLlmMessageStreamConfig;
import io.vertigo.ai.llm.model.VPersona;
import io.vertigo.ai.llm.model.VPromptContext;
import io.vertigo.ai.llm.plugin.lc4j.rag.storage.Lc4jPgVectorStoragePlugin;
import io.vertigo.core.lang.WrappedException;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/ai/documents/")
public class AiDocumentsController extends AbstractVSpringMvcController {

	@Inject
	private AiServices aiServices;

	@Inject
	private LlmManager llmManager;

	@Inject
	private Optional<Lc4jPgVectorStoragePlugin> lc4jPgVectorStoragePluginOpt;

	private static final ViewContextKey<AiQuery> aiDocumentKey = ViewContextKey.of("aiDocument");

	private static final ViewContextKey<Boolean> isEnabledKey = ViewContextKey.of("isEnabled");
	private static final ViewContextKey<AiDocumentInfo> documentInfoListKey = ViewContextKey.of("documentInfoList");
	private static final ViewContextKey<AiDocumentInfo> documentSearchInfoListKey = ViewContextKey.of("documentSearchInfoList");

	private static final VPersona PERSONA = new VPersona(
			"Agnès",
			null,
			"Tu es une assistante neutre et aidante, t'efforçant de répondre au mieux à la question de façon claire et concise. Tu n'as pas de personnalité propre, tu es une assistante virtuelle. Si nécessaire, tu peux demander des précisions pour mieux comprendre la question.",
			null,
			"La réponse ne doit comporter que du markdown, il ne faut pas écrire de formules en LaTeX par exemple. Quand un montant est présent sans précisions, il s'agit d'un montant TTC.");

	@GetMapping("/")
	public void initContext(final ViewContext viewContext) {
		//---
		viewContext
				.publishDto(aiDocumentKey, new AiQuery())
				.publishRef(isEnabledKey, lc4jPgVectorStoragePluginOpt.isPresent())
				.publishDtList(documentInfoListKey, new DtList<>(AiDocumentInfo.class))
				.publishDtList(documentSearchInfoListKey, new DtList<>(AiDocumentInfo.class))
				//---
				.toModeCreate();

		doRefreshDocInfos(viewContext);
	}

	@PostMapping("/_refresh")
	public ViewContext doRefreshDocInfos(final ViewContext viewContext) {
		lc4jPgVectorStoragePluginOpt.ifPresent(pgVector -> {
			final var rawDocInfos = pgVector.getAllDocumentsInfos();
			final var docInfos = new DtList<AiDocumentInfo>(AiDocumentInfo.class);
			for (final var rawDocInfo : rawDocInfos) {
				final var newDocInfo = new AiDocumentInfo();
				newDocInfo.setDocUri(rawDocInfo.fileInfoURI());
				newDocInfo.setFileName(rawDocInfo.fileName());
				newDocInfo.setChunkCount(rawDocInfo.chunkCount());
				docInfos.add(newDocInfo);
			}
			viewContext.publishDtList(documentInfoListKey, docInfos);
		});
		return viewContext;
	}

	@PostMapping("/_search")
	public ViewContext doSearch(final ViewContext viewContext, @RequestParam("query") final String query) {
		viewContext.publishDtList(documentSearchInfoListKey, aiServices.searchDocument(query));

		return viewContext;
	}

	@PostMapping("/_index")
	@ResponseBody
	public String doIndex(@RequestParam("fileUri") final FileInfoURI fileUri) {
		aiServices.indexDocument(fileUri);

		return "{\"success\": true}";
	}

	@PostMapping("/_delete")
	public ViewContext doDelete(final ViewContext viewContext, @RequestParam("fileUri") final FileInfoURI fileUri) {
		aiServices.deleteDocument(fileUri);

		return doRefreshDocInfos(viewContext);
	}

	@PostMapping("/_initChat")
	@ResponseBody
	public String initChat(@RequestParam("fileUri") final Optional<FileInfoURI> fileUriOpt) {
		final VPromptContext context = new VPromptContext();
		context.setPersona(PERSONA);

		final var connectedUser = SecurityUtil.<MarsUserSession>getUserSession().getLoggedPerson();
		context.setUserPersona(new VPersona(connectedUser.getFullName(), null, null, null, null));

		final var docSource = llmManager.getPersistedDocumentSource();

		LlmChat chat;
		if (fileUriOpt.isPresent()) {
			chat = llmManager.initChatOnSpecificFiles(context, docSource, fileUriOpt.get());
		} else {
			chat = llmManager.initChatOnAllFiles(context, docSource);
		}
		return "{\"id\":\"" + chat.getId() + "\"}"; // String because Java Long is too big for javascript numbers
	}

	@PostMapping("/_ask")
	@ResponseBody
	public String doChat(@RequestParam("prompt") final String instructions, @RequestParam("chatId") final UUID chatId) {
		return llmManager.getChat(chatId).chat(instructions).message().getHtml();
	}

	@GetMapping("/_askStream")
	public SseEmitter doChatStream(@RequestParam("prompt") final String instructions, @RequestParam("chatId") final UUID chatId) {
		final SseEmitter emitter = new SseEmitter();
		llmManager.getChat(chatId).chatStream(instructions, VLlmMessageStreamConfig.builder()
				.withPartialMessageHandler(chatMessage -> {
					try {
						emitter.send(Map.of("message", chatMessage.message().getHtml()));
					} catch (final IOException e) {
						throw WrappedException.wrap(e);
					}
				})
				.withMessageHandler(chatMessage -> {
					try {
						emitter.send(Map.of("message", chatMessage.message().getHtml(), "sources", chatMessage.message().getSources(), "end", true));
						emitter.complete();
					} catch (final IOException e) {
						throw WrappedException.wrap(e);
					}
				})
				.withErrorHandler(emitter::completeWithError)
				.build());
		return emitter;
	}

}
