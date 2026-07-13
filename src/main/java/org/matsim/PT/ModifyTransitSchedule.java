package org.matsim.PT;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.VspExperimentalConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.population.routes.RouteUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.TransitScheduleReaderV2;
import org.matsim.pt.transitSchedule.api.*;
import org.matsim.vehicles.MatsimVehicleReader;
import org.matsim.vehicles.MatsimVehicleWriter;
import org.matsim.vehicles.VehicleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Skeleton class to be extended in the pt exercise
 */
public class ModifyTransitSchedule {

	public static void main(String[] args) {
		// Read current transit related files. Loading the scenario instead needs extensive config settings.
		Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		MatsimNetworkReader networkReader = new MatsimNetworkReader(scenario.getNetwork());
		networkReader.readFile("https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/dresden/dresden-v1.0/input/dresden-v1.0-network-with-pt.xml.gz");
		TransitScheduleReaderV2 transitScheduleReader = new TransitScheduleReaderV2(scenario.getTransitSchedule(), scenario.getPopulation().getFactory().getRouteFactories());
		transitScheduleReader.readFile("https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/dresden/dresden-v1.0/input/dresden-v1.0-transitSchedule.xml.gz");
		MatsimVehicleReader vehicleReader = new MatsimVehicleReader(scenario.getTransitVehicles());
		vehicleReader.readFile("https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/dresden/dresden-v1.0/input/dresden-v1.0-transitVehicles.xml.gz");

		VehicleType vehicleType = scenario.getVehicles().getFactory().createVehicleType(Id.create("cablecar", VehicleType.class));
		vehicleType.setLength(1.0d);
		vehicleType.setNetworkMode("cablecar");
		vehicleType.getCapacity().setSeats(40);
		scenario.getTransitVehicles().addVehicleType(vehicleType);

		addCablecarRoute1(scenario);

		NetworkUtils.writeNetwork(scenario.getNetwork(), "input/v1.0/dresden-v1.0-1pct.network_cablecar.xml.gz");
		(new TransitScheduleWriter(scenario.getTransitSchedule())).writeFile("input/v1.0/dresden-v1.0-1pct.transitSchedule_cablecar.xml.gz");
		(new MatsimVehicleWriter(scenario.getTransitVehicles())).writeFile("input/v1.0/dresden-v1.0-1pct.transitVehicles_cablecar.xml.gz");

		// Test
		Controler controler = new Controler(scenario);
		controler.getConfig().transit().setUseTransit(true);
		controler.getConfig().controller().setOutputDirectory("output/output-dresden-1pct-cablecar");
		controler.getConfig().controller().setLastIteration(1);
		controler.getConfig().controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
		controler.run();
	}

