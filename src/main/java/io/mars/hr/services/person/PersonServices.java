package io.mars.hr.services.person;

import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.mars.authorization.GlobalAuthorizations;
import io.mars.hr.dao.PersonDAO;
import io.mars.hr.domain.Person;
import io.mars.support.MarsUserSession;
import io.mars.support.fileinfo.FileInfoStd;
import io.mars.support.services.MarsFileServices;
import io.mars.support.util.SecurityUtil;
import io.vertigo.account.account.Account;
import io.vertigo.account.authorization.AuthorizationUtil;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datamodel.data.model.UID;
import io.vertigo.datastore.entitystore.EntityStoreManager;
import io.vertigo.datastore.filestore.FileStoreManager;
import io.vertigo.datastore.filestore.definitions.FileInfoDefinition;
import io.vertigo.datastore.filestore.model.FileInfo;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.impl.filestore.model.StreamFile;

@Transactional
public class PersonServices implements Component, Activeable {

	@Inject
	private PersonDAO personDAO;

	@Inject
	private EntityStoreManager entityStoreManager;
	@Inject
	private FileStoreManager fileStoreManager;

	@Inject
	private MarsFileServices commonsServices;

	private VFile defaultPhoto;

	@Override
	public void start() {
		defaultPhoto = StreamFile.of(
				"defaultPhoto.png",
				"image/png",
				PersonServices.class.getResource("/io/mars/images/defaultPhoto.png"));
	}

	@Override
	public void stop() {
		//rien
	}

	public Person getPerson(final Long personId) {
		//if not AdmUsers, can only edit my info
		AuthorizationUtil.assertOr(
				AuthorizationUtil.hasAuthorization(GlobalAuthorizations.AtzAdmUsers),
				() -> personId.equals(SecurityUtil.<MarsUserSession>getUserSession().getLoggedPerson().getPersonId()));
		//-----
		return personDAO.get(personId);
	}

	public Person getLoggedPerson(final Long personId) {
		//use when user login
		//-----
		return personDAO.get(personId);
	}

	public void updatePerson(final Person person) {
		//if not AdmUsers, can only edit my info
		AuthorizationUtil.assertOr(
				AuthorizationUtil.hasAuthorization(GlobalAuthorizations.AtzAdmUsers),
				() -> person.getPersonId().equals(SecurityUtil.<MarsUserSession>getUserSession().getLoggedPerson().getPersonId()));
		//-----
		personDAO.update(person);
	}

	public Person createPerson(final Person person) {
		AuthorizationUtil.assertAuthorizations(GlobalAuthorizations.AtzAdmUsers);
		//-----
		return personDAO.create(person);
	}

	public Person initPerson() {
		return new Person();
	}

	public DtList<Person> getPersons(final DtListState dtListState) {
		AuthorizationUtil.assertAuthorizations(GlobalAuthorizations.AtzAdmUsers);
		//-----
		final DtList<Person> persons = personDAO.findAll(Criterions.alwaysTrue(), dtListState);
		if (dtListState.getSortFieldName().isPresent()) {
			return entityStoreManager.sort(persons, dtListState.getSortFieldName().get(), dtListState.isSortDesc().get());
		}
		return persons;
	}

	public Set<UID<Account>> getAllPersonsUID() {
		//Used internaly for notifications for exemple
		//-----
		final DtListState dtListState = DtListState.of(null);
		final DtList<Person> persons = personDAO.findAll(Criterions.alwaysTrue(), dtListState);
		final Set<UID<Account>> accountUIDs = persons
				.stream()
				.map((person) -> UID.of(Account.class, String.valueOf(person.getPersonId())))
				.collect(Collectors.toSet());
		return accountUIDs;
	}

	public VFile getPersonPicture(final Long fileId) {
		//apply security check
		if (fileId == null) {
			return defaultPhoto;
		}
		return fileStoreManager.read(toFileInfoStdURI(fileId)).getVFile();
	}

	public void savePersonPicture(final Long personId, final FileInfoURI personPictureTmp) {
		AuthorizationUtil.assertAuthorizations(GlobalAuthorizations.AtzAdmUsers);
		//-----
		final Person person = getPerson(personId);
		//apply security check
		final Long oldPicture = person.getPicturefileId();
		final VFile fileTmp = commonsServices.getFileTmp(personPictureTmp);
		final FileInfo fileInfo = fileStoreManager.create(new FileInfoStd(fileTmp));
		person.setPicturefileId((Long) fileInfo.getURI().getKey());
		updatePerson(person);
		if (oldPicture != null) {
			fileStoreManager.delete(toFileInfoStdURI(oldPicture));
		}
	}

	private static FileInfoURI toFileInfoStdURI(final Long fileId) {
		return new FileInfoURI(FileInfoDefinition.findFileInfoDefinition(FileInfoStd.class), fileId);
	}
}
