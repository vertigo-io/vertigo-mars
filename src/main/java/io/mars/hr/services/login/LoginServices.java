package io.mars.hr.services.login;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.keycloak.KeycloakPrincipal;

import io.mars.hr.domain.Person;
import io.mars.hr.services.person.PersonServices;
import io.mars.support.MarsUserSession;
import io.vertigo.account.account.Account;
import io.vertigo.account.authentication.AuthenticationManager;
import io.vertigo.account.authorization.VSecurityException;
import io.vertigo.account.impl.authentication.UsernameAuthenticationToken;
import io.vertigo.account.impl.authentication.UsernamePasswordAuthenticationToken;
import io.vertigo.account.security.VSecurityManager;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.connectors.keycloak.KeycloakDeploymentConnector;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.Tuple;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.lang.WrappedException;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.social.notification.Notification;
import io.vertigo.social.notification.NotificationManager;
import io.vertigo.vega.impl.servlet.filter.AbstactKeycloakDelegateAuthenticationHandler;

@Transactional
public class LoginServices extends AbstactKeycloakDelegateAuthenticationHandler implements Component, Activeable {

	@Inject
	private AuthenticationManager authenticationManager;
	@Inject
	private VSecurityManager securityManager;
	@Inject
	private NotificationManager notificationManager;
	@Inject
	private PersonServices personServices;
	@Inject
	private List<KeycloakDeploymentConnector> keycloakDeploymentConnectors;

	private boolean keycloakEnabled = false;

	@Override
	public void start() {
		Assertion.check().isNotNull(keycloakDeploymentConnectors);
		//---
		final Optional<KeycloakDeploymentConnector> keycloakDeploymentConnectorOpt = keycloakDeploymentConnectors.stream().filter(connector -> "main".equals(connector.getName())).findFirst();
		if (keycloakDeploymentConnectorOpt.isPresent()) {
			keycloakEnabled = true;
			init(keycloakDeploymentConnectorOpt.get());
		} else {
			keycloakEnabled = false;
		}

	}

	@Override
	public void stop() {
		// nothing

	}

	@Override
	public Tuple<Boolean, HttpServletRequest> doBeforeChain(final HttpServletRequest request, final HttpServletResponse response) {
		if (keycloakEnabled) {
			return super.doBeforeChain(request, response);
		}
		return Tuple.of(false, request);
	}

	@Override
	public boolean doLogin(final HttpServletRequest request, final HttpServletResponse response) {
		if (!isAuthenticated()) {
			// we should have a Principal
			final KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) request.getUserPrincipal();
			final String email = keycloakPrincipal.getKeycloakSecurityContext().getIdToken().getEmail();
			loginWithPrincipal(email);
			try {
				response.sendRedirect(request.getContextPath() + "/home/");
			} catch (final IOException e) {
				throw WrappedException.wrap(e);
			}
			//consumed by redirect
			return true;
		}
		//not consumed
		return false;
	}

	public void loginWithLoginPassword(final String login, final String password) {
		final Optional<Account> loggedAccount = authenticationManager.login(new UsernamePasswordAuthenticationToken(login, password));
		if (!loggedAccount.isPresent()) {
			sendNotificationToAll(Notification.builder()
					.withSender("System")
					.withTitle("Fail login attempt")
					.withContent("Try to login:'" + login + "")
					.withTTLInSeconds(600)
					.withType("MARS-LOGIN-ATTEMP") //should prefix by app, in case of multi-apps notifications
					.withTargetUrl("#")
					.build());
			throw new VUserException("Login or Password invalid");
		}
		final Account account = loggedAccount.get();
		final Person person = personServices.getPerson(Long.valueOf(account.getId()));
		getUserSession().setLoggedPerson(person);
		getUserSession().setCurrentProfile("Administrator");

		sendNotificationToAll(Notification.builder()
				.withSender(account.getDisplayName())
				.withTitle("New login")
				.withContent("User " + account.getDisplayName() + " just login")
				.withTTLInSeconds(600)
				.withType("MARS-LOGIN") //should prefix by app, in case of multi-apps notifications
				.withTargetUrl("/mars/hr/person/" + account.getId())
				.build());

	}

	private void loginWithPrincipal(final String login) {
		final Optional<Account> loggedAccount = authenticationManager.login(new UsernameAuthenticationToken(login));
		if (!loggedAccount.isPresent()) {
			throw new VUserException("Login invalid");
		}
		final Account account = loggedAccount.get();
		final Person person = personServices.getPerson(Long.valueOf(account.getId()));
		getUserSession().setLoggedPerson(person);
		getUserSession().setCurrentProfile("Administrator");

	}

	private void sendNotificationToAll(final Notification notification) {
		final Set<UID<Account>> accountUIDs = personServices.getPersons(DtListState.of(null))
				.stream()
				.map((person) -> UID.of(Account.class, String.valueOf(person.getPersonId())))
				.collect(Collectors.toSet());
		notificationManager.send(notification, accountUIDs);
	}

	public boolean isAuthenticated() {
		final Optional<MarsUserSession> userSession = securityManager.<MarsUserSession> getCurrentUserSession();
		return !userSession.isPresent() ? false : userSession.get().isAuthenticated();
	}

	public Person getLoggedPerson() {
		return getUserSession().getLoggedPerson();
	}

	public void logout(final HttpSession httpSession) {
		authenticationManager.logout();
		httpSession.invalidate();
	}

	public String changeProfile(final String profile) {
		//TODO
		getUserSession().setCurrentProfile(profile);
		return profile;
	}

	public String getActiveProfile() {
		return getUserSession().getCurrentProfile();
	}

	private MarsUserSession getUserSession() {
		return securityManager.<MarsUserSession> getCurrentUserSession().orElseThrow(() -> new VSecurityException(MessageText.of("No active session found")));
	}

}
