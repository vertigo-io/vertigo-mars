package io.mars.basemanagement.datageneration;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.mars.basemanagement.BasemanagementPAO;
import io.mars.basemanagement.dao.BaseDAO;
import io.mars.basemanagement.dao.PictureDAO;
import io.mars.basemanagement.domain.Base;
import io.mars.basemanagement.domain.Picture;
import io.mars.support.fileinfo.FileInfoStd;
import io.mars.support.smarttypes.GeoPoint;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datastore.filestore.FileStoreManager;
import io.vertigo.datastore.filestore.model.FileInfo;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.impl.filestore.model.StreamFile;

@Transactional
public class BaseGenerator implements Component {

	@Inject
	private BasemanagementPAO basemanagementPAO;

	@Inject
	private BaseDAO baseDAO;
	@Inject
	private PictureDAO pictureDAO;

	@Inject
	private FileStoreManager fileStoreManager;

	public List<Base> generateInitialBases() {
		final List<GeoPoint> geoLocations = Collections.singletonList(new GeoPoint(2.333333, 48.866667)); //paris by default for everybody

		final List<String> nameFirstPartDictionnary1 = List.of("Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Eta", "Theta");
		final List<String> nameSecondPartDictionnary2 = List.of("Aldebaran", "Centauri", "Pisces", "Cygnus", "Pegasus", "Dragon", "Andromeda");
		final List<String> sampleTags = List.of("#mountain", "#crater", "#esa", "#cnsa", "#roscosmos", "#nasa", "#experimental");

		final String exteriorPicturePrefix = "/io/mars/datageneration/files/bases/mars base ";
		final String exteriorPictureSuffix = ".jpg";
		final String interiorPicturePrefix = "/io/mars/datageneration/files/bases/inner base ";
		final String interiorPictureSuffix = ".jpg";

		final FakeBaseListBuilder builder = new FakeBaseListBuilder()
				.withMaxValues(10)
				.withGeosectorIds(basemanagementPAO.selectGeosectorId())
				.withGeoLocations(geoLocations)
				.withNameDictionnaries(nameSecondPartDictionnary2, nameFirstPartDictionnary1)
				.withTagsDictionnary(sampleTags)
				.withPictures(1, exteriorPicturePrefix, exteriorPictureSuffix)
				.withPictures(2, interiorPicturePrefix, interiorPictureSuffix);

		final List<Base> bases = builder.build();
		//base location
		final Base aldebaran = bases.get(0);
		aldebaran.setName("Aldebaran (Paris)");
		aldebaran.setGeoLocation(new GeoPoint(2.333333, 48.866667));
		aldebaran.setCode("Paris-Aldebaran");
		aldebaran.setDescription("The Aldebaran base was the first base of the MMC and managed by Paris."
				+ " The first settlers, led by James T. Kirk, planted the flag in a crater, near a frozen lake.\n"
				+ "The crater is one kilometer deep, which offers a mountain-like landscape.\n"
				+ "The place is rich in minerals, especially in antimony.");
		aldebaran.setTags("#cnes;#crater;#mine");

		final Base centauri = bases.get(1);
		centauri.setName("Centauri (Oslo)");
		centauri.setGeoLocation(new GeoPoint(10.752245399999993, 59.9138688));
		centauri.setCode("Oslo-Centauri");
		centauri.setDescription("The Centauri base is managed by Oslo. "
				+ "The first settlers, led by John König, planted the flag near Utopia Planitia.\n"
				+ "The area is flat and several kilometers long.\n"
				+ "The place is ideal for agriculture, with a soil rich in potassium.");
		centauri.setTags("#esa;#flat;#agriculture");

		final Base pisces = bases.get(2);
		pisces.setName("Pisces (Stockholm)");
		pisces.setGeoLocation(new GeoPoint(18.06216022, 59.3294824));
		pisces.setCode("Stockholm-Pisces");
		pisces.setDescription("The Pisces base is managed by Stockholm. "
				+ "The first settlers, led by Jean-Luc Picard, planted the flag in Olympus Mons.\n"
				+ "The base offers a wonderful landscape around the Olympus Mons which offers a natural watchtower.\n"
				+ "The place is used as a refinery thanks to blast furnaces.");
		pisces.setTags("#esa;#mountain;#refinery");

		final Base cygnus = bases.get(3);
		cygnus.setName("Cygnus (Copenhagen)");
		cygnus.setGeoLocation(new GeoPoint(12.568337, 55.676098));
		cygnus.setCode("Copenhagen-Cygnus");
		cygnus.setDescription("The Cygnus base is managed by Copenhagen. "
				+ "The first settlers, led by Darth Vader, planted the flag in Tharsis.\n"
				+ "The base offers a wonderful maritime landscape and is well-known for its seaside resort and its retirement house\n"
				+ "The place is used as a rest place.");
		cygnus.setTags("#cnes;#seaside;#hollidays");

		final Base dragon = bases.get(4);
		dragon.setName("Dragon (Roma)");
		dragon.setGeoLocation(new GeoPoint(12.48327333, 41.89988));
		dragon.setCode("Roma-Dragon");
		dragon.setDescription("The Dragon base is managed by Roma."
				+ " The first settlers, led by Han Solo, planted the flag in a crater, near a frozen lake.\n"
				+ "The crater is one kilometer deep, which offers a mountain-like landscape.\n"
				+ "The place is rich in minerals, especially in antimony.");
		dragon.setTags("#cnsa;#crater;#mine");

		final Base andromeda = bases.get(5);
		andromeda.setName("Andromeda (London)");
		andromeda.setGeoLocation(new GeoPoint(-0.1255, 51.5084));
		andromeda.setCode("London-Andromeda");
		andromeda.setDescription("The Andromeda base is managed by London. "
				+ "The first settlers, led by Jim Lovell, planted the flag near Utopia Planitia.\n"
				+ "The area is flat and several kilometers long.\n"
				+ "The place is ideal for agriculture, with a soil rich in potassium.");
		andromeda.setTags("#esa;#powerplant;#energy");

		final Base proxima = bases.get(6);
		proxima.setName("Proxima (Barcelona)");
		proxima.setGeoLocation(new GeoPoint(2.166117778, 41.38961111));
		proxima.setCode("Barcelona-Proxima");
		proxima.setDescription("The Proxima base is managed by Barcelona. "
				+ "The first settlers, led by William Adama, planted the flag in Olympus Mons.\n"
				+ "The base offers a wonderful landscape around the Olympus Mons which offers a natural watchtower.\n"
				+ "The place is used as a refinery thanks to blast furnaces.");
		proxima.setTags("#cnsa;#mountain;#gas");

		final Base cassiopeiae = bases.get(7);
		cassiopeiae.setName("Cassiopeiae (Hamburg)");
		cassiopeiae.setGeoLocation(new GeoPoint(10.002914, 53.561012));
		cassiopeiae.setCode("Hamburg-Cassiopeiae");
		cassiopeiae.setDescription("The Cassiopeiae base is managed by Hamburg. "
				+ "The first settlers, led by HAL, planted the flag in Tharsis.\n"
				+ "The base offers a wonderful maritime landscape and is well-known for its seaside resort and its retirement house\n"
				+ "The place is used as a rest place.");
		cassiopeiae.setTags("#roscomos;#maritime;#hollidays");

		final Base persei = bases.get(8);
		persei.setName("Persei (Moscow)");
		persei.setGeoLocation(new GeoPoint(37.619183, 55.757425));
		persei.setCode("Moscow-Persei");
		persei.setDescription("The Persei base is managed by Moscow."
				+ " The first settlers, led by Arthur Dallas, planted the flag in a crater, near a frozen lake.\n"
				+ "The crater is one kilometer deep, which offers a mountain-like landscape.\n"
				+ "The place is rich in minerals, especially in antimony.");
		persei.setTags("#roscosmos;#crater;#mine");

		final Base pegasus = bases.get(9);
		pegasus.setName("Pegasus (Munich)");
		pegasus.setGeoLocation(new GeoPoint(11.576124, 48.137154));
		pegasus.setCode("Munich-Pegasus");
		pegasus.setDescription("The Pegasus base is managed by Munich. "
				+ "The first settlers, led by Ellen Louise Ripley, planted the flag near Utopia Planitia.\n"
				+ "The area is flat and several kilometers long.\n"
				+ "The place is ideal for agriculture, with a soil rich in potassium.");
		pegasus.setTags("#nasa;#flat;#farming");

		int baseIdx = 0;
		for (final Base base : bases) {
			baseDAO.create(base);
			baseIdx++;

			//Add picture

			for (final String picturePath : builder.generatePictures(baseIdx)) {
				final VFile pictureFile = StreamFile.of(
						picturePath.substring(picturePath.lastIndexOf('/') + 1),
						"image/" + picturePath.substring(picturePath.lastIndexOf('.') + 1),
						this.getClass().getResource(picturePath));

				final FileInfo fileInfo = fileStoreManager.create(new FileInfoStd(pictureFile));

				final Picture picture = new Picture();
				picture.setBaseId(base.getBaseId());
				picture.setPicturefileId((Long) fileInfo.getURI().getKey());
				pictureDAO.create(picture);
			}
		}
		return bases;

	}

}
