package io.mars.support.boot;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.mars.authorization.GlobalAuthorizations;
import io.mars.authorization.SecuredEntities.BaseAuthorizations;
import io.mars.authorization.SecuredEntities.EquipmentAuthorizations;
import io.mars.hr.domain.RoleEnum;
import io.vertigo.account.authorization.definitions.Authorization;
import io.vertigo.account.authorization.definitions.AuthorizationName;
import io.vertigo.account.authorization.definitions.Role;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.definition.Definition;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.SimpleDefinitionProvider;

public final class MarsRoleDefinitionProvider implements SimpleDefinitionProvider {

	//@Inject
	//private RoleDAO roleDao;

	@Override
	public List<Definition> provideDefinitions(final DefinitionSpace definitionSpace) {
		return List.of(
				createRole(RoleEnum.admin,
						GlobalAuthorizations.AtzAdmMissions, GlobalAuthorizations.AtzAdmUsers, GlobalAuthorizations.AtzViewBases, GlobalAuthorizations.AtzAdmMasterData,
						EquipmentAuthorizations.AtzEquipment$writeAll, EquipmentAuthorizations.AtzEquipment$editTickets,
						BaseAuthorizations.AtzBase$admin, BaseAuthorizations.AtzBase$addEquiAdm),
				createRole(RoleEnum.manager,
						GlobalAuthorizations.AtzAdmMissions, GlobalAuthorizations.AtzViewBases, GlobalAuthorizations.AtzAdmMasterData, GlobalAuthorizations.AtzViewAcademy,
						EquipmentAuthorizations.AtzEquipment$readConf, EquipmentAuthorizations.AtzEquipment$write, EquipmentAuthorizations.AtzEquipment$editTickets,
						BaseAuthorizations.AtzBase$write, BaseAuthorizations.AtzBase$readConf, BaseAuthorizations.AtzBase$addEqui),
				createRole(RoleEnum.engineer,
						GlobalAuthorizations.AtzViewBases, GlobalAuthorizations.AtzViewAcademy,
						EquipmentAuthorizations.AtzEquipment$write, EquipmentAuthorizations.AtzEquipment$editTickets,
						BaseAuthorizations.AtzBase$read, BaseAuthorizations.AtzBase$addEqui),
				createRole(RoleEnum.trainee,
						GlobalAuthorizations.AtzViewBases, GlobalAuthorizations.AtzViewAcademy,
						EquipmentAuthorizations.AtzEquipment$read, EquipmentAuthorizations.AtzEquipment$readTickets,
						BaseAuthorizations.AtzBase$read));
	}

	private Role createRole(final RoleEnum roleName, final AuthorizationName... authorizationNames) {
		//final io.mars.hr.domain.Role marsRole = getMarsRole(roleName);
		final String roleId = "R" + roleName.getEntityUID().getId();
		final String roleLabel = roleName.name();
		final List<Authorization> authorizations = Arrays.stream(authorizationNames)
				.map(name -> getAuthorization(name))
				.collect(Collectors.toList());
		return new Role(roleId, roleLabel, authorizations);
	}

	private Authorization getAuthorization(final AuthorizationName authorizationName) {
		final DefinitionSpace definitionSpace = Node.getNode().getDefinitionSpace();
		return definitionSpace.resolve(authorizationName.name(), Authorization.class);
	}

	/*private io.mars.hr.domain.Role getMarsRole(final RoleEnum roleName) {
		return roleDao.get(roleName.getEntityUID());
	}*/
}
