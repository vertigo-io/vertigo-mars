/**
 *
 */
package io.mars.support.boot;

import javax.inject.Inject;

import io.mars.support.easyforms.MarsEasyFormsResources;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.node.component.ComponentInitializer;

/**
 * Init ressources.
 *
 * @author npiedeloup
 */
public class I18nResourcesInitializer implements ComponentInitializer {

	@Inject
	private LocaleManager localeManager;

	/** {@inheritDoc} */
	@Override
	public void init() {
		localeManager.add("io.vertigo.basics.constraint.Constraint", io.vertigo.basics.constraint.Resources.values());
		localeManager.add("io.vertigo.basics.formatter.Formatter", io.vertigo.basics.formatter.Resources.values());
		localeManager.add("io.vertigo.account.authorization.Authorization", io.vertigo.account.authorization.Resources.values());

		localeManager.add("io.mars.support.MarsEasyFormsResources", MarsEasyFormsResources.values());
	}
}
