package io.mars.datageneration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.inject.Inject;

import io.mars.basemanagement.datageneration.BaseGenerator;
import io.mars.basemanagement.datageneration.EquipmentGenerator;
import io.mars.basemanagement.datageneration.ReferenceDataGenerator;
import io.mars.basemanagement.domain.Base;
import io.mars.hr.datageneration.PersonGenerator;
import io.mars.maintenance.datageneration.TicketGenerator;
import io.mars.opendata.datageneration.OpendataSetGenerator;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.commons.transaction.VTransactionWritable;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.ParamValue;
import io.vertigo.datamodel.data.util.DataModelUtil;
import io.vertigo.datastore.entitystore.EntityStoreManager;

public class DataGenerator implements Component {

	private static final Long RANDOM_SEED = 1337L;
	public static final Random RND = new Random(RANDOM_SEED);

	// a placer sur la bonne méthode
	//	@DaemonScheduled(name = "DMN_TICKET_AND_WO_GENERATOR", periodInSeconds = 10)

	@Inject
	private TicketGenerator ticketGenerator;
	@Inject
	private EquipmentGenerator equipmentGenerator;
	@Inject
	private PersonGenerator personGenerator;
	@Inject
	private BaseGenerator baseGenerator;
	@Inject
	private ReferenceDataGenerator referenceDataGenerator;
	@Inject
	private OpendataSetGenerator opendataSetGenerator;

	@Inject
	private EntityStoreManager entityStoreManager;
	@Inject
	private VTransactionManager transactionManager;

	private final int initialEquipmentUnits;
	private final String resourceRoot;

	@Inject
	public DataGenerator(
			@ParamValue("initialEquipmentUnits") final Integer initialEquipmentUnits,
			@ParamValue("dataset") final Optional<String> datasetOpt) {
		Assertion.check().isNotNull(initialEquipmentUnits);
		//---
		this.initialEquipmentUnits = initialEquipmentUnits;
		resourceRoot = "/io/mars/datasets/" + datasetOpt.orElse("mars") + "/datageneration/";
	}

	public void generateInitialData() {
		final int baseCount;
		try (VTransactionWritable transaction = transactionManager.createCurrentTransaction()) {
			baseCount = entityStoreManager.count(DataModelUtil.findDataDefinition(Base.class));
		}

		if (baseCount == 0) {
			generateReferenceData();
			final List<Base> bases = generateInitialBases();
			generateInitialEquipments(bases);
			generateInitialPersons(bases);
			generateInitialOpendataSets();
			generatePastData(ZonedDateTime.of(LocalDate.of(2018, 11, 19), LocalTime.of(0, 0), ZoneOffset.UTC).toInstant(), Instant.now());
		}
	}

	private void generateInitialOpendataSets() {
		opendataSetGenerator.createInitialOpendataSetsFromCSV(resourceRoot + "opendataSets.csv");
	}

	private void generateInitialEquipments(final List<Base> bases) {
		equipmentGenerator.createInitialEquipmentCategories();
		equipmentGenerator.createInitialEquipmentTypesFromCSV(resourceRoot + "equipmentTypes.csv");
		equipmentGenerator.createInitialEquipments(initialEquipmentUnits, bases);
	}

	private void generateInitialPersons(final List<Base> bases) {
		personGenerator.createInitialPersonsFromCSV(resourceRoot + "persons.csv", bases);
	}

	private List<Base> generateInitialBases() {
		return baseGenerator.generateInitialBases(resourceRoot);
	}

	private void generateReferenceData() {
		referenceDataGenerator.generateReferenceData(resourceRoot);
	}

	private void generatePastData(final Instant from, final Instant to) {
		Assertion.check()
				.isNotNull(from)
				.isNotNull(to);
		//
		Instant timeCursor = from;
		while (timeCursor.isBefore(to)) {
			ticketGenerator.generatePastTickets(timeCursor, ChronoUnit.DAYS, 1);
			// tous les 10 à 20 jours
			timeCursor = timeCursor.plus(RND.nextInt(10) + 10L, ChronoUnit.DAYS);
		}
	}

}
