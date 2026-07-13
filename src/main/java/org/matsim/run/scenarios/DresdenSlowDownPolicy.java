package org.matsim.run.scenarios;

import org.jspecify.annotations.Nullable;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.application.MATSimApplication;
import org.matsim.core.config.Config;
import org.matsim.core.controler.Controler;

import java.util.Set;

public class DresdenSlowDownPolicy extends DresdenScenario {
	public static void main(String[] args) {
		MATSimApplication.execute(DresdenSlowDownPolicy.class, args);
	}

	@Override
	protected @Nullable Config prepareConfig(Config config) {
		super.prepareConfig(config);
		simwrapper = false;

		// your modifications come here
		config.controller().setLastIteration(0);
		config.controller().setOutputDirectory("output-policy");
		return config;
	}

	@Override
	protected void prepareScenario(Scenario scenario) {
		super.prepareScenario(scenario);

		// your modifications come here
		Set<String> linksToChange = Set.of( "318199257", "31059226");
		for (String linkId : linksToChange) {
			Link link = scenario.getNetwork().getLinks().get(Id.createLinkId(linkId));
			link.setFreespeed(1.);
			link.setCapacity(500);
		}

	}

	@Override
	protected void prepareControler(Controler controler) {
		super.prepareControler(controler);
	}
}
