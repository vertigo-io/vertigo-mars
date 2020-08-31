package io.mars.opendata.services;

import javax.inject.Inject;

import io.mars.opendata.dao.OpendataSetDAO;
import io.mars.opendata.domain.OpendataSet;
import io.mars.support.fileinfo.FileInfoStd;
import io.mars.support.services.MarsFileServices;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datastore.filestore.FileStoreManager;
import io.vertigo.datastore.filestore.definitions.FileInfoDefinition;
import io.vertigo.datastore.filestore.model.FileInfo;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.impl.filestore.model.StreamFile;

@Transactional
public class OpendataSetServices implements Component, Activeable {

	@Inject
	private OpendataSetDAO opendataSetDAO;
	@Inject
	private FileStoreManager fileStoreManager;
	@Inject
	private MarsFileServices commonsServices;

	private VFile defaultOpendataSetPicture;

	@Override
	public void start() {

		defaultOpendataSetPicture = StreamFile.of(
				"defaultOpendataSetPhoto.png",
				"image/png",
				OpendataSetServices.class.getResource("/io/mars/datageneration/files/opendataset/defaultOpendataSetPhoto.png"));
	}

	@Override
	public void stop() {
		//nothing
	}

	public OpendataSet getOpendataSet(final Long odsId) {
		return opendataSetDAO.get(odsId);
	}

	public void save(final OpendataSet opendataSet) {
		opendataSetDAO.save(opendataSet);
	}

	public void updateOpendataSet(final OpendataSet opendataSet) {
		opendataSetDAO.update(opendataSet);
	}

	public DtList<OpendataSet> getOpendataSets(final DtListState dtListState) {
		return opendataSetDAO.findAll(Criterions.alwaysTrue(), dtListState);
	}

	public VFile getOpendataSetPicture(final Long fileId) {
		//apply security check
		if (fileId == null) {
			return defaultOpendataSetPicture;
		}
		return fileStoreManager.read(toFileInfoStdURI(fileId)).getVFile();
	}

	public void saveOpendataSetPicture(final Long odsId, final FileInfoURI odsPictureTmp) {
		final OpendataSet opendataSet = getOpendataSet(odsId);
		//apply security check
		final Long oldPicture = opendataSet.getPicturefileId();
		final VFile fileTmp = commonsServices.getFileTmp(odsPictureTmp);
		final FileInfo fileInfo = fileStoreManager.create(new FileInfoStd(fileTmp));
		opendataSet.setPicturefileId((Long) fileInfo.getURI().getKey());
		updateOpendataSet(opendataSet);
		if (oldPicture != null) {
			fileStoreManager.delete(toFileInfoStdURI(oldPicture));
		}
	}

	private static FileInfoURI toFileInfoStdURI(final Long fileId) {
		return new FileInfoURI(FileInfoDefinition.findFileInfoDefinition(FileInfoStd.class), fileId);
	}

	public OpendataSet createOpendataSet(final OpendataSet opendataSet) {
		return opendataSetDAO.create(opendataSet);
	}

}
