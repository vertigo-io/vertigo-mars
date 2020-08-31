package io.mars.basemanagement.dao;

import javax.inject.Inject;

import io.vertigo.core.lang.Generated;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.task.definitions.TaskDefinition;
import io.vertigo.datamodel.task.model.Task;
import io.vertigo.datamodel.task.model.TaskBuilder;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datastore.entitystore.EntityStoreManager;
import io.vertigo.datastore.impl.dao.DAO;
import io.vertigo.datastore.impl.dao.StoreServices;
import io.vertigo.datamodel.smarttype.SmartTypeManager;
import io.vertigo.datamodel.task.TaskManager;
import io.mars.basemanagement.domain.Equipment;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class EquipmentDAO extends DAO<Equipment, java.lang.Long> implements StoreServices {

	/**
	 * Contructeur.
	 * @param entityStoreManager Manager de persistance
	 * @param taskManager Manager de Task
	 * @param smartTypeManager SmartTypeManager
	 */
	@Inject
	public EquipmentDAO(final EntityStoreManager entityStoreManager, final TaskManager taskManager, final SmartTypeManager smartTypeManager) {
		super(Equipment.class, entityStoreManager, taskManager, smartTypeManager);
	}

	/**
	 * Indique que le keyConcept associé à cette UID va être modifié.
	 * Techniquement cela interdit les opérations d'ecriture en concurrence
	 * et envoie un évenement de modification du keyConcept (à la fin de transaction eventuellement)
	 * @param UID UID du keyConcept modifié
	 * @return KeyConcept à modifier
	 */
	public Equipment readOneForUpdate(final UID<Equipment> uid) {
		return entityStoreManager.readOneForUpdate(uid);
	}

	/**
	 * Indique que le keyConcept associé à cet id va être modifié.
	 * Techniquement cela interdit les opérations d'ecriture en concurrence
	 * et envoie un évenement de modification du keyConcept (à la fin de transaction eventuellement)
	 * @param id Clé du keyConcept modifié
	 * @return KeyConcept à modifier
	 */
	public Equipment readOneForUpdate(final java.lang.Long id) {
		return readOneForUpdate(createDtObjectUID(id));
	}

	/**
	 * Creates a taskBuilder.
	 * @param name  the name of the task
	 * @return the builder 
	 */
	private static TaskBuilder createTaskBuilder(final String name) {
		final TaskDefinition taskDefinition = Node.getNode().getDefinitionSpace().resolve(name, TaskDefinition.class);
		return Task.builder(taskDefinition);
	}

	/**
	 * Execute la tache TkGetEquipmentsByBaseCode.
	 * @param code String
	 * @return DtList de Equipment equipments
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkGetEquipmentsByBaseCode",
			request = "select " + 
 "            	equ.*" + 
 "			from equipment equ" + 
 "				join base bas on bas.base_id = equ.base_id" + 
 "			where bas.code = #code#",
			taskEngineClass = io.vertigo.dynamox.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtEquipment")
	public io.vertigo.datamodel.structure.model.DtList<io.mars.basemanagement.domain.Equipment> getEquipmentsByBaseCode(@io.vertigo.datamodel.task.proxy.TaskInput(name = "code", smartType = "STyCode") final String code) {
		final Task task = createTaskBuilder("TkGetEquipmentsByBaseCode")
				.addValue("code", code)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TkGetLastPurchasedEquipmentsByBaseId.
	 * @param baseId Long
	 * @return DtList de Equipment equipments
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkGetLastPurchasedEquipmentsByBaseId",
			request = "select " + 
 "            	equ.*" + 
 "			from equipment equ" + 
 "			where equ.base_id = #baseId#" + 
 "			order by equ.purchase_date desc" + 
 "			limit 50",
			taskEngineClass = io.vertigo.dynamox.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtEquipment")
	public io.vertigo.datamodel.structure.model.DtList<io.mars.basemanagement.domain.Equipment> getLastPurchasedEquipmentsByBaseId(@io.vertigo.datamodel.task.proxy.TaskInput(name = "baseId", smartType = "STyId") final Long baseId) {
		final Task task = createTaskBuilder("TkGetLastPurchasedEquipmentsByBaseId")
				.addValue("baseId", baseId)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TkInsertEquipmentsBatch.
	 * @param equipmentsList DtList de Equipment
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkInsertEquipmentsBatch",
			request = "INSERT INTO EQUIPMENT (EQUIPMENT_ID, " + 
 "        							NAME, " + 
 "        							CODE, " + 
 "        							HEALTH_LEVEL, " + 
 "        							PURCHASE_DATE, " + 
 "        							DESCRIPTION, " + 
 "        							TAGS, " + 
 "        							GEO_LOCATION, " + 
 "        							RENTING_FEE, " + 
 "        							EQUIPMENT_VALUE, " + 
 "        							BASE_ID, " + 
 "        							GEOSECTOR_ID, " + 
 "        							BUSINESS_ID, " + 
 "        							EQUIPMENT_TYPE_ID) values (nextval('SEQ_EQUIPMENT')," + 
 "        														#equipmentsList.name#," + 
 "        														#equipmentsList.code#," + 
 "        														#equipmentsList.healthLevel#," + 
 "        														#equipmentsList.purchaseDate#," + 
 "        														#equipmentsList.description#," + 
 "        														#equipmentsList.tags#," + 
 "        														#equipmentsList.geoLocation#," + 
 "        														#equipmentsList.rentingFee#," + 
 "        														#equipmentsList.equipmentValue#," + 
 "        														#equipmentsList.baseId#," + 
 "        														#equipmentsList.geosectorId#," + 
 "        														#equipmentsList.businessId#," + 
 "        														#equipmentsList.equipmentTypeId#)",
			taskEngineClass = io.vertigo.dynamox.task.TaskEngineProcBatch.class)
	public void insertEquipmentsBatch(@io.vertigo.datamodel.task.proxy.TaskInput(name = "equipmentsList", smartType = "STyDtEquipment") final io.vertigo.datamodel.structure.model.DtList<io.mars.basemanagement.domain.Equipment> equipmentsList) {
		final Task task = createTaskBuilder("TkInsertEquipmentsBatch")
				.addValue("equipmentsList", equipmentsList)
				.build();
		getTaskManager().execute(task);
	}

	/**
	 * Execute la tache TkLoadEquipmentsByChunk.
	 * @param limit Long
	 * @param offset Long
	 * @param dateExist Instant
	 * @return DtList de Equipment equipmentList
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkLoadEquipmentsByChunk",
			request = "select * from EQUIPMENT " + 
 "			where EQUIPMENT_ID > #offset#" + 
 "			and PURCHASE_DATE <= #dateExist#" + 
 "        	order by EQUIPMENT_ID asc" + 
 "			limit #limit#",
			taskEngineClass = io.vertigo.dynamox.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtEquipment")
	public io.vertigo.datamodel.structure.model.DtList<io.mars.basemanagement.domain.Equipment> loadEquipmentsByChunk(@io.vertigo.datamodel.task.proxy.TaskInput(name = "limit", smartType = "STyId") final Long limit, @io.vertigo.datamodel.task.proxy.TaskInput(name = "offset", smartType = "STyId") final Long offset, @io.vertigo.datamodel.task.proxy.TaskInput(name = "dateExist", smartType = "STyInstant") final java.time.Instant dateExist) {
		final Task task = createTaskBuilder("TkLoadEquipmentsByChunk")
				.addValue("limit", limit)
				.addValue("offset", offset)
				.addValue("dateExist", dateExist)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

}
