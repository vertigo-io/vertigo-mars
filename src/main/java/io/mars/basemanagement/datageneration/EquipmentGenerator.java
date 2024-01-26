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
import io.vertigo.easyforms.domain.EasyForm;
import io.vertigo.easyforms.easyformsrunner.model.EasyFormsTemplate;
import io.vertigo.easyforms.easyformsrunner.model.EasyFormsTemplateBuilder;
import io.vertigo.easyforms.impl.easyformsdesigner.services.EasyFormsDesignerServices;
import io.vertigo.easyforms.impl.easyformsrunner.library.provider.FieldTypeDefinitionProvider.FieldTypeEnum;

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
	private EasyFormsDesignerServices easyFormsAdminServices;

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
		createEquipmentCategory(true, "Bot", new EasyFormsTemplateBuilder()
				.addField("iaAutonomy", FieldTypeEnum.LABEL, "IA autonomy", null, false, false, List.of())
				.addField("easyness", FieldTypeEnum.LABEL, "Easyness of use", null, false, false, List.of())
				.addField("obsolete", FieldTypeEnum.LABEL, "Is obsolete", null, false, true, List.of())
				.build());
		createEquipmentCategory(true, "Building", new EasyFormsTemplateBuilder()
				.addField("equipment", FieldTypeEnum.LABEL, "Completude of equipment", null, false, true, List.of())
				.addField("temperature", FieldTypeEnum.LABEL, "Temperature inside", null, false, false, List.of())
				.addField("wearState", FieldTypeEnum.LABEL, "State of wear", null, false, false, List.of())
				.build());
		createEquipmentCategory(true, "Vehicle", new EasyFormsTemplateBuilder()
				.addField("comfort", FieldTypeEnum.LABEL, "Comfort", null, false, true, List.of())
				.addField("speed", FieldTypeEnum.LABEL, "Speed", null, false, false, List.of())
				.addField("storageCapacity", FieldTypeEnum.LABEL, "Storage capacity", null, false, false, List.of())
				.addField("wearState", FieldTypeEnum.LABEL, "State of wear", null, false, false, List.of())
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

	private EquipmentCategory createEquipmentCategory(final boolean active, final String label, final EasyFormsTemplate easyFormsTemplate) {
		final EquipmentCategory equipmentCategory = new EquipmentCategory();

		if (easyFormsTemplate != null) {
			final EasyForm easyForm = new EasyForm();
			easyForm.setTemplate(easyFormsTemplate);
			equipmentCategory.easyForm().set(easyFormsAdminServices.createEasyForm(easyForm));
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
