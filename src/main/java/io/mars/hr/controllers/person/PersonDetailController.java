package io.mars.hr.controllers.person;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.mars.basemanagement.domain.Tag;
import io.mars.domain.DtDefinitions.GroupsFields;
import io.mars.domain.DtDefinitions.MissionDisplayFields;
import io.mars.hr.domain.Groups;
import io.mars.hr.domain.MissionDisplay;
import io.mars.hr.domain.Person;
import io.mars.hr.domain.PersonInput;
import io.mars.hr.services.mission.MissionServices;
import io.mars.hr.services.person.PersonServices;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.ProtectedValueUtil;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
//@Secured("AdmUsers") not here : can edit my profil
@RequestMapping("/hr/person")
public class PersonDetailController extends AbstractVSpringMvcController {

	private static final ViewContextKey<Person> personKey = ViewContextKey.of("person");
	private static final ViewContextKey<PersonInput> personInputKey = ViewContextKey.of("personInput");
	private static final ViewContextKey<MissionDisplay> missionsKey = ViewContextKey.of("missions");
	private static final ViewContextKey<Tag> tagsKey = ViewContextKey.of("tags");
	private static final ViewContextKey<Groups> groupsKey = ViewContextKey.of("groups");
	private static final ViewContextKey<FileInfoURI> personPictureUri = ViewContextKey.of("personPictureUri");

	@Inject
	private PersonServices personServices;
	@Inject
	private MissionServices missionServices;

	@GetMapping("/{personId}")
	public void initContext(final ViewContext viewContext, @PathVariable("personId") final Long personId) {

		final PersonInput personInput = new PersonInput();
		personInput.setGroups(List.of(1000L));
		viewContext
				.publishMdl(tagsKey, Tag.class, null) //all
				.publishDto(personKey, personServices.getPerson(personId))
				.publishDtList(missionsKey, MissionDisplayFields.missionId, missionServices.getMissionsByPersonId(personId))
				.publishFileInfoURI(personPictureUri, null)
				.publishDto(personInputKey, personInput)
				.publishDtList(groupsKey, GroupsFields.groupId, getFakeGroups())
				.toModeReadOnly();
	}

	@Secured("AdmUsers")
	@GetMapping("/new")
	public void initContext(final ViewContext viewContext) {
		viewContext
				.publishDto(personKey, personServices.initPerson())
				.publishMdl(tagsKey, Tag.class, null) //all
				.publishDto(personInputKey, new PersonInput())
				.publishDtList(groupsKey, GroupsFields.groupId, getFakeGroups())
				//---
				.toModeCreate();
	}

	@GetMapping("/picture/{protectedUrl}")
	public VFile loadFile(@PathVariable("protectedUrl") final String protectedUrl) {
		//project specific part
		final Long fileKey = ProtectedValueUtil.readProtectedValue(protectedUrl, Long.class);
		return personServices.getPersonPicture(fileKey);
	}

	@PostMapping("/_edit")
	public void doEdit(final ViewContext viewContext) {
		viewContext.toModeEdit();
	}

	@PostMapping("/_cancel")
	public void doCancel(final ViewContext viewContext) {
		viewContext.toModeReadOnly();

	}

	@Secured("AdmUsers")
	@PostMapping("/_create")
	public String doCreate(@ViewAttribute("person") final Person person, @ViewAttribute("personPictureUri") final Optional<FileInfoURI> personPictureFile) {
		personServices.createPerson(person);
		if (personPictureFile.isPresent()) {
			personServices.savePersonPicture(person.getPersonId(), personPictureFile.get());
		}
		return "redirect:/hr/person/" + person.getPersonId();
	}

	@PostMapping("/_save")
	public String doSave(@ViewAttribute("person") final Person person, @ViewAttribute("personInput") final PersonInput personInput, @ViewAttribute("personPictureUri") final Optional<FileInfoURI> personPictureFile) {
		personServices.updatePerson(person);
		if (personPictureFile.isPresent()) {
			personServices.savePersonPicture(person.getPersonId(), personPictureFile.get());
		}
		return "redirect:/hr/person/" + person.getPersonId();
	}

	@PostMapping("/_reloadMissions")
	public ViewContext doReloadMissions(final ViewContext viewContext, @ViewAttribute("person") final Person person) {
		viewContext.publishDtList(missionsKey, MissionDisplayFields.missionId, missionServices.getMissionsByPersonId(person.getPersonId()));
		return viewContext;
	}

	private DtList<Groups> getFakeGroups() {
		final Groups group1 = new Groups();
		group1.setGroupId(1000L);
		group1.setName("Group 1");

		final Groups group2 = new Groups();
		group2.setGroupId(1001L);
		group2.setName("Group 2");

		return DtList.of(group1, group2);
	}

}
