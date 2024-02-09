package io.mars.maintenance.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.data.model.Entity;
import io.vertigo.datastore.impl.entitystore.EnumStoreVAccessor;
import io.vertigo.datamodel.data.model.UID;
import io.vertigo.datastore.impl.entitystore.StoreVAccessor;
import io.vertigo.datamodel.data.stereotype.Field;
import io.vertigo.datamodel.data.util.DataUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class WorkOrder implements Entity {
	private static final long serialVersionUID = 1L;

	private Long woId;
	private String code;
	private String ticketCode;
	private String name;
	private String description;
	private java.time.LocalDate dateCreated;
	private java.time.LocalDate dateClosed;
	private java.time.LocalDate dueDate;

	@io.vertigo.datamodel.data.stereotype.Association(
			name = "ATicketWorkOrder",
			fkFieldName = "ticketId",
			primaryDtDefinitionName = "DtTicket",
			primaryIsNavigable = true,
			primaryRole = "Ticket",
			primaryLabel = "Ticket",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtWorkOrder",
			foreignIsNavigable = false,
			foreignRole = "WorkOrder",
			foreignLabel = "WorkOrder",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.mars.maintenance.domain.Ticket> ticketIdAccessor = new StoreVAccessor<>(io.mars.maintenance.domain.Ticket.class, "Ticket");

	@io.vertigo.datamodel.data.stereotype.Association(
			name = "AWorkOrderWorkOrderStatus",
			fkFieldName = "workOrderStatusId",
			primaryDtDefinitionName = "DtWorkOrderStatus",
			primaryIsNavigable = true,
			primaryRole = "WorkOrderStatus",
			primaryLabel = "Work Order Status",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtWorkOrder",
			foreignIsNavigable = false,
			foreignRole = "WorkOrder",
			foreignLabel = "WorkOrder",
			foreignMultiplicity = "0..*")
	private final EnumStoreVAccessor<io.mars.maintenance.domain.WorkOrderStatus, io.mars.maintenance.domain.WorkOrderStatusEnum> workOrderStatusIdAccessor = new EnumStoreVAccessor<>(io.mars.maintenance.domain.WorkOrderStatus.class, "WorkOrderStatus", io.mars.maintenance.domain.WorkOrderStatusEnum.class);

	/** {@inheritDoc} */
	@Override
	public UID<WorkOrder> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Id'.
	 * @return Long woId <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Id")
	public Long getWoId() {
		return woId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'Id'.
	 * @param woId Long <b>Obligatoire</b>
	 */
	public void setWoId(final Long woId) {
		this.woId = woId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Code'.
	 * @return String code <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Code")
	public String getCode() {
		return code;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Code'.
	 * @param code String <b>Obligatoire</b>
	 */
	public void setCode(final String code) {
		this.code = code;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Number'.
	 * @return String ticketCode
	 */
	@Field(smartType = "STyLabel", label = "Number")
	public String getTicketCode() {
		return ticketCode;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Number'.
	 * @param ticketCode String
	 */
	public void setTicketCode(final String ticketCode) {
		this.ticketCode = ticketCode;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Name'.
	 * @return String name <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Name")
	public String getName() {
		return name;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Name'.
	 * @param name String <b>Obligatoire</b>
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Description'.
	 * @return String description
	 */
	@Field(smartType = "STyDescription", label = "Description")
	public String getDescription() {
		return description;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Description'.
	 * @param description String
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Creation Date'.
	 * @return LocalDate dateCreated
	 */
	@Field(smartType = "STyLocaldate", label = "Creation Date")
	public java.time.LocalDate getDateCreated() {
		return dateCreated;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Creation Date'.
	 * @param dateCreated LocalDate
	 */
	public void setDateCreated(final java.time.LocalDate dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Closing Date'.
	 * @return LocalDate dateClosed
	 */
	@Field(smartType = "STyLocaldate", label = "Closing Date")
	public java.time.LocalDate getDateClosed() {
		return dateClosed;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Closing Date'.
	 * @param dateClosed LocalDate
	 */
	public void setDateClosed(final java.time.LocalDate dateClosed) {
		this.dateClosed = dateClosed;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Due Date'.
	 * @return LocalDate dueDate
	 */
	@Field(smartType = "STyLocaldate", label = "Due Date")
	public java.time.LocalDate getDueDate() {
		return dueDate;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Due Date'.
	 * @param dueDate LocalDate
	 */
	public void setDueDate(final java.time.LocalDate dueDate) {
		this.dueDate = dueDate;
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Ticket'.
	 * @return Long ticketId
	 */
	@io.vertigo.datamodel.data.stereotype.ForeignKey(smartType = "STyId", label = "Ticket", fkDefinition = "DtTicket" )
	public Long getTicketId() {
		return (Long) ticketIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Ticket'.
	 * @param ticketId Long
	 */
	public void setTicketId(final Long ticketId) {
		ticketIdAccessor.setId(ticketId);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Work Order Status'.
	 * @return String workOrderStatusId
	 */
	@io.vertigo.datamodel.data.stereotype.ForeignKey(smartType = "STyCode", label = "Work Order Status", fkDefinition = "DtWorkOrderStatus" )
	public String getWorkOrderStatusId() {
		return (String) workOrderStatusIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Work Order Status'.
	 * @param workOrderStatusId String
	 */
	public void setWorkOrderStatusId(final String workOrderStatusId) {
		workOrderStatusIdAccessor.setId(workOrderStatusId);
	}

 	/**
	 * Association : Ticket.
	 * @return l'accesseur vers la propriété 'Ticket'
	 */
	public StoreVAccessor<io.mars.maintenance.domain.Ticket> ticket() {
		return ticketIdAccessor;
	}

 	/**
	 * Association : Work Order Status.
	 * @return l'accesseur vers la propriété 'Work Order Status'
	 */
	public EnumStoreVAccessor<io.mars.maintenance.domain.WorkOrderStatus, io.mars.maintenance.domain.WorkOrderStatusEnum> workOrderStatus() {
		return workOrderStatusIdAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataUtil.toString(this);
	}
}
