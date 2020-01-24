package io.mars.hr.services.person;

import javax.inject.Inject;

import io.mars.hr.dao.PersonDAO;
import io.mars.hr.domain.Person;
import io.mars.support.fileinfo.FileInfoStd;
import io.mars.support.services.MarsFileServices;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datastore.entitystore.EntityStoreManager;
import io.vertigo.datastore.filestore.FileManager;
import io.vertigo.datastore.filestore.FileStoreManager;
import io.vertigo.datastore.filestore.metamodel.FileInfoDefinition;
import io.vertigo.datastore.filestore.model.FileInfo;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;

@Transactional
public class PersonServices implements Component, Activeable {

	@Inject
	private PersonDAO personDAO;

	@Inject
	private EntityStoreManager entityStoreManager;
	@Inject
	private FileStoreManager fileStoreManager;
	@Inject
	private FileManager fileManager;

	@Inject
	private MarsFileServices commonsServices;

	private VFile defaultPhoto;

	@Override
	public void start() {
		defaultPhoto = fileManager.createFile(
				"defaultPhoto.png",
				"image/png",
				PersonServices.class.getResource("/defaultPhoto.png"));
	}

	@Override
	public void stop() {
		//rien
	}

	public Person getPerson(final Long personId) {
		return personDAO.get(personId);
	}

	public void updatePerson(final Person person) {
		personDAO.update(person);
	}

	public Person createPerson(final Person person) {
		return personDAO.create(person);
	}

	public Person initPerson() {
		return new Person();
	}

	public DtList<Person> getPersons(final DtListState dtListState) {
		final DtList<Person> persons = personDAO.findAll(Criterions.alwaysTrue(), dtListState);
		if (dtListState.getSortFieldName().isPresent()) {
			return entityStoreManager.sort(persons, dtListState.getSortFieldName().get(), dtListState.isSortDesc().get());
		}
		return persons;
	}

	public VFile getPersonPicture(final Long fileId) {
		//apply security check
		if (fileId == null) {
			return defaultPhoto;
		}
		return fileStoreManager.read(toFileInfoStdURI(fileId)).getVFile();
	}

	public void savePersonPicture(final Long personId, final FileInfoURI personPictureTmp) {
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
