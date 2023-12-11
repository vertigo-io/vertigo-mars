package io.mars.basemanagement.services.equipment;

import java.time.Instant;

import javax.inject.Inject;

import io.mars.basemanagement.dao.EquipmentSurveyDAO;
import io.mars.basemanagement.domain.EquipmentSurvey;
import io.mars.domain.DtDefinitions.EquipmentSurveyFields;
import io.mars.support.MarsUserSession;
import io.mars.support.util.SecurityUtil;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
public class EquipmentSurveyServices implements Component {

	@Inject
	private EquipmentSurveyDAO equipmentSurveyDAO;

	public DtList<EquipmentSurvey> getListByEquipment(final Long equipmentId, final DtListState dtListState) {
		return equipmentSurveyDAO.findAll(Criterions.isEqualTo(EquipmentSurveyFields.equipmentId, equipmentId), dtListState);
	}

	public EquipmentSurvey getById(final Long esuId) {
		return equipmentSurveyDAO.get(esuId);
	}

	public Long save(final EquipmentSurvey survey) {
		Assertion.check()
				.isNull(survey.getEsuId(), "Cannot edit survey");
		// ---
		survey.setDateAnswer(Instant.now());
		survey.setPersonId(SecurityUtil.<MarsUserSession>getUserSession().getLoggedPerson().getPersonId());
		return equipmentSurveyDAO.save(survey).getEsuId();
	}

}
