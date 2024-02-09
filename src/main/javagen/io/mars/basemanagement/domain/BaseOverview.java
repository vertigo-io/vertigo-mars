package io.mars.basemanagement.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.data.model.Data;
import io.vertigo.datamodel.data.stereotype.Field;
import io.vertigo.datamodel.data.util.DataUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class BaseOverview implements Data {
	private static final long serialVersionUID = 1L;

	private Long equipmentCount;
	private Long openedTickets;
	private Long workOrdersInprogress;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Equipments count'.
	 * @return Long equipmentCount <b>Obligatoire</b>
	 */
	@Field(smartType = "STyCount", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Equipments count")
	public Long getEquipmentCount() {
		return equipmentCount;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Equipments count'.
	 * @param equipmentCount Long <b>Obligatoire</b>
	 */
	public void setEquipmentCount(final Long equipmentCount) {
		this.equipmentCount = equipmentCount;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Open tickets'.
	 * @return Long openedTickets <b>Obligatoire</b>
	 */
	@Field(smartType = "STyCount", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Open tickets")
	public Long getOpenedTickets() {
		return openedTickets;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Open tickets'.
	 * @param openedTickets Long <b>Obligatoire</b>
	 */
	public void setOpenedTickets(final Long openedTickets) {
		this.openedTickets = openedTickets;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Work Orders in progress'.
	 * @return Long workOrdersInprogress <b>Obligatoire</b>
	 */
	@Field(smartType = "STyCount", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Work Orders in progress")
	public Long getWorkOrdersInprogress() {
		return workOrdersInprogress;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Work Orders in progress'.
	 * @param workOrdersInprogress Long <b>Obligatoire</b>
	 */
	public void setWorkOrdersInprogress(final Long workOrdersInprogress) {
		this.workOrdersInprogress = workOrdersInprogress;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataUtil.toString(this);
	}
}
