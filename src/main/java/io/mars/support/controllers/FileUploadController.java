package io.mars.support.controllers;

import java.util.ArrayList;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.mars.support.services.MarsFileServices;
import io.vertigo.datastore.filestore.model.FileInfo;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.UiFileInfoList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;
import io.vertigo.vega.webservice.stereotype.QueryParam;

@Controller
@RequestMapping("/commons/")
public class FileUploadController extends AbstractVSpringMvcController {

	@Inject
	private MarsFileServices commonsServices;

	public static final ViewContextKey<FileInfo> storedFileInfo = ViewContextKey.of("storedFiles");

	@PostMapping("/upload")
	public ViewContext uploadFile(final ViewContext viewContext, @QueryParam("file") final VFile file) {
		if (!viewContext.containsKey(storedFileInfo)) {
			viewContext.publishFileInfo(storedFileInfo, new ArrayList<>());
		}
		final FileInfo storeFile = commonsServices.saveFileTmp(file);
		final UiFileInfoList<FileInfo> storeFiles = viewContext.getUiFileInfoList(storedFileInfo);
		storeFiles.add(storeFile);
		viewContext.markModifiedKeys(storedFileInfo);
		return viewContext;
	}

	@DeleteMapping("/upload")
	public ViewContext removeFile(final ViewContext viewContext, @QueryParam("file") final FileInfoURI fileInfoUri) {
		commonsServices.deleteFileTmp(fileInfoUri);
		final UiFileInfoList<FileInfo> storeFiles = viewContext.getUiFileInfoList(storedFileInfo);
		storeFiles.remove(fileInfoUri);
		viewContext.markModifiedKeys(storedFileInfo);
		return viewContext; //if no return, you must get the response. Prefer to return old uri.
	}

}
