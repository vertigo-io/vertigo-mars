package io.mars.basemanagement.services.equipment;

import java.time.Instant;

import javax.inject.Inject;

import io.mars.authorization.SecuredEntities;
import io.mars.basemanagement.BasemanagementPAO;
import io.mars.basemanagement.dao.EquipmentSurveyDAO;
import io.mars.basemanagement.domain.Equipment;
import io.mars.basemanagement.domain.EquipmentSurvey;
import io.mars.basemanagement.domain.EquipmentSurveyDisplay;
import io.mars.support.MarsUserSession;
import io.mars.support.util.SecurityUtil;
import io.vertigo.account.authorization.AuthorizationUtil;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.easyforms.metaformulaire.domain.ModeleFormulaire;
import io.vertigo.easyforms.metaformulaire.services.FormulaireServices;
import io.vertigo.vega.webservice.validation.UiMessageStack;

@Transactional
public class EquipmentSurveyServices implements Component {

	@Inject
	private FormulaireServices formulaireServices;

	@Inject
	private EquipmentSurveyDAO equipmentSurveyDAO;

	@Inject
	private BasemanagementPAO basemanagementPAO;

	public DtList<EquipmentSurveyDisplay> getListByEquipment(final Long equipmentId, final DtListState dtListState) {
		return basemanagementPAO.listSurveysFromEquipmentId(equipmentId, AuthorizationUtil.authorizationCriteria(Equipment.class, SecuredEntities.EquipmentOperations.read));
		//		return equipmentSurveyDAO.findAll(Criterions.isEqualTo(EquipmentSurveyFields.equipmentId, equipmentId), dtListState);
	}

	public EquipmentSurvey getById(final Long esuId) {
		final var equipmentSurvey = equipmentSurveyDAO.get(esuId);
		equipmentSurvey.person().load();
		return equipmentSurvey;
	}

	public Long save(final UiMessageStack uiMessageStack, final EquipmentSurvey survey, final ModeleFormulaire modeleFormulaire) {
		Assertion.check()
				.isNull(survey.getEsuId(), "Cannot edit survey");
		// ---
		formulaireServices.checkFormulaire(survey, survey.getFormulaire(), modeleFormulaire, uiMessageStack);

		survey.setDateAnswer(Instant.now());
		survey.setPersonId(SecurityUtil.<MarsUserSession>getUserSession().getLoggedPerson().getPersonId());
		return equipmentSurveyDAO.save(survey).getEsuId();
	}

}
