package io.mars.datageneration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import io.mars.basemanagement.datageneration.BaseGenerator;
import io.mars.basemanagement.datageneration.EquipmentGenerator;
import io.mars.basemanagement.datageneration.ReferenceDataGenerator;
import io.mars.basemanagement.domain.Base;
import io.mars.hr.datageneration.PersonGenerator;
import io.mars.maintenance.datageneration.TicketGenerator;
import io.mars.opendata.datageneration.OpendataSetGenerator;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.ParamValue;

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

	private final int initialEquipmentUnits;

	@Inject
	public DataGenerator(
			@ParamValue("initialEquipmentUnits") final Integer initialEquipmentUnits) {
		Assertion.check().isNotNull(initialEquipmentUnits);
		//---
		this.initialEquipmentUnits = initialEquipmentUnits;
	}

	public void generateInitialData() {
		generateReferenceData();
		final List<Base> bases = generateInitialBases();
		generateInitialEquipments(bases);
		generateInitialPersons(bases);
		generateInitialOpendataSets();
		generatePastData(ZonedDateTime.of(LocalDate.of(2018, 11, 19), LocalTime.of(0, 0), ZoneOffset.UTC).toInstant(), Instant.now());
	}

	private void generateInitialOpendataSets() {
		opendataSetGenerator.createInitialOpendataSetsFromCSV("io/mars/datageneration/opendataSets.csv");
	}

	private void generateInitialEquipments(final List<Base> bases) {
		equipmentGenerator.createInitialEquipmentCategories();
		equipmentGenerator.createInitialEquipmentTypesFromCSV("io/mars/datageneration/equipmentTypes.csv");
		equipmentGenerator.createInitialEquipments(initialEquipmentUnits, bases);
	}

	private void generateInitialPersons(final List<Base> bases) {
		personGenerator.createInitialPersonsFromCSV("io/mars/datageneration/persons.csv", bases);
	}

	private List<Base> generateInitialBases() {
		return baseGenerator.generateInitialBases();
	}

	private void generateReferenceData() {
		referenceDataGenerator.generateReferenceData();
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
