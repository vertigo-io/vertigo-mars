package io.mars.hr.datageneration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

import io.mars.basemanagement.domain.Base;
import io.mars.datageneration.DataGenerator;
import io.mars.hr.dao.PersonDAO;
import io.mars.hr.domain.Mission;
import io.mars.hr.domain.Person;
import io.mars.hr.domain.RoleEnum;
import io.mars.hr.services.mission.MissionServices;
import io.mars.support.fileinfo.FileInfoStd;
import io.mars.support.util.CSVReaderUtil;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.resource.ResourceManager;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datastore.filestore.FileStoreManager;
import io.vertigo.datastore.filestore.model.FileInfo;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.impl.filestore.model.StreamFile;

@Transactional
public class PersonGenerator implements Component {

	private static final int PERSON_CSV_FILE_COLUMN_NUMBER = 6;

	@Inject
	private PersonDAO personDAO;
	@Inject
	private ResourceManager resourceManager;
	@Inject
	private FileStoreManager fileStoreManager;
	@Inject
	private MissionServices missionServices;

	public void createInitialPersonsFromCSV(final String csvFilePath, final List<Base> bases) {
		CSVReaderUtil.parseCSV(resourceManager, csvFilePath, this::consume);
		final DtList<Person> persons = personDAO.findAll(Criterions.alwaysTrue(), DtListState.defaultOf(Person.class));
		createMissions(persons, bases);
	}

	private void consume(final String csvFilePath, final String[] record) {
		Assertion.check().isTrue(record.length == PERSON_CSV_FILE_COLUMN_NUMBER, "CSV File {0} Format not suitable for Persons", csvFilePath);
		// ---
		final String firstName = record[0];
		final String lastName = record[1];
		final String email = record[2];
		final String tags = StringUtil.isBlank(record[3]) ? null : record[3];
		final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		final LocalDate dateHired = LocalDate.parse(record[4], dateFormatter);
		final String picturePath = record[5];
		final Long pictureId;

		if (picturePath.isEmpty()) {
			pictureId = null;
		} else {
			final var cvsFolder = csvFilePath.substring(0, csvFilePath.lastIndexOf('/') + 1);
			final VFile pictureFile = StreamFile.of(
					picturePath.substring(picturePath.lastIndexOf('/') + 1),
					"image/" + picturePath.substring(picturePath.lastIndexOf('.') + 1),
					this.getClass().getResource(cvsFolder + picturePath));
			final FileInfo fileInfo = fileStoreManager.create(new FileInfoStd(pictureFile));
			pictureId = (Long) fileInfo.getURI().getKey();
		}

		personDAO.create(createPerson(firstName, lastName, email, tags, dateHired, pictureId));
	}

	private static Person createPerson(final String firstName,
			final String lastName,
			final String eMail,
			final String tags,
			final LocalDate dateHired,
			final Long pictureId) {
		final Person person = new Person();
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setEmail(eMail);
		person.setTags(tags);
		person.setDateHired(dateHired);
		person.setPicturefileId(pictureId);
		return person;
	}

	private void createMissions(final DtList<Person> persons, final List<Base> bases) {
		persons.forEach(person -> createMissionForPerson(bases, person));
		for (final Person person : persons) {
			for (int i = 0; i < 1 + DataGenerator.RND.nextInt(3); i++) {
				missionServices.createMission(createMissionForPerson(bases, person));
			}
		}
	}

	private static Mission createMissionForPerson(final List<Base> bases, final Person person) {
		final Mission mission = new Mission();
		mission.setPersonId(person.getPersonId());
		mission.setBaseId(bases.get(DataGenerator.RND.nextInt(bases.size())).getBaseId());
		mission.role().setEnumValue(RoleEnum.admin);
		return mission;
	}

}