	private static void addCablecarRoute1(Scenario scenario) {
		// First add new Nodes and Links to the network
		// This script re-uses existing nodes to keep it simple, but it can be more appropriate to create new nodes

		double cablecarFreespeed = 10;
		// The first stop is usually on a loop link
		Node stop1Node = scenario.getNetwork().getNodes().get(Id.createNodeId("pt_regio_187185"));
		Node stop2Node = scenario.getNetwork().getNodes().get(Id.createNodeId("pt_regio_185482"));
		Node stop3Node = scenario.getNetwork().getNodes().get(Id.createNodeId("pt_regio_378502"));
		Link cablecarStop1LoopLink = scenario.getNetwork().getFactory().createLink(
			Id.createLinkId("cablecarStop1LoopLink"),
			stop1Node,
			stop1Node);
		cablecarStop1LoopLink.setLength(10);
		cablecarStop1LoopLink.setFreespeed(cablecarFreespeed);
		cablecarStop1LoopLink.setCapacity(100000.0);
		cablecarStop1LoopLink.setAllowedModes(Set.of("cablecar"));
		scenario.getNetwork().addLink(cablecarStop1LoopLink);
		TransitStopFacility cablecarStop1onLoop = scenario.getTransitSchedule().getFactory().createTransitStopFacility(
			Id.create("cablecarStop1onLoop", TransitStopFacility.class),
			stop1Node.getCoord(),
			false);
		cablecarStop1onLoop.setLinkId(cablecarStop1LoopLink.getId());
		cablecarStop1onLoop.setName("Dresden Altpieschen Cablecar");
		scenario.getTransitSchedule().addStopFacility(cablecarStop1onLoop);

		Link cablecarLinkStop2ComingFromStop1 = scenario.getNetwork().getFactory().createLink(
			Id.createLinkId("cablecarLinkStop2ComingFromStop1"),
			stop1Node,
			stop2Node);
		cablecarLinkStop2ComingFromStop1.setLength(NetworkUtils.getEuclideanDistance(stop1Node.getCoord(), stop2Node.getCoord()));
		cablecarLinkStop2ComingFromStop1.setFreespeed(cablecarFreespeed);
		cablecarLinkStop2ComingFromStop1.setCapacity(100000.0);
		cablecarLinkStop2ComingFromStop1.setAllowedModes(Set.of("cablecar"));
		scenario.getNetwork().addLink(cablecarLinkStop2ComingFromStop1);
		TransitStopFacility cablecarStop2ComingFromStop1 = scenario.getTransitSchedule().getFactory().createTransitStopFacility(
			Id.create("cablecarStop2ComingFromStop1", TransitStopFacility.class),
			scenario.getNetwork().getNodes().get(Id.createNodeId("pt_regio_185482")).getCoord(),
			false);
		cablecarStop2ComingFromStop1.setLinkId(cablecarLinkStop2ComingFromStop1.getId());
		cablecarStop2ComingFromStop1.setName("Dresden Waltherstraße Cablecar");
		scenario.getTransitSchedule().addStopFacility(cablecarStop2ComingFromStop1);

		Link cablecarLinkStop3ComingFromStop2 = scenario.getNetwork().getFactory().createLink(
			Id.createLinkId("cablecarLinkStop3ComingFromStop2"),
			stop2Node,
			stop3Node);
		cablecarLinkStop3ComingFromStop2.setLength(NetworkUtils.getEuclideanDistance(stop2Node.getCoord(), stop3Node.getCoord()));
		cablecarLinkStop3ComingFromStop2.setFreespeed(cablecarFreespeed);
		cablecarLinkStop3ComingFromStop2.setCapacity(100000.0);
		cablecarLinkStop3ComingFromStop2.setAllowedModes(Set.of("cablecar"));
		scenario.getNetwork().addLink(cablecarLinkStop3ComingFromStop2);
		TransitStopFacility cablecarStop3ComingFromStop2 = scenario.getTransitSchedule().getFactory().createTransitStopFacility(
			Id.create("cablecarStop3ComingFromStop2", TransitStopFacility.class),
			stop3Node.getCoord(),
			false);
		cablecarStop3ComingFromStop2.setLinkId(cablecarLinkStop3ComingFromStop2.getId());
		cablecarStop3ComingFromStop2.setName("Dresden Tharandter Straße Cablecar");
		scenario.getTransitSchedule().addStopFacility(cablecarStop3ComingFromStop2);

		TransitLine transitLine = scenario.getTransitSchedule().getFactory().createTransitLine(Id.create("cablecar", TransitLine.class));
		transitLine.setName("cablecar");
		NetworkRoute networkRoute = RouteUtils.createLinkNetworkRouteImpl(
			cablecarStop1LoopLink.getId(),
			List.of(cablecarLinkStop2ComingFromStop1.getId()),
			cablecarLinkStop3ComingFromStop2.getId());

		double stopTime = 10.0;
		List<TransitRouteStop> stops = new ArrayList<>();
		stops.add(scenario.getTransitSchedule().getFactory().createTransitRouteStop(
			cablecarStop1onLoop,
			0.0d,
			stopTime));

		double travelTimeStop1ToStop2 = NetworkUtils.getEuclideanDistance(stop1Node.getCoord(), stop2Node.getCoord()) / cablecarFreespeed + 1;
		stops.add(scenario.getTransitSchedule().getFactory().createTransitRouteStop(
			cablecarStop2ComingFromStop1,
			stops.getLast().getDepartureOffset().seconds() + travelTimeStop1ToStop2,
			stops.getLast().getDepartureOffset().seconds() + travelTimeStop1ToStop2 + stopTime));

		double travelTimeStop2ToStop3 = NetworkUtils.getEuclideanDistance(stop2Node.getCoord(), stop3Node.getCoord()) / cablecarFreespeed + 1;
		stops.add(scenario.getTransitSchedule().getFactory().createTransitRouteStop(
			cablecarStop3ComingFromStop2,
			stops.getLast().getDepartureOffset().seconds() + travelTimeStop2ToStop3,
			stops.getLast().getDepartureOffset().seconds() + travelTimeStop2ToStop3 + stopTime));

		stops.forEach(stop -> stop.setAwaitDepartureTime(true));

		TransitRoute transitRoute = scenario.getTransitSchedule().getFactory().createTransitRoute(
			Id.create("Cablecar1", TransitRoute.class),
			networkRoute,
			stops,
			"");
		transitRoute.setTransportMode("cablecar");

		int headway = 5 * 60;
		for (int i = 0; i < 30*60*60 / headway; i++) {
			Departure departure = scenario.getTransitSchedule().getFactory().createDeparture(Id.create("cablecar_1_" + i, Departure.class), i * headway);
			scenario.getTransitVehicles().addVehicle(
				scenario.getTransitVehicles().getFactory().createVehicle(
					Id.createVehicleId("cablecar_1_" + i),
					scenario.getTransitVehicles().getVehicleTypes().get(Id.create("cablecar", VehicleType.class))));
			departure.setVehicleId(Id.createVehicleId("cablecar_1_" + i));
			transitRoute.addDeparture(departure);
		}

		transitLine.addRoute(transitRoute);
		scenario.getTransitSchedule().addTransitLine(transitLine);
	}
}
