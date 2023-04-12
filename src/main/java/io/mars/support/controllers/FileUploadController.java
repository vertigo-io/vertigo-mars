package io.mars.support.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.mars.support.services.MarsFileServices;
import io.vertigo.account.security.UserSession;
import io.vertigo.account.security.VSecurityManager;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VUserException;
import io.vertigo.datastore.filestore.model.FileInfo;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.kvstore.KVCollection;
import io.vertigo.datastore.kvstore.KVStoreManager;
import io.vertigo.ui.core.UiFileInfo;
import io.vertigo.ui.impl.springmvc.util.UiRequestUtil;
import io.vertigo.vega.webservice.stereotype.QueryParam;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import io.vertigo.vega.webservice.validation.UiMessageStack.Level;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/commons/")
public class FileUploadController {

    private static final KVCollection FILE_INFOS_COLLECTION_KEY = new KVCollection("uiFileInfos");

    @Inject
    private MarsFileServices commonsServices;
    @Inject
    private VSecurityManager securityManager;
    @Inject
    private KVStoreManager kvStoreManager;

    @GetMapping("/upload/download/{fileUri}")
    public VFile downloadFile(@PathVariable("fileUri") final FileInfoURI fileInfoUri) {
        return commonsServices.getFile(fileInfoUri).getVFile();
    }

    @GetMapping("/upload")
    public UiFileInfo loadUiFileInfo(@QueryParam("file") final FileInfoURI fileInfoUri) {
        Optional<UiFileInfo> uiFileInfo = kvStoreManager.find(FILE_INFOS_COLLECTION_KEY, createUserFileKey(fileInfoUri),
                UiFileInfo.class);
        if (!uiFileInfo.isPresent()) {
            uiFileInfo = Optional.of(new UiFileInfo<>(commonsServices.getFile(fileInfoUri)));
            kvStoreManager.put(FILE_INFOS_COLLECTION_KEY, createUserFileKey(fileInfoUri), uiFileInfo.get());
        }
        return uiFileInfo.get();
    }

    @GetMapping("/upload/fileInfos")
    public List<UiFileInfo> loadUiFileInfos(@QueryParam("file") final List<FileInfoURI> fileInfoUris) {
        return fileInfoUris
                .stream()
                .map(fileInfoUri -> {
                    Optional<UiFileInfo> uiFileInfo = kvStoreManager.find(FILE_INFOS_COLLECTION_KEY,
                            createUserFileKey(fileInfoUri), UiFileInfo.class);
                    if (!uiFileInfo.isPresent()) {
                        uiFileInfo = Optional.of(new UiFileInfo<>(commonsServices.getFile(fileInfoUri)));
                        kvStoreManager.put(FILE_INFOS_COLLECTION_KEY, createUserFileKey(fileInfoUri), uiFileInfo.get());
                    }
                    return uiFileInfo.get();
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/upload")
    public FileInfoURI uploadFile(@QueryParam("file") final VFile vFile) {
        final FileInfo storeFile = commonsServices.saveFileTmp(vFile);
        kvStoreManager.put(FILE_INFOS_COLLECTION_KEY, createUserFileKey(storeFile.getURI()),
                new UiFileInfo<>(storeFile));
        return storeFile.getURI();
    }

    @DeleteMapping("/upload")
    public FileInfoURI removeFile(@QueryParam("file") final FileInfoURI fileInfoUri) {
        // supportServices.removeFile(fileInfoUri);
        // Don't remove file now : it may be needed it user go back before saving
        // obtainUiFileInfoListSession().remove(fileInfoUri);
        return fileInfoUri; // if no return, you must get the response. Prefer to return old uri.
    }

    private String createUserFileKey(final FileInfoURI fileInfoURI) {
        final Optional<UserSession> userSessionOptional = securityManager.getCurrentUserSession();
        Assertion.check().isTrue(userSessionOptional.isPresent(), "UserSession is mandatory for security token");
        // -----
        return new StringBuilder()
                .append(userSessionOptional.get().getSessionUUID().toString())
                .append(":")
                .append(fileInfoURI.toURN())
                .toString();
    }

    public void publishFileInfo(final FileInfo fileInfo) {
        kvStoreManager.put(FILE_INFOS_COLLECTION_KEY, createUserFileKey(fileInfo.getURI()), new UiFileInfo<>(fileInfo));
    }

    @ResponseBody
    @ExceptionHandler(VUserException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public static Object handleVUserException(final VUserException ex, final HttpServletRequest request) {
        final UiMessageStack uiMessageStack = UiRequestUtil.obtainCurrentUiMessageStack();
        uiMessageStack.addGlobalMessage(Level.ERROR, ex.getMessage());
        // ---
        return uiMessageStack;
    }
}
