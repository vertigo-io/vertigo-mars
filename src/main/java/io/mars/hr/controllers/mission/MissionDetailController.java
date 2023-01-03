package io.mars.hr.controllers.mission;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.mars.basemanagement.domain.Base;
import io.mars.basemanagement.domain.Business;
import io.mars.basemanagement.services.base.BaseServices;
import io.mars.hr.domain.Mission;
import io.mars.hr.domain.Role;
import io.mars.hr.services.mission.MissionServices;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@Secured("AdmMissions")
@RequestMapping("/hr/mission")
public class MissionDetailController extends AbstractVSpringMvcController {

	@Inject
	private MissionServices missionServices;
	@Inject
	private BaseServices baseServices;

	private static final ViewContextKey<Mission> missionKey = ViewContextKey.of("mission");
	private static final ViewContextKey<String> successCallbackKey = ViewContextKey.of("successCallback");
	private static final ViewContextKey<Boolean> closeSuccessKey = ViewContextKey.of("closeSuccess");

	@GetMapping("/new")
	public void initContextCreation(final ViewContext viewContext, @RequestParam("personId") final Long personId, @RequestParam("successCallback") final String successCallback) {
		loadLists(viewContext);
		//---
		final Mission mission = new Mission();
		mission.person().setId(personId);
		//---
		viewContext
				.publishDto(missionKey, mission)
				.publishRef(successCallbackKey, successCallback)
				.publishRef(closeSuccessKey, false)
				//---
				.toModeCreate();
	}

	@GetMapping("/{missionId}")
	public void initContext(final ViewContext viewContext, @PathVariable("missionId") final Long missionId, @RequestParam("successCallback") final String successCallback) {
		loadLists(viewContext);
		//---
		viewContext
				.publishDto(missionKey, missionServices.get(missionId))
				.publishRef(successCallbackKey, successCallback)
				.publishRef(closeSuccessKey, false)
				//---
				.toModeEdit();
	}

	private void loadLists(final ViewContext viewContext) {
		viewContext
				.publishMdl(ViewContextKey.of("roles"), Role.class, null)
				.publishDtList(ViewContextKey.of("bases"), baseServices.getBases(DtListState.defaultOf(Base.class)))
				.publishMdl(ViewContextKey.of("businesses"), Business.class, null);
	}

	@PostMapping("/_save")
	public void doSave(final ViewContext viewContext, @ViewAttribute("mission") final Mission mission) {
		missionServices.save(mission);
		viewContext.publishRef(closeSuccessKey, true);
	}

	@PostMapping("/_create")
	public void doCreate(final ViewContext viewContext, @ViewAttribute("mission") final Mission mission) {
		missionServices.createMission(mission);
		viewContext.publishRef(closeSuccessKey, true);
	}

}
