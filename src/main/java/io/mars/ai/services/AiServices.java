package io.mars.ai.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import io.mars.ai.domain.AiDocumentInfo;
import io.mars.support.fileinfo.FileInfoStd;
import io.vertigo.ai.llm.LlmManager;
import io.vertigo.ai.llm.model.VPersona;
import io.vertigo.ai.llm.model.rag.VLlmDocument;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.util.VCollectors;
import io.vertigo.datastore.filestore.FileStoreManager;
import io.vertigo.datastore.filestore.model.FileInfoURI;

@Transactional
public class AiServices implements Component {

	private static final String COMMON_STYLE = "la réponse ne doit comporter que du markdown, il ne faut pas écrire de formules en LaTeX par exemple. Quand un montant est présent sans précisions, il s'agit d'un montant TTC.";

	private static final Map<String, VPersona> PERSONA_MAP = Map.of(
			"STE",
			new VPersona("Stéphane", null,
					"Tu es un assistant neutre et aidant, t'efforçant de répondre au mieux à la question de façon claire et concise. Tu n'as pas de personnalité propre, tu es un assistant virtuelle. Si nécessaire, tu peux demander des précisions pour mieux comprendre la question.",
					null, COMMON_STYLE),
			"MAR",
			new VPersona("Marie", null,
					"Tu es une chef comptable, tu t'intéresses à la comptabilité et à la finance, tu es très précise et tu n'hésite pas à mettre des tableaux pour expliquer les choses, détailler les prix HT et TTC. Tu n'hésite pas à utisiser ta calculatrice pour vérifier ou calculer les montants, en particulier utiliser les bonnes formules pour calculer le montant de la TVA à partir d'un montant TTC.",
					null, COMMON_STYLE),
			"DAN",
			new VPersona("Daniel", null,
					"Tu es un directeur de projets, à ce titre tu à une vision organisationnelle et financière des projets, tu es très orienté sur les coûts et les délais.",
					null, COMMON_STYLE),
			"ISA",
			new VPersona("Isabelle", null,
					"Tu es une architecte informatique, tu as une vision globale des systèmes d'information et des architectures techniques, tu es très orientée sur les normes et les standards.",
					null, COMMON_STYLE),
			"JUL",
			new VPersona("Julia", null,
					"Tu es une assistante virtuel très pragmatique qui répond avec le moins de mots possible, par exemple 'Oui' ou 'Non' ou si l'on te demande un montant tu répond uniquement le montant sans autre texte ni mise en forme. Tu ne refuses jamais de répondre et fait de ton mieux pour donner les informations demandées.",
					null, COMMON_STYLE));

	@Inject
	private FileStoreManager fileStoreManager;

	@Inject
	private LlmManager llmManager;

	public void indexDocument(final FileInfoURI fileUri) {
		final var file = fileStoreManager.read(fileUri);
		// persist file
		final var persistedFile = fileStoreManager.create(new FileInfoStd(file.getVFile()));

		// add to documents
		final var docSource = llmManager.getPersistedDocumentSource();
		docSource.addDocument(new VLlmDocument(persistedFile));
	}

	public void deleteDocument(final FileInfoURI fileUri) {
		// delete from storage
		fileStoreManager.delete(fileUri);

		// delete from documents
		final var docSource = llmManager.getPersistedDocumentSource();
		docSource.removeDocument(fileUri);
	}

	public DtList<AiDocumentInfo> searchDocument(final String query, final Optional<Integer> maxChunks, final Optional<Double> minScore) {
		final var docMap = new LinkedHashMap<String, AiDocumentInfo>(); // LinkedHashMap to preserve document order by pertinence
		llmManager.getPersistedDocumentSource().search(query, Map.of(), maxChunks.orElse(100), minScore.orElse(0.7d)).forEach(r -> {
			final AiDocumentInfo curentDoc = docMap.computeIfAbsent(r.document().fileInfo().getURI().toURN(), k -> {
				final var newDoc = new AiDocumentInfo();
				newDoc.setDocUri(r.document().fileInfo().getURI());
				newDoc.setFileName((String) r.document().metadatas().get("file_name"));
				newDoc.setChunkCount(0L);
				newDoc.setTextFragments(new ArrayList<>());
				newDoc.setFragmentsScores(new ArrayList<>());
				newDoc.setScorePercent(0L);
				return newDoc;
			});

			final Long scorePercent = Math.round(r.score() * 100);
			curentDoc.setChunkCount(curentDoc.getChunkCount() + 1);
			curentDoc.getTextFragments().add(r.textFragment());
			curentDoc.setScorePercent(Math.max(curentDoc.getScorePercent(), scorePercent));
			curentDoc.getFragmentsScores().add(scorePercent);
		});

		return docMap.values().stream().collect(VCollectors.toDtList(AiDocumentInfo.class));
	}

	public VPersona getPersona(final String personaCode) {
		return PERSONA_MAP.get(personaCode);
	}

}
