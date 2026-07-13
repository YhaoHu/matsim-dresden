package org.matsim.run.scenarios;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.roadpricing.RoadPricingModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controller;
import org.matsim.core.controler.ControllerUtils;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;

public class RunWithRoadPricingFromConfig {
    static void main() {
        Config config = ConfigUtils.loadConfig("scenarios/equil/config-with-roadpricing.xml");
        config.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        config.controller().setLastIteration(0);

        Scenario scenario = ScenarioUtils.loadScenario(config);

        Controller controller = ControllerUtils.createController(scenario);
        controller.addOverridingModule(new RoadPricingModule());


        controller.run();
    }
}
