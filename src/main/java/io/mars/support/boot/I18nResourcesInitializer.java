/**
 *
 */
package io.mars.support.boot;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.locale.MessageKey;
import io.vertigo.core.node.component.ComponentInitializer;

/**
 * Init ressources.
 * @author npiedeloup
 */
public class I18nResourcesInitializer implements ComponentInitializer {

	private static final Set<Class<? extends MessageKey>> REGISTRED_RESOURCES = new HashSet<>();

	@Inject
	private LocaleManager localeManager;

	/** {@inheritDoc} */
	@Override
	public void init() {
		registerMessageKey(localeManager, "io.vertigo.basics.constraint.Constraint", io.vertigo.basics.constraint.Resources.class);
		registerMessageKey(localeManager, "io.vertigo.basics.formatter.Formatter", io.vertigo.basics.formatter.Resources.class);
	}

	/**
	 * Ajoute un fichier de ressources au localManager.
	 *
	 * @param component localManager
	 * @param baseName Emplacement fichier de ressources
	 * @param clazz Enum associ� au fichier de ressources
	 */
	private static void registerMessageKey(final LocaleManager component, final String baseName, final Class<? extends MessageKey> clazz) {
		component.add(baseName, clazz.getEnumConstants());
		REGISTRED_RESOURCES.add(clazz);
	}

	/**
	 * Donne la valeur de registredRessources.
	 *
	 * @return la valeur de registredRessources.
	 */
	public static Set<Class<? extends MessageKey>> getRegistredRessources() {
		final Set<Class<? extends MessageKey>> cloneSet = new HashSet<>();
		cloneSet.addAll(REGISTRED_RESOURCES);
		return cloneSet;
	}
}
