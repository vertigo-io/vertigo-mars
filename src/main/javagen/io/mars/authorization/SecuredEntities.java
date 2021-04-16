package io.mars.authorization;

import io.vertigo.account.authorization.definitions.Authorization;
import io.vertigo.account.authorization.definitions.AuthorizationName;
import io.vertigo.account.authorization.definitions.OperationName;
import io.vertigo.core.node.Node;
import io.mars.basemanagement.domain.Equipment;
import io.mars.basemanagement.domain.Base;

/**
 * Warning. This class is generated automatically !
 *
 * Enum of the security authorizations and operations associated with secured entities known by the application.
 */
public final class SecuredEntities {

	private SecuredEntities() {
		//private constructor
	}

	/**
	 * Authorizations of Equipment.
	 */
	public enum EquipmentAuthorizations implements AuthorizationName {
		/** Gestion des tickets. */
		AtzEquipment$editTickets,
		/** Visibilité pour les membres. */
		AtzEquipment$read,
		/** Visibilité pour les admin.. */
		AtzEquipment$readAll,
		/** Visibilité pour les managers. */
		AtzEquipment$readConf,
		/** Lecture des tickets. */
		AtzEquipment$readTickets,
		/** Edition information. */
		AtzEquipment$write,
		/** Edition information. */
		AtzEquipment$writeAll;

		/**
		 * Get the associated authorization.
		 *
		 * @param code authorization code
		 * @return authorization
		 */
		public static Authorization of(final String code) {
			return Node.getNode().getDefinitionSpace().resolve(code, Authorization.class);
		}

		/**
		 * Get the associated authorization.
		 *
		 * @return role
		 */
		public Authorization getAuthorization() {
			return of(name());
		}
	}

	/**
	 * Operations of Equipment.
	 */
	public enum EquipmentOperations implements OperationName<Equipment> {
		/** Gestion des tickets. */
		editTickets,
		/** Visibilité pour les membres. */
		read,
		/** Visibilité pour les admin.. */
		readAll,
		/** Visibilité pour les managers. */
		readConf,
		/** Lecture des tickets. */
		readTickets,
		/** Edition information. */
		write,
		/** Edition information. */
		writeAll;
	}
	/**
	 * Authorizations of Base.
	 */
	public enum BaseAuthorizations implements AuthorizationName {
		/** Ajout d'équipement à la base. */
		AtzBase$addEqui,
		/** Ajout d'équipement à la base. */
		AtzBase$addEquiAdm,
		/** Create, Archive or delete base. */
		AtzBase$admin,
		/** Visibilité pour les membres. */
		AtzBase$read,
		/** Visibilité pour les admin.. */
		AtzBase$readAll,
		/** Visibilité pour les managers. */
		AtzBase$readConf,
		/** Edition information base. */
		AtzBase$write,
		/** Edition information base. */
		AtzBase$writeAll;

		/**
		 * Get the associated authorization.
		 *
		 * @param code authorization code
		 * @return authorization
		 */
		public static Authorization of(final String code) {
			return Node.getNode().getDefinitionSpace().resolve(code, Authorization.class);
		}

		/**
		 * Get the associated authorization.
		 *
		 * @return role
		 */
		public Authorization getAuthorization() {
			return of(name());
		}
	}

	/**
	 * Operations of Base.
	 */
	public enum BaseOperations implements OperationName<Base> {
		/** Ajout d'équipement à la base. */
		addEqui,
		/** Ajout d'équipement à la base. */
		addEquiAdm,
		/** Create, Archive or delete base. */
		admin,
		/** Visibilité pour les membres. */
		read,
		/** Visibilité pour les admin.. */
		readAll,
		/** Visibilité pour les managers. */
		readConf,
		/** Edition information base. */
		write,
		/** Edition information base. */
		writeAll;
	}
}
