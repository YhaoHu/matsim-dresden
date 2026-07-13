package org.matsim.run.scenarios;

import org.jspecify.annotations.Nullable;
import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.application.MATSimApplication;
import org.matsim.core.config.Config;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.utils.gis.shp2matsim.ShpGeometryUtils;
import playground.vsp.simpleParkingCostHandler.ParkingCostModule;

import java.util.List;

public class DresdenScenarioParking extends DresdenScenario {

	public static void main(String[] args) {
		MATSimApplication.execute(DresdenScenarioParking.class, args);
	}

	@Override
	protected @Nullable Config prepareConfig(Config config) {

		simwrapper = false;

		super.prepareConfig(config);

		config.controller().setLastIteration(30);
		config.controller().setOutputDirectory("output/parking");
		config.controller().setOverwriteFileSetting(
			OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists
		);

		return config;
	}

	@Override
	protected void prepareScenario(Scenario scenario) {

		super.prepareScenario(scenario);

		List<Geometry> geometries = ShpGeometryUtils.loadGeometries(
			IOUtils.getFileUrl(
				"input/v1.0/vvo_tarifzone_10_dresden/v1.0_vvo_tarifzone_10_dresden_utm32n.shp"
			)
		);

		double parkingCost = 10.0;

		int counter = 0;

		for (Link link : scenario.getNetwork().getLinks().values()) {

			boolean insideParkingArea = geometries.stream()
				.anyMatch(geometry -> geometry.contains(MGC.coord2Point(link.getCoord())));

			if (insideParkingArea) {
				link.getAttributes().putAttribute("pc_car", parkingCost);
				counter++;
			}
		}

		System.out.println("Parking cost added to " + counter + " links.");
	}

	@Override
	protected void prepareControler(Controler controler) {

		super.prepareControler(controler);

		controler.addOverridingModule(new ParkingCostModule());
	}
}
