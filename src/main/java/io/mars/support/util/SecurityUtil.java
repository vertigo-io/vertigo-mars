package io.mars.support.util;

import io.vertigo.account.authorization.VSecurityException;
import io.vertigo.account.security.UserSession;
import io.vertigo.account.security.VSecurityManager;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.core.node.Node;

public final class SecurityUtil {

	public static <U extends UserSession> U getUserSession() {
		final VSecurityManager securityManager = Node.getNode().getComponentSpace().resolve(VSecurityManager.class);
		return securityManager.<U> getCurrentUserSession().orElseThrow(() -> new VSecurityException(LocaleMessageText.of("No active session found")));
	}

}
