package io.mars.basemanagement.services.base;

import java.util.List;

import javax.inject.Inject;

import io.mars.authorization.SecuredEntities;
import io.mars.basemanagement.BasemanagementPAO;
import io.mars.basemanagement.dao.BaseDAO;
import io.mars.basemanagement.dao.GeosectorDAO;
import io.mars.basemanagement.dao.PictureDAO;
import io.mars.basemanagement.domain.Base;
import io.mars.basemanagement.domain.BaseIndex;
import io.mars.basemanagement.domain.BaseOverview;
import io.mars.basemanagement.domain.BasesSummary;
import io.mars.basemanagement.domain.Equipment;
import io.mars.basemanagement.domain.Geosector;
import io.mars.basemanagement.domain.Picture;
import io.mars.domain.DtDefinitions.PictureFields;
import io.mars.hr.services.person.PersonServices;
import io.mars.support.fileinfo.FileInfoStd;
import io.mars.support.services.MarsFileServices;
import io.vertigo.account.authorization.AuthorizationCriteria;
import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.account.authorization.AuthorizationUtil;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datastore.filestore.FileStoreManager;
import io.vertigo.datastore.filestore.definitions.FileInfoDefinition;
import io.vertigo.datastore.filestore.model.FileInfo;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.impl.filestore.model.StreamFile;
import io.vertigo.social.notification.Notification;
import io.vertigo.social.notification.NotificationManager;

@Transactional
public class BaseServices implements Component, Activeable {

	@Inject
	private BaseDAO baseDAO;
	@Inject
	private PictureDAO pictureDAO;
	@Inject
	private GeosectorDAO geosectorDAO;

	@Inject
	private BasemanagementPAO basemanagementPAO;

	@Inject
	private MarsFileServices commonsServices;
	@Inject
	private FileStoreManager fileStoreManager;

	@Inject
	private PersonServices personServices;
	@Inject
	private NotificationManager notificationManager;

	@Inject
	private AuthorizationManager authorizationManager;

	private VFile defaultPhoto;

	@Override
	public void start() {
		defaultPhoto = StreamFile.of(
				"defaultBase.png",
				"image/png",
				PersonServices.class.getResource("/io/mars/images/noImage.png"));
	}

	@Override
	public void stop() {
		//rien
	}

	public Base get(final Long baseId) {
		final Base base = baseDAO.get(baseId);
		AuthorizationUtil.assertOperations(base, SecuredEntities.BaseOperations.read);
		return base;
	}

	public BaseOverview getBaseOverview(final Long baseId) {
		//Sometimes we must load the entity to check security filter
		AuthorizationUtil.assertOperations(baseDAO.get(baseId), SecuredEntities.BaseOperations.read);
		final AuthorizationCriteria<Equipment> securityEquipmentFilter = AuthorizationUtil.authorizationCriteria(Equipment.class, SecuredEntities.EquipmentOperations.read);
		return basemanagementPAO.getBaseOverview(baseId, securityEquipmentFilter);
	}

	public void save(final Base base, final List<FileInfoURI> addedPictureFile, final DtList<Picture> deletedPictures) {
		//apply Security Checks
		AuthorizationUtil.assertOperationsOnOriginalEntity(base, SecuredEntities.BaseOperations.write);
		//update Base
		baseDAO.save(base);

		//remove deleted pictures
		for (final Picture deletedPicture : deletedPictures) {
			pictureDAO.delete(deletedPicture.getPictureId());
			fileStoreManager.delete(toFileInfoStdURI(deletedPicture.getPicturefileId()));
		}
		//create added pictures
		for (final FileInfoURI fileInfoURI : addedPictureFile) {
			final VFile fileTmp = commonsServices.getFileTmp(fileInfoURI);
			final FileInfo fileInfo = fileStoreManager.create(new FileInfoStd(fileTmp));

			final Picture picture = new Picture();
			picture.setBaseId(base.getBaseId());
			picture.setPicturefileId((Long) fileInfo.getURI().getKey());
			pictureDAO.create(picture);
		}

		final Notification notification = Notification.builder()
				.withSender("System")
				.withTitle("Base updated")
				.withContent("Base " + base.getCode() + " informations updated")
				.withTTLInSeconds(600)
				.withType("MARS-BASE") //should prefix by app, in case of multi-apps notifications
				.withTargetUrl("/mars/basemanagement/base/information/" + base.getBaseId())
				.build();
		sendNotificationToAll(notification);
	}

	public DtList<Base> getBases(final DtListState dtListState) {
		final Criteria<Base> securityFilter = authorizationManager.getCriteriaSecurity(Base.class, SecuredEntities.BaseOperations.read);
		final DtList<Base> bases = baseDAO.findAll(securityFilter, dtListState);
		return bases;
	}

	public DtList<Geosector> getAllGeosectors() {
		return geosectorDAO.findAll(Criterions.alwaysTrue(), DtListState.defaultOf(Geosector.class));
	}

	public DtList<BaseIndex> getBaseIndex(final List<Long> baseIds) {
		//use for search indexation : no security filter
		return basemanagementPAO.loadBaseIndex(baseIds);
	}

	public BasesSummary getBaseSummary() {
		final AuthorizationCriteria<Base> securityBaseFilter = AuthorizationUtil.authorizationCriteria(Base.class, SecuredEntities.BaseOperations.read);
		final AuthorizationCriteria<Equipment> securityEquipmentFilter = AuthorizationUtil.authorizationCriteria(Equipment.class, SecuredEntities.EquipmentOperations.read);
		return basemanagementPAO.getBasesSummary(securityBaseFilter, securityEquipmentFilter);
	}

	public VFile getBaseMainPicture(final Long baseId) {
		final DtList<Picture> pictures = getPictures(baseId);
		if (pictures.isEmpty()) {
			return defaultPhoto;
		}
		return getBasePicture(pictures.get(0).getPicturefileId());
	}

	public VFile getBasePicture(final Long fileId) {
		//apply security check
		if (fileId == null) {
			return defaultPhoto;
		}
		return fileStoreManager.read(toFileInfoStdURI(fileId)).getVFile();
	}

	private static FileInfoURI toFileInfoStdURI(final Long fileId) {
		return new FileInfoURI(FileInfoDefinition.findFileInfoDefinition(FileInfoStd.class), fileId);
	}

	public DtList<Picture> getPictures(final Long baseId) {
		return pictureDAO.findAll(Criterions.isEqualTo(PictureFields.baseId, baseId), DtListState.defaultOf(Picture.class));
	}

	private void sendNotificationToAll(final Notification notification) {
		notificationManager.send(notification, personServices.getAllPersonsUID());
	}
}
