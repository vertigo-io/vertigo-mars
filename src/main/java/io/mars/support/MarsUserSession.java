/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2013-2017, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiere - BP 159 - 92357 Le Plessis Robinson Cedex - France
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mars.support;

import java.util.Locale;

import io.mars.hr.domain.MissionDisplay;
import io.mars.hr.domain.Person;
import io.vertigo.account.account.Account;
import io.vertigo.account.authentication.AuthenticationManager;
import io.vertigo.account.authorization.VSecurityException;
import io.vertigo.account.security.UserSession;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.data.model.DtList;

/**
 * UserSession.
 *
 * @author mlaroche.
 * @version $Id$
 */
public class MarsUserSession extends UserSession {

	private static final long serialVersionUID = 1782541593145943505L;
	private Person loggedPerson;
	private DtList<MissionDisplay> availableProfiles;
	private MissionDisplay currentProfile;

	/** {@inheritDoc} */
	@Override
	public Locale getLocale() {
		return Locale.US;
	}

	public final Person getLoggedPerson() {
		return loggedPerson;
	}

	public final void setLoggedPerson(final Person loggedPerson) {
		this.loggedPerson = loggedPerson;
	}

	public final MissionDisplay getCurrentProfile() {
		return currentProfile;
	}

	public final void setCurrentProfile(final MissionDisplay currentProfile) {
		this.currentProfile = currentProfile;
	}

	public final Account getLoggedAccount() {
		return Node.getNode().getComponentSpace().resolve(AuthenticationManager.class).getLoggedAccount()
				.orElseThrow(() -> new VSecurityException(LocaleMessageText.of("No account logged in")));
	}

	public final DtList<MissionDisplay> getAvailableProfiles() {
		return availableProfiles;
	}

	public void setAvailableProfiles(final DtList<MissionDisplay> availableProfiles) {
		this.availableProfiles = availableProfiles;
	}

}
