package io.mars.catalog.jobs.supplier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Optional;

import javax.inject.Inject;

import io.mars.catalog.domain.Supplier;
import io.mars.catalog.services.supplier.SupplierServices;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import io.vertigo.core.resource.ResourceManager;
import io.vertigo.core.util.DateUtil;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.orchestra.impl.services.execution.AbstractActivityEngine;
import io.vertigo.orchestra.services.execution.ActivityExecutionWorkspace;

public class ParseCSVSuppliersActivityEngine extends AbstractActivityEngine {

	@Inject
	private SupplierServices supplierServices;
	@Inject
	private ResourceManager resourceManager;
	@Inject
	private ParamManager paramManager;

	@Override
	public ActivityExecutionWorkspace execute(final ActivityExecutionWorkspace workspace) {
		final String rootDirectory = "file:///" + paramManager.getParam("orchestra.root.directory").getValueAsString() + "/IMPORT/SIRENE/";
		final String csvFilePath = rootDirectory + "StockUniteLegale_utf8.csv";
		final Optional<Param> supplierLimit = paramManager.getOptionalParam("suppliersImportLimit");
		try (final InputStream inputStream = resourceManager.resolve(csvFilePath).openStream()) {
			Assertion.check().isNotNull(inputStream, "fichier non trouvé : {0}", csvFilePath);
			try (final BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream))) {
				final DtList<Supplier> suppliers = new DtList<>(Supplier.class);
				String line;
				int count = 0;
				line = rd.readLine();// skip first line
				while ((line = rd.readLine()) != null) {
					if (supplierLimit.isPresent() && count >= supplierLimit.get().getValueAsInt()) {
						break;
					}
					count++;
					try {
						suppliers.add(readSupplier(line));
					} catch (final Exception e) {
						// don't care
					}
					if (suppliers.size() > 10000) {
						supplierServices.indexChunk(suppliers);
						suppliers.clear();
					}
				}
				// last chunk
				supplierServices.indexChunk(suppliers);
				suppliers.clear();
			}
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		workspace.setSuccess();
		return workspace;
	}

	private static Supplier readSupplier(final String line) {
		final String[] fields = line.split(",");
		final Supplier supplier = new Supplier();
		if (!StringUtil.isBlank(fields[0])) {
			supplier.setSiren(fields[0]);
		}
		if (!StringUtil.isBlank(fields[1])) {
			supplier.setStatutDiffusion(fields[1]);
		}
		if (!StringUtil.isBlank(fields[3])) {
			supplier.setDateCreation(DateUtil.parseToLocalDate(fields[3], "yyyy-MM-dd"));
		}
		if (!StringUtil.isBlank(fields[5])) {
			supplier.setSexe(fields[5]);
		}
		if (!StringUtil.isBlank(fields[10])) {
			supplier.setPrenomUsuel(fields[10]);
		}
		if (!StringUtil.isBlank(fields[13])) {
			supplier.setTrancheEffectifs(fields[13]);
		}
		if (!StringUtil.isBlank(fields[15])) {
			supplier.setDateDernierTraitement(Instant.parse(fields[15] + "Z"));
		}
		if (!StringUtil.isBlank(fields[16])) {
			supplier.setNombrePeriodes(Long.valueOf(fields[16]));
		}
		supplier.setCategorieEntreprise(fields[17]);
		if (!StringUtil.isBlank(fields[19])) {
			supplier.setDateDebut(DateUtil.parseToLocalDate(fields[19], "yyyy-MM-dd"));
		}
		if (!StringUtil.isBlank(fields[20])) {
			supplier.setEtatAdministratif(fields[20]);
		}
		if (!StringUtil.isBlank(fields[21])) {
			supplier.setNom(fields[21]);
		}
		if (!StringUtil.isBlank(fields[22])) {
			supplier.setNomUsage(fields[22]);
		}
		if (!StringUtil.isBlank(fields[23])) {
			supplier.setDenomination(fields[23]);
		}
		if (!StringUtil.isBlank(fields[27])) {
			supplier.setCategorieJuridique(fields[27]);
		}
		if (!StringUtil.isBlank(fields[28])) {
			supplier.setActivitePrincipale(fields[28]);
		}
		if (!StringUtil.isBlank(fields[29])) {
			supplier.setNomenclatureActivite(fields[29]);
		}
		if (!StringUtil.isBlank(fields[30])) {
			supplier.setNicSiege(Long.valueOf(fields[30]));
		}
		if (!StringUtil.isBlank(fields[32])) {
			supplier.setCaractereEmployeur("O".equals(fields[32]) ? true : false);
		}
		return supplier;
	}

}
