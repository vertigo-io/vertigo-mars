package io.mars.basemanagement.datageneration;

import javax.inject.Inject;

import io.mars.basemanagement.dao.BusinessDAO;
import io.mars.basemanagement.dao.GeosectorDAO;
import io.mars.basemanagement.domain.Business;
import io.mars.basemanagement.domain.Geosector;
import io.mars.support.util.CSVReaderUtil;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.resource.ResourceManager;

@Transactional
public class ReferenceDataGenerator implements Component {

	private static final int BUSINESS_CSV_FILE_COLUMN_NUMBER = 2;
	private static final int GEOSECTOR_CSV_FILE_COLUMN_NUMBER = 1;

	@Inject
	private GeosectorDAO geosectorDAO;
	@Inject
	private BusinessDAO businessDAO;
	@Inject
	private ResourceManager resourceManager;

	public void generateReferenceData(final String resourceRoot) {
		CSVReaderUtil.parseCSV(resourceManager, resourceRoot + "geosectors.csv", this::createInitialGeosectorFromCSV);
		CSVReaderUtil.parseCSV(resourceManager, resourceRoot + "businesses.csv", this::createInitialBusinessFromCSV);
	}

	private void createInitialBusinessFromCSV(final String csvFilePath, final String[] businessRecord) {
		Assertion.check().isTrue(businessRecord.length == BUSINESS_CSV_FILE_COLUMN_NUMBER, "CSV File {0} Format not suitable for Equipment Types", csvFilePath);
		// ---
		final String businessName = businessRecord[0];
		final String businessIcon = businessRecord[1];
		businessDAO.create(createBusiness(businessName, businessIcon));
	}

	private void createInitialGeosectorFromCSV(final String csvFilePath, final String[] geoSectorRecord) {
		Assertion.check().isTrue(geoSectorRecord.length == GEOSECTOR_CSV_FILE_COLUMN_NUMBER, "CSV File {0} Format not suitable for Equipment Types", csvFilePath);
		// ---
		final String geosectorName = geoSectorRecord[0];
		geosectorDAO.create(createGeosector(geosectorName));
	}

	private static Business createBusiness(final String businessName, final String businessIcon) {
		final Business business = new Business();
		business.setName(businessName);
		business.setIcon(businessIcon);
		return business;
	}

	private static Geosector createGeosector(final String geosectorName) {
		final Geosector geosector = new Geosector();
		geosector.setSectorLabel(geosectorName);
		return geosector;
	}
}
