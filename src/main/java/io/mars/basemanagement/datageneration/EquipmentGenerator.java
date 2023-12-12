package io.mars.basemanagement.datageneration;

import java.util.List;

import javax.inject.Inject;

import io.mars.basemanagement.BasemanagementPAO;
import io.mars.basemanagement.dao.BusinessDAO;
import io.mars.basemanagement.dao.EquipmentDAO;
import io.mars.basemanagement.domain.Base;
import io.mars.basemanagement.domain.Equipment;
import io.mars.catalog.dao.EquipmentCategoryDAO;
import io.mars.catalog.dao.EquipmentTypeDAO;
import io.mars.catalog.domain.EquipmentCategory;
import io.mars.catalog.domain.EquipmentType;
import io.mars.domain.DtDefinitions.EquipmentCategoryFields;
import io.mars.support.util.CSVReaderUtil;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.resource.ResourceManager;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.easyforms.metaformulaire.domain.MetaFormulaire;
import io.vertigo.easyforms.metaformulaire.domain.ModeleFormulaire;
import io.vertigo.easyforms.metaformulaire.domain.ModeleFormulaireBuilder;
import io.vertigo.easyforms.metaformulaire.domain.TypeDeChamp;
import io.vertigo.easyforms.metaformulaire.services.MetaFormulaireServices;

@Transactional
public class EquipmentGenerator implements Component {

	private static final int EQUIPMENT_TYPE_CSV_FILE_COLUMN_NUMBER = 3;

	@Inject
	private EquipmentDAO equipmentDAO;
	@Inject
	private EquipmentCategoryDAO equipmentCategoryDAO;
	@Inject
	private EquipmentTypeDAO equipmentTypeDAO;
	@Inject
	private BasemanagementPAO basemanagementPAO;
	@Inject
	private BusinessDAO businessDAO;
	@Inject
	private ResourceManager resourceManager;
	@Inject
	private MetaFormulaireServices metaFormulaireServices;

	public void createInitialEquipments(final int equipmentUnitsToGenerate, final List<Base> bases) {
		final DtList<EquipmentType> equipmentTypes = equipmentTypeDAO.selectEquipmentType();
		equipmentTypes.forEach(equType -> equType.equipmentCategory().load());// not so great but 20 lines

		final DtList<Equipment> equipments = new FakeEquipmentListBuilder()
				.withBases(bases)
				.withGeosectorIdList(basemanagementPAO.selectGeosectorId())
				.withBusinessList(businessDAO.selectBusiness())
				.withEquipmentTypeList(equipmentTypes)
				.withMaxValues(equipmentUnitsToGenerate)
				.build();

		equipmentDAO.insertEquipmentsBatch(equipments);
	}

	public void createInitialEquipmentCategories() {
		createEquipmentCategory(true, "Bot", new ModeleFormulaireBuilder()
				.ajouterChamp("iaAutonomy", TypeDeChamp.of(MetaFormulaireServices.PREFIX_CODE_TYPE_CHAMP + "Label"), null, "IA autonomy", null, false, false, List.of())
				.ajouterChamp("easyness", TypeDeChamp.of(MetaFormulaireServices.PREFIX_CODE_TYPE_CHAMP + "Label"), null, "Easyness of use", null, false, false, List.of())
				.ajouterChamp("obsolete", TypeDeChamp.of(MetaFormulaireServices.PREFIX_CODE_TYPE_CHAMP + "Label"), null, "Is obsolete", null, false, false, List.of())
				.build());
		createEquipmentCategory(true, "Building", new ModeleFormulaireBuilder()
				.ajouterChamp("equipment", TypeDeChamp.of(MetaFormulaireServices.PREFIX_CODE_TYPE_CHAMP + "Label"), null, "Completude of equipment", null, false, false, List.of())
				.ajouterChamp("temperature", TypeDeChamp.of(MetaFormulaireServices.PREFIX_CODE_TYPE_CHAMP + "Label"), null, "Temperature inside", null, false, false, List.of())
				.ajouterChamp("wearState", TypeDeChamp.of(MetaFormulaireServices.PREFIX_CODE_TYPE_CHAMP + "Label"), null, "State of wear", null, false, false, List.of())
				.build());
		createEquipmentCategory(true, "Vehicle", new ModeleFormulaireBuilder()
				.ajouterChamp("comfort", TypeDeChamp.of(MetaFormulaireServices.PREFIX_CODE_TYPE_CHAMP + "Label"), null, "Comfort", null, false, false, List.of())
				.ajouterChamp("speed", TypeDeChamp.of(MetaFormulaireServices.PREFIX_CODE_TYPE_CHAMP + "Label"), null, "Speed", null, false, false, List.of())
				.ajouterChamp("storageCapacity", TypeDeChamp.of(MetaFormulaireServices.PREFIX_CODE_TYPE_CHAMP + "Label"), null, "Storage capacity", null, false, false, List.of())
				.ajouterChamp("wearState", TypeDeChamp.of(MetaFormulaireServices.PREFIX_CODE_TYPE_CHAMP + "Label"), null, "State of wear", null, false, false, List.of())
				.build());
	}

	private void consume(final String csvFilePath, final String[] record) {
		Assertion.check().isTrue(record.length == EQUIPMENT_TYPE_CSV_FILE_COLUMN_NUMBER,
				"CSV File {0} Format not suitable for Equipment Types", csvFilePath);
		//---
		final Boolean enabled = Boolean.valueOf(record[0]);
		final String nextCategoryLabel = record[1];
		final String equipmentTypeName = record[2];
		final EquipmentCategory equipmentCategory = equipmentCategoryDAO.find(Criterions.isEqualTo(EquipmentCategoryFields.label, nextCategoryLabel));

		equipmentTypeDAO.create(createEquipmentType(enabled, equipmentTypeName, equipmentCategory));
	}

	public void createInitialEquipmentTypesFromCSV(final String csvFilePath) {
		CSVReaderUtil.parseCSV(resourceManager, csvFilePath, this::consume);
	}

	private EquipmentCategory createEquipmentCategory(final boolean active, final String label, final ModeleFormulaire modeleFormulaire) {
		final EquipmentCategory equipmentCategory = new EquipmentCategory();

		if (modeleFormulaire != null) {
			final MetaFormulaire metaFormulaire = new MetaFormulaire();
			metaFormulaire.setModele(modeleFormulaire);
			equipmentCategory.metaFormulaire().set(metaFormulaireServices.creerMetaFormulaire(metaFormulaire));
		}

		equipmentCategory.setActive(active);
		equipmentCategory.setLabel(label);
		return equipmentCategoryDAO.create(equipmentCategory);
	}

	private static EquipmentType createEquipmentType(final boolean active, final String label, final EquipmentCategory equipmentCategory) {
		final EquipmentType equipmentType = new EquipmentType();
		equipmentType.setActive(active);
		equipmentType.setLabel(label);
		equipmentType.equipmentCategory().set(equipmentCategory);
		return equipmentType;
	}

}
