package org.matsim.run.scenarios;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.roadpricing.RoadPricingModule;
import org.matsim.contrib.roadpricing.RoadPricingScheme;
import org.matsim.contrib.roadpricing.RoadPricingSchemeImpl;
import org.matsim.contrib.roadpricing.RoadPricingUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controller;
import org.matsim.core.controler.ControllerUtils;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;

public class RunWithRoadPricingFromCode {
    static void main() {
        Config config = ConfigUtils.loadConfig("scenarios/equil/config-2026.xml");
        config.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        config.controller().setLastIteration(0);
        config.plans().setInputFile("plans2000_clean.xml.gz");

        Scenario scenario = ScenarioUtils.loadScenario(config);

        RoadPricingSchemeImpl roadPricingScheme = RoadPricingUtils.addOrGetMutableRoadPricingScheme(scenario);
        RoadPricingUtils.setType(roadPricingScheme, RoadPricingScheme.TOLL_TYPE_LINK);

        RoadPricingUtils.addLink(roadPricingScheme, Id.createLinkId("11"));
        RoadPricingUtils.createAndAddGeneralCost(roadPricingScheme, 0, 36*3600, 1.0);

        Controller controller = ControllerUtils.createController(scenario);
        controller.addOverridingModule(new RoadPricingModule());
        controller.run();
    }
}
