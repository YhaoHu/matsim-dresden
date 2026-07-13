package org.matsim.run.scenarios;

import org.jspecify.annotations.Nullable;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.application.MATSimApplication;
import org.matsim.core.config.Config;
import org.matsim.core.controler.Controler;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.Set;

public class DresdenRemoveLinkPolicy extends DresdenScenario {
	public static void main(String[] args) {
		MATSimApplication.execute(DresdenRemoveLinkPolicy.class, "--config", "input/v1.0/dresden-v1.0-1pct.config.xml", "--no-simwrapper");
	}

	@Override
	protected @Nullable Config prepareConfig(Config config) {
		super.prepareConfig(config);

		// your modifications come here
		config.controller().setLastIteration(0);
		config.controller().setOutputDirectory("output-policy-remove");
		return config;
	}

	@Override
	protected void prepareScenario(Scenario scenario) {
		super.prepareScenario(scenario);

		// your modifications come here
		Set<String> removeSet = Set.of("318199257", "31059226");
		for (String linkId : removeSet) {
			scenario.getNetwork().getLinks().get(Id.createLinkId(linkId)).setAllowedModes(Set.of());
		}
		ScenarioUtils.cleanScenario(scenario);

//		ScenarioUtils.cleanScenario(scenario);
	}

	@Override
	protected void prepareControler(Controler controler) {
		super.prepareControler(controler);
	}
}
