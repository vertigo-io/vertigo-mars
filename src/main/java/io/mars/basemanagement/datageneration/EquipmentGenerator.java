package io.mars.basemanagement.datageneration;

import java.util.ArrayList;
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
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.easyforms.designer.services.IEasyFormsDesignerServices;
import io.vertigo.easyforms.domain.EasyForm;
import io.vertigo.easyforms.impl.runner.library.provider.FieldTypeDefinitionProvider.FieldTypeEnum;
import io.vertigo.easyforms.runner.model.template.EasyFormsTemplate;
import io.vertigo.easyforms.runner.model.template.EasyFormsTemplateField;

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
	private IEasyFormsDesignerServices easyFormsAdminServices;

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
		final List<EasyFormsTemplateField> botFields = new ArrayList<>();
		botFields.add(new EasyFormsTemplateField("iaAutonomy", FieldTypeEnum.LABEL).withLabel("IA autonomy"));
		botFields.add(new EasyFormsTemplateField("easyness", FieldTypeEnum.LABEL).withLabel("Easyness of use"));
		botFields.add(new EasyFormsTemplateField("obsolete", FieldTypeEnum.LABEL).withLabel("Is obsolete").withMandatory());
		createEquipmentCategory(true, "Bot", new EasyFormsTemplate(botFields));

		final List<EasyFormsTemplateField> buildingFields = new ArrayList<>();
		buildingFields.add(new EasyFormsTemplateField("equipment", FieldTypeEnum.LABEL).withLabel("Completude of equipment").withMandatory());
		buildingFields.add(new EasyFormsTemplateField("temperature", FieldTypeEnum.LABEL).withLabel("Temperature inside"));
		buildingFields.add(new EasyFormsTemplateField("wearState", FieldTypeEnum.LABEL).withLabel("State of wear"));
		createEquipmentCategory(true, "Building", new EasyFormsTemplate(buildingFields));

		final List<EasyFormsTemplateField> vehicleFields = new ArrayList<>();
		vehicleFields.add(new EasyFormsTemplateField("comfort", FieldTypeEnum.LABEL).withLabel("Comfort").withMandatory());
		vehicleFields.add(new EasyFormsTemplateField("speed", FieldTypeEnum.LABEL).withLabel("Speed"));
		vehicleFields.add(new EasyFormsTemplateField("storageCapacity", FieldTypeEnum.LABEL).withLabel("Storage capacity"));
		vehicleFields.add(new EasyFormsTemplateField("wearState", FieldTypeEnum.LABEL).withLabel("State of wear"));
		createEquipmentCategory(true, "Vehicle", new EasyFormsTemplate(vehicleFields));
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
