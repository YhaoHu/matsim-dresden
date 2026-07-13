package org.matsim.run.scenarios;

import org.jspecify.annotations.Nullable;
import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Scenario;
import org.matsim.application.MATSimApplication;
import org.matsim.contrib.roadpricing.RoadPricingModule;
import org.matsim.contrib.roadpricing.RoadPricingSchemeImpl;
import org.matsim.contrib.roadpricing.RoadPricingUtils;
import org.matsim.core.config.Config;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.utils.gis.shp2matsim.ShpGeometryUtils;

import java.util.List;

public class DresdenScenarioRoadPricing extends DresdenScenario {
	public static void main(String[] args) {
		MATSimApplication.execute(DresdenScenarioRoadPricing.class, args);
	}

	@Override
	protected @Nullable Config prepareConfig(Config config) {
		simwrapper = false;
		super.prepareConfig(config);
		config.controller().setLastIteration(0);
		config.controller().setOutputDirectory("output/road-pricing");
		config.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
		return config;
	}

	@Override
	protected void prepareScenario(Scenario scenario) {
		super.prepareScenario(scenario);
		RoadPricingSchemeImpl roadPricingScheme = RoadPricingUtils.addOrGetMutableRoadPricingScheme(scenario);

		List<Geometry> geometries = ShpGeometryUtils.loadGeometries(IOUtils.getFileUrl("input/v1.0/vvo_tarifzone_10_dresden/v1.0_vvo_tarifzone_10_dresden_utm32n.shp"));

		RoadPricingUtils.createAndAddGeneralCost(roadPricingScheme, 0, 36 * 3600, 1.0);
		RoadPricingUtils.setType(roadPricingScheme, RoadPricingSchemeImpl.TOLL_TYPE_LINK);
	}

	@Override
	protected void prepareControler(Controler controler) {
		super.prepareControler(controler);
		controler.addOverridingModule(new RoadPricingModule());
	}
}
