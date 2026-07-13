package org.matsim.run.scenarios;

import org.jspecify.annotations.Nullable;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.application.MATSimApplication;
import org.matsim.contrib.roadpricing.RoadPricingModule;
import org.matsim.contrib.roadpricing.RoadPricingSchemeImpl;
import org.matsim.contrib.roadpricing.RoadPricingUtils;
import org.matsim.core.config.Config;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;

import java.util.List;

/**
 * Based on the road-pricing example introduced in the exercise.
 *
 * Own implementation:
 * - selection of tolled bridge links in Dresden
 * - definition of toll period and toll amount
 * - configuration and validation of the policy run
 */
public class HARoadPricingRunner extends DresdenScenario {

	public static void main(String[] args) {
		MATSimApplication.execute(
			HARoadPricingRunner.class,
			args
		);
	}

	@Override
	protected @Nullable Config prepareConfig(Config config) {
		simwrapper = false;
		config = super.prepareConfig(config);

		config.controller().setLastIteration(0);
		config.controller().setRunId("road-pricing");
		config.controller().setOutputDirectory(
			"output/road-pricing"
		);

		config.controller().setOverwriteFileSetting(
			OutputDirectoryHierarchy
				.OverwriteFileSetting
				.deleteDirectoryIfExists
		);

		return config;
	}

	@Override
	protected void prepareScenario(Scenario scenario) {
		super.prepareScenario(scenario);

		RoadPricingSchemeImpl roadPricingScheme =
			RoadPricingUtils
				.addOrGetMutableRoadPricingScheme(scenario);

		RoadPricingUtils.setType(
			roadPricingScheme,
			RoadPricingSchemeImpl.TOLL_TYPE_LINK
		);

		List<Id<Link>> bridgeLinkIds = List.of(
			Id.createLinkId("425728245"),
			Id.createLinkId("14448952"),
			Id.createLinkId("31059226"),
			Id.createLinkId("318199257"),
			Id.createLinkId("-488766980"),
			Id.createLinkId("761288685"),
			Id.createLinkId("-264360404"),
			Id.createLinkId("1031454500"),
			Id.createLinkId("4214231"),
			Id.createLinkId("901959078"),
			Id.createLinkId("-264360396#1"),
			Id.createLinkId("505502627#0"),
			Id.createLinkId("277710971"),
			Id.createLinkId("132572494")
		);

		for (Id<Link> linkId : bridgeLinkIds) {
			Link link =
				scenario.getNetwork().getLinks().get(linkId);

			if (link == null) {
				throw new IllegalArgumentException(
					"Tolled link does not exist: " + linkId
				);
			}


			RoadPricingUtils.addLink(
				roadPricingScheme,
				linkId
			);
		}

		RoadPricingUtils.createAndAddGeneralCost(
			roadPricingScheme,
			0,
			36 * 3600,
			1.0
		);


	}

	@Override
	protected void prepareControler(Controler controler) {
		super.prepareControler(controler);

		controler.addOverridingModule(
			new RoadPricingModule()
		);
	}
}
