package io.mars.hr.services.login;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import com.nimbusds.oauth2.sdk.AuthorizationSuccessResponse;

import io.mars.basemanagement.domain.Base;
import io.mars.hr.domain.Mission;
import io.mars.hr.domain.MissionDisplay;
import io.mars.hr.domain.Person;
import io.mars.hr.services.mission.MissionServices;
import io.mars.hr.services.person.PersonServices;
import io.mars.support.MarsUserSession;
import io.vertigo.account.account.Account;
import io.vertigo.account.authentication.AuthenticationManager;
import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.account.authorization.UserAuthorizations;
import io.vertigo.account.authorization.VSecurityException;
import io.vertigo.account.authorization.definitions.Role;
import io.vertigo.account.impl.authentication.UsernameAuthenticationToken;
import io.vertigo.account.impl.authentication.UsernamePasswordAuthenticationToken;
import io.vertigo.account.security.VSecurityManager;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.connectors.oidc.OIDCDeploymentConnector;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.social.notification.Notification;
import io.vertigo.social.notification.NotificationManager;
import io.vertigo.vega.plugins.authentication.oidc.OIDCAppLoginHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Transactional
public class LoginServices implements OIDCAppLoginHandler, Component {

    @Inject
    private AuthenticationManager authenticationManager;
    @Inject
    private VSecurityManager securityManager;
    @Inject
    private AuthorizationManager authorizationManager;
    @Inject
    private NotificationManager notificationManager;
    @Inject
    private PersonServices personServices;
    @Inject
    private MissionServices missionServices;
    @Inject
    Optional<OIDCDeploymentConnector> keycloakDeploymentConnectorOpt;

    @Override
    public String doLogin(final HttpServletRequest request, final Map<String, Object> claims,
            final AuthorizationSuccessResponse authorizationSuccessResponse, final Optional<String> requestedUrl) {
        if (!isAuthenticated()) {
            // we should have a Principal
            loginWithPrincipal(claims, authorizationSuccessResponse);
        }
        return requestedUrl.orElse("/home/");
    }

    @Override
    public String doLogout(final HttpServletRequest request) {
        return "/";
    }

    public void loginWithLoginPassword(final String login, final String password) {
        Assertion.check()
                .isTrue(
                        keycloakDeploymentConnectorOpt.isEmpty(),
                        "Cannot login with local authentication when keycloak is enabled");
        // ---
        final Optional<Account> loggedAccount = authenticationManager
                .login(new UsernamePasswordAuthenticationToken(login, password));
        if (!loggedAccount.isPresent()) {
            sendNotificationToAll(Notification.builder()
                    .withSender("System")
                    .withTitle("Fail login attempt")
                    .withContent("Try to login:'" + login + "")
                    .withTTLInSeconds(600)
                    .withType("MARS-LOGIN-ATTEMP") // should prefix by app, in case of multi-apps notifications
                    .withTargetUrl("#")
                    .build());
            throw new VUserException("Login or Password invalid");
        }
        final Account account = loggedAccount.get();
        final Person person = personServices.getLoggedPerson(Long.valueOf(account.getId()));
        final DtList<MissionDisplay> availableProfiles = missionServices.getMissionsByPersonId(person.getPersonId());
        getUserSession().setLoggedPerson(person);
        getUserSession().setAvailableProfiles(availableProfiles);
        changeProfile(availableProfiles.get(0).getMissionId());

        sendNotificationToAll(Notification.builder()
                .withSender(account.getDisplayName())
                .withTitle("New login")
                .withContent("User " + account.getDisplayName() + " just login")
                .withTTLInSeconds(600)
                .withType("MARS-LOGIN") // should prefix by app, in case of multi-apps notifications
                .withTargetUrl("/mars/hr/person/" + account.getId())
                .build());

    }

    private void loginWithPrincipal(final Map<String, Object> claims,
            final AuthorizationSuccessResponse authorizationSuccessResponse) {
        final String email = (String) claims.get("email");
        final String firstName = (String) claims.get("given_name");
        final String lastName = (String) claims.get("family_name");
        final Account loggedAccount = authenticationManager.login(new UsernameAuthenticationToken(email)).orElseGet(
                () -> {
                    // auto provisionning an account when using keycloak
                    final Person newPerson = personServices.initPerson();
                    newPerson.setDateHired(LocalDate.now());
                    newPerson.setEmail(email);
                    newPerson.setFirstName(firstName);
                    newPerson.setLastName(lastName);
                    personServices.createPerson(newPerson);
                    return authenticationManager.login(new UsernameAuthenticationToken(email)).get();

                });
        final Person person = personServices.getLoggedPerson(Long.valueOf(loggedAccount.getId()));
        final DtList<MissionDisplay> availableProfiles = missionServices.getMissionsByPersonId(person.getPersonId());
        getUserSession().setLoggedPerson(person);
        getUserSession().setAvailableProfiles(availableProfiles);
        changeProfile(availableProfiles.get(0).getMissionId());
    }

    private void sendNotificationToAll(final Notification notification) {
        notificationManager.send(notification, personServices.getAllPersonsUID());
    }

    public boolean isAuthenticated() {
        final Optional<MarsUserSession> userSession = securityManager.<MarsUserSession>getCurrentUserSession();
        return !userSession.isPresent() ? false : userSession.get().isAuthenticated();
    }

    public Person getLoggedPerson() {
        return getUserSession().getLoggedPerson();
    }

    public String logout(final HttpSession httpSession) {
        if (keycloakDeploymentConnectorOpt.isPresent()) {
            return "/OIDC/logout";
        } else {
            httpSession.invalidate();
            return "/";

        }
    }

    public DtList<MissionDisplay> getAvailableProfiles() {
        return getUserSession().getAvailableProfiles();
    }

    public MissionDisplay changeProfile(final long profileId) {
        // may check profile is available is IPD (don't trust session for ever)
        final MissionDisplay activeProfile = getUserSession().getAvailableProfiles().stream()
                .filter(m -> m.getMissionId() == profileId)
                .findFirst().get();
        getUserSession().setCurrentProfile(activeProfile);
        final Mission mission = missionServices.get(activeProfile.getMissionId());

        final UserAuthorizations userAuthorizations = authorizationManager.obtainUserAuthorizations()
                .clearRoles()
                .clearSecurityKeys()
                .addRole(getRole(mission.getRoleId()))
                .withSecurityKeys("baseId", mission.getBaseId())
                .withSecurityKeys("personId", getUserSession().getLoggedPerson().getPersonId());
        if (mission.getBaseId() != null) {
            mission.base().load();
            final Base base = mission.base().get();
            userAuthorizations.withSecurityKeys("assetsValue", base.getAssetsValue());
        }
        return activeProfile;
    }

    private Role getRole(final String roleId) {
        final DefinitionSpace definitionSpace = Node.getNode().getDefinitionSpace();
        return definitionSpace.resolve("R" + roleId, Role.class);
    }

    public MissionDisplay getActiveProfile() {
        return getUserSession().getCurrentProfile();
    }

    private MarsUserSession getUserSession() {
        return securityManager.<MarsUserSession>getCurrentUserSession()
                .orElseThrow(() -> new VSecurityException(LocaleMessageText.of("No active session found")));
    }

}
