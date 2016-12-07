package io.vertigo.samples.dao.run;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.app.AutoCloseableApp;
import io.vertigo.app.config.AppConfigBuilder;
import io.vertigo.core.component.di.injector.Injector;
import io.vertigo.samples.dao.config.SampleConfigBuilder;
import io.vertigo.samples.dao.dao.ActorDAO;
import io.vertigo.samples.dao.dao.CountryDAO;
import io.vertigo.samples.dao.dao.MovieDAO;
import io.vertigo.samples.dao.dao.MyActorDAO;
import io.vertigo.samples.dao.dao.MyCountryDAO;
import io.vertigo.samples.dao.dao.MyMovieDAO;
import io.vertigo.samples.dao.dao.MyRoleDAO;
import io.vertigo.samples.dao.dao.RoleDAO;
import io.vertigo.samples.dao.services.ActorServices;
import io.vertigo.samples.dao.services.ActorServicesImpl;
import io.vertigo.samples.dao.services.CountryServices;
import io.vertigo.samples.dao.services.CountryServicesImpl;
import io.vertigo.samples.dao.services.MovieServices;
import io.vertigo.samples.dao.services.MovieServicesImpl;
import io.vertigo.samples.dao.services.RepriseServices;
import io.vertigo.samples.dao.services.RepriseServicesImpl;
import io.vertigo.samples.reprise.ReprisePAO;

public class Level7 {

	@Inject
	private RepriseServices repriseServices;

	public static void main(final String[] args) {
		final AppConfigBuilder appConfigBuilder = SampleConfigBuilder.createAppConfigBuilderWithoutCrebase();
		appConfigBuilder.beginModule("mineDAO")
				.withNoAPI()
				.addComponent(MyMovieDAO.class)
				.addComponent(MyActorDAO.class)
				.addComponent(MyRoleDAO.class)
				.addComponent(MyCountryDAO.class)
				.addComponent(MovieDAO.class)
				.addComponent(ActorDAO.class)
				.addComponent(RoleDAO.class)
				.addComponent(CountryDAO.class)
				.addComponent(ReprisePAO.class)
				.endModule()
				.beginModule("mineServices")
				.addComponent(CountryServices.class, CountryServicesImpl.class)
				.addComponent(MovieServices.class, MovieServicesImpl.class)
				.addComponent(ActorServices.class, ActorServicesImpl.class)
				.addComponent(RepriseServices.class, RepriseServicesImpl.class)
				.endModule();
		try (final AutoCloseableApp app = new AutoCloseableApp(appConfigBuilder.build())) {
			final Level7 level7 = new Level7();
			Injector.injectMembers(level7, app.getComponentSpace());
			//-----
			level7.step1();
		}
	}

	void step1() {
		final long chunksize = 1000L;
		Optional<Long> offset = Optional.of(0L);
		while (offset.isPresent()) {
			offset = repriseServices.fillMovies(chunksize, offset.get());

		}
	}

}