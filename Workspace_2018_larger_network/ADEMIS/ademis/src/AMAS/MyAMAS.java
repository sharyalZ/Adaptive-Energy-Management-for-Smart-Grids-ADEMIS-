package AMAS;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import Interface.PowerFactoryCaller;
import fr.irit.smac.amak.Amas;
import fr.irit.smac.amak.Configuration;
import fr.irit.smac.amak.Scheduler;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.amak.aid.AID;
import ui.MyLog;
import ui.Parameters;
import ui.Results;

public class MyAMAS extends Amas<MyEnvironment> {

	private Map<String, MyAgent> myAgents;
	private List<Load> loads;
	private DateFormat format;
	private GregorianCalendar endTime;

	private double simulationDuration;

	public MyAMAS(MyEnvironment environment, Scheduling scheduling, Object[] params) {
		super(environment, scheduling, params);
		format = new SimpleDateFormat("H:mm:ss");
		endTime = Parameters.getEndTime();
		simulationDuration = System.currentTimeMillis();
		final String dir = System.getProperty("user.dir");
		Path workingPath = Paths.get(dir);
		String communicationPath = workingPath.getParent().getParent().toString();
		Parameters.setCommunicationPath(
				communicationPath + "\\PowerFactory\\CommunicationFiles\\" + Parameters.getGridName());
	}

	@Override
	protected void onInitialConfiguration() {
		super.onInitialConfiguration();
		Configuration.executionPolicy = ExecutionPolicy.TWO_PHASES;

	}

	@Override
	protected void onInitialAgentsCreation() {
//		System.out.println("Agents creation\n");
		myAgents = new HashMap<>();

		///////////////////////////////////////////////////////////////////////////////
		////////////// Test communication between agents /////////////////////
		///////////////////////////////////////////////////////////////////////////////
		/*
		 * MyAgent agent1=new MyAgent(this, "agent1");
		 * 
		 * MyAgent agent2=new MyAgent(this, "agent2");
		 * 
		 * agent1.setOtherAgentAID(agent2.getAID());
		 * agent2.setOtherAgentAID(agent1.getAID());
		 */
		///////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////

		AggregatorAgent aggregatorAgent = new AggregatorAgent(this, "Aggregator");
		myAgents.put("Aggregator", aggregatorAgent);

		List<ProsumerAgent> prosumerAgents = new ArrayList<>();
		busAgentsCreation(aggregatorAgent);
		lineAgentsCreation();
		aidToSourceDefinition();
		evAgentsCreation(prosumerAgents);
		pvAgentsCreation(prosumerAgents);

		prosumersNeighbourhood(prosumerAgents);

		///// Loads : not agents, active entites

		List<String> loadsPath = PowerFactoryCaller.GetLoads();
		loads = new ArrayList<>();
		for(int i = 0; i < loads.size(); i++) {   
		    System.out.print("\n"+loads.get(i));
		}  
		for (String load : loadsPath) {
			loads.add(new Load(load, this.environment.getCurrentTime(), aggregatorAgent));
			System.out.println("Loadagentcreartion-->"+load);

		}
		
	}

	private void prosumersNeighbourhood(List<ProsumerAgent> prosumerAgents) {
		boolean first = true;
		ProsumerAgent previousProsumer = null, firstProsumer = null;
		for (ProsumerAgent prosumerAgent : prosumerAgents) {
			if (first) {
				first = false;
				previousProsumer = prosumerAgent;
				firstProsumer = prosumerAgent;
			} else {
				prosumerAgent.setNeighborAID1(previousProsumer.getAID());
				previousProsumer.setNeighborAID2(prosumerAgent.getAID());
				previousProsumer = prosumerAgent;
			}
			prosumerAgent.setNeighborAID2(firstProsumer.getAID());
			firstProsumer.setNeighborAID1(prosumerAgent.getAID());
		}
	}

	private void busAgentsCreation(AggregatorAgent aggregatorAgent) {
		List<String> buses;	  
		buses = PowerFactoryCaller.GetBuses();

	
		for (String bus : buses) {
			String busName = PowerFactoryCaller.GetComplete1Param(bus, "loc_name");    //issue exist in this part. The bus name is being returned null from the python through the GetComplete1Param method in python and java
			if(busName == null) {
				//System.out.println("	Error");
			}else {
				myAgents.put(busName, new BusAgent(this, aggregatorAgent, bus, busName));
			}
			

		}
	}

	private Map<BusAgent, List<LineAgent>> linesConnectedToBuses;

	private void lineAgentsCreation() {
		linesConnectedToBuses = new HashMap<>();
		List<String> lines;
		
		//to check the number of lines
		
		lines = PowerFactoryCaller.GetLines();
		for(int i = 0; i < lines.size(); i++) {   
		    System.out.print("\n"+lines.get(i));
		} 
		
		
		
		/*
		 * from the linesPath, it will fetch cubicle path (bus1) and store it in cubPath1--> then from cubicle1  path it will fetch busPath1 (cterm) and store it as busPath1 and the from busPath1 it will fetch bus name (loc_name)
		 */
		for (String linePath : lines) {
			String cubPath1 = null;
			String busPath1 = null;
			String bus1 = null;
			String cubPath2 = null;
			String busPath2 = null;
			String bus2 = null;
			String lineName = null;
			if (linePath.matches("(.*)ElmLne(.*)")){
			//System.out.println("LineName-->"+lines);
			cubPath1 = PowerFactoryCaller.Get1Param(linePath, "bus1");
			busPath1 = PowerFactoryCaller.Get1Param(cubPath1, "cterm");
			bus1 = PowerFactoryCaller.GetComplete1Param(busPath1, "loc_name");
			cubPath2 = PowerFactoryCaller.Get1Param(linePath, "bus2");
			busPath2 = PowerFactoryCaller.Get1Param(cubPath2, "cterm");
			bus2 = PowerFactoryCaller.GetComplete1Param(busPath2, "loc_name");
			 lineName = PowerFactoryCaller.GetComplete1Param(linePath, "loc_name");
			}else {  //this is to recognise transformer as a line, the line is associated with bus1 and bus2 whereas a transformer is associated with buslv and bushv in powerfactory
				cubPath1 = PowerFactoryCaller.Get1Param(linePath, "bushv");
				busPath1 = PowerFactoryCaller.Get1Param(cubPath1, "cterm");
				bus1 = PowerFactoryCaller.GetComplete1Param(busPath1, "loc_name");
				cubPath2 = PowerFactoryCaller.Get1Param(linePath, "buslv");
				busPath2 = PowerFactoryCaller.Get1Param(cubPath2, "cterm");
				bus2 = PowerFactoryCaller.GetComplete1Param(busPath2, "loc_name");
				 lineName = PowerFactoryCaller.GetComplete1Param(linePath, "loc_name");
				
				}
			LineAgent lineAgent = new LineAgent(this, lineName, linePath);
			BusAgent bus1Agent = (BusAgent) myAgents.get(bus1);
			BusAgent bus2Agent = (BusAgent) myAgents.get(bus2);
			lineAgent.setBus1(bus1Agent.getAID(), bus1);
			lineAgent.setBus2(bus2Agent.getAID(), bus2);
			bus1Agent.addLineAID(lineAgent.getAID());
			bus2Agent.addLineAID(lineAgent.getAID());
			myAgents.put(lineName, lineAgent);
			lineAgent.addNeighbor(bus1Agent);
			lineAgent.addNeighbor(bus2Agent);
			bus1Agent.addNeighbor(lineAgent);
			bus2Agent.addNeighbor(lineAgent);

			List<LineAgent> linesList;
			if (linesConnectedToBuses.containsKey(bus1Agent)) {
				linesList = linesConnectedToBuses.get(bus1Agent);
			} else {
				linesList = new ArrayList<>();
				linesConnectedToBuses.put(bus1Agent, linesList);
			}
			linesList.add(lineAgent);

			if (linesConnectedToBuses.containsKey(bus2Agent)) {
				linesList = linesConnectedToBuses.get(bus2Agent);
			} else {
				linesList = new ArrayList<>();
				linesConnectedToBuses.put(bus2Agent, linesList);
			}
			linesList.add(lineAgent);
		}
	}

	private void aidToSourceDefinition() {
		List<String> buses;
		buses = PowerFactoryCaller.GetBuses();
		List<String> lines;
		lines = PowerFactoryCaller.GetLines();
		List<BusAgent> busesToDo = new ArrayList();
		List<LineAgent> LinesDone = new ArrayList();

		String firstBusName = Parameters.getFirstBusName();
		System.out.println("firstBusName : " + firstBusName);

		BusAgent busAgent = (BusAgent) myAgents.get(firstBusName);
		busAgent.setAIDtoSource(null);
		busesToDo.add(busAgent);

		while (!busesToDo.isEmpty()) {
			busAgent = busesToDo.get(0);
			String currentBusName = busAgent.getName();
			AID currentAID = busAgent.getAID();

			busesToDo.remove(0);
			if (linesConnectedToBuses.containsKey(busAgent)) {
				List<LineAgent> myLines = linesConnectedToBuses.get(busAgent);
				for (LineAgent lineAgent : myLines) {
					if (!LinesDone.contains(lineAgent)) {
						LinesDone.add(lineAgent);
						lineAgent.setAIDtoSource(currentAID);
						String busName = lineAgent.getBus1Name();
						if (busName.equals(currentBusName)) {
							busName = lineAgent.getBus2Name();
						}
						busesToDo.add((BusAgent) myAgents.get(busName));
						BusAgent myBusAgent = (BusAgent) myAgents.get(busName);
						myBusAgent.setAIDtoSource(lineAgent.getAID());
					}

				}
			} else {
				JOptionPane.showMessageDialog(null, "unknown key in linesConnectedToBuses", "Error",
						JOptionPane.ERROR_MESSAGE);
			}

		}

	}

	private void evAgentsCreation(List<ProsumerAgent> prosumerAgents) {
		List<String> EVs;
		EVs = PowerFactoryCaller.GetEVs();
		/*
		 * if(EVs.size()!=Parameters.getNbEVs()) { JOptionPane.showMessageDialog(null,
		 * "Fleet size is not correct", "Error", JOptionPane.ERROR_MESSAGE); }
		 */

		AggregatorAgent aggregator = (AggregatorAgent) myAgents.get("Aggregator");
		for (String ev : EVs) {
			String evName = PowerFactoryCaller.GetComplete1Param(ev, "loc_name");
			if (Parameters.getEVActivated(evName)) {
				PowerFactoryCaller.EVConnection(ev, true);// connect vehicle
				EVAgent evAgent = new EVAgent(this, evName, ev);
				evAgent.setAggregatorAID(aggregator.getAID());
				myAgents.put(evName, evAgent);
				aggregator.addNeighborAID(evAgent.getAID());

				// get bus
				String cubPath = PowerFactoryCaller.Get1Param(ev, "bus1");
				String busPath = PowerFactoryCaller.Get1Param(cubPath, "cterm");
				String bus = PowerFactoryCaller.GetComplete1Param(busPath, "loc_name");
				BusAgent busAgent = (BusAgent) myAgents.get(bus);
				busAgent.addProsumerAID(evAgent.getAID());
				evAgent.setBusAID(busAgent.getAID());
				prosumerAgents.add(evAgent);
				evAgent.addNeighbor(busAgent);
				busAgent.addNeighbor(evAgent);

			} else {
				PowerFactoryCaller.EVConnection(ev, false);// disconnect vehicle
			}

		}
	}

	private void pvAgentsCreation(List<ProsumerAgent> prosumerAgents) {
		List<String> PVs;
		// PVdslModels = new ArrayList<>();
		PVs = PowerFactoryCaller.GetPVs();
		AggregatorAgent aggregator = (AggregatorAgent) myAgents.get("Aggregator");
		for (String pv : PVs) {
			// PVAgent pvagent=new PVAgent(this,pv);
			// PVdslModels.add(pvagent.getDslmodels());
			String pvName = PowerFactoryCaller.GetComplete1Param(pv, "loc_name");
			PowerFactoryCaller.EVConnection(pv, true);// disconnect PV
			if (Parameters.getPVActivated(pvName)) {
				PVAgent pvAgent = new PVAgent(this, pvName, pv);
				pvAgent.setAggregatorAID(aggregator.getAID());
				myAgents.put(pvName, pvAgent);
				aggregator.addNeighborAID(pvAgent.getAID());

				// get bus
				String cubPath = PowerFactoryCaller.Get1Param(pv, "bus1");
				String busPath = PowerFactoryCaller.Get1Param(cubPath, "cterm");
				String bus = PowerFactoryCaller.GetComplete1Param(busPath, "loc_name");
				BusAgent busAgent = (BusAgent) myAgents.get(bus);
				busAgent.addProsumerAID(pvAgent.getAID());
				pvAgent.setBusAID(busAgent.getAID());
				prosumerAgents.add(pvAgent);
				pvAgent.addNeighbor(busAgent);
				busAgent.addNeighbor(pvAgent);

			} else {
				PowerFactoryCaller.EVConnection(pv, false);// disconnect PV
			}

		}
	}

	@Override
	public void onSystemCycleBegin() {
		/*
		 * try { environment.onCycleBegin(); }catch(EndSimulationException e) {
		 * e.printStackTrace(); endSimu(); }
		 */
		MyLog.print("\n" + format.format(this.environment.getCurrentTime().getTime()));
		System.out.println("\n" + format.format(this.environment.getCurrentTime().getTime()));
		if (!this.environment.getCurrentTime().before(endTime)) {
			System.out.println("end simu");
			endSimu();
			// throw new EndSimulationException("end of simulation");
			// amas.endSimu();
		} else {
			environment.onCycleBegin();
		}

		for (Load load : loads) {
			load.checkConsumption();
		}

	}

	@Override
	protected void onSystemCycleEnd() {
		super.onSystemCycleEnd();
		Results.endCycle();
	}

	public void endSimu() {
		Scheduler.getDefaultScheduler().stop();
		for (String agentName : myAgents.keySet()) {
			myAgents.get(agentName).endSimulation();
		}
		// Results.endSimu();

		Results.saveLastResults();

		simulationDuration = System.currentTimeMillis() - simulationDuration;
//		System.out.println("simulation time : " + ((double) simulationDuration) / 60000 + " minutes");
		JOptionPane.showMessageDialog(null, "End of simulation", "Information", JOptionPane.INFORMATION_MESSAGE);
		/*
		 * String s =
		 * (String)JOptionPane.showInputDialog(null,"Completer :\n \"un plus un = ...\""
		 * , "le titre",JOptionPane.QUESTION_MESSAGE,null, // c'est ouvert !!! "trois");
		 * // valeur initiale if ((s != null) && (s.length() > 0))
		 * messagesPerso.setText("\"un plus un = ...\" " + s + "!");
		 */
		String simulation = JOptionPane.showInputDialog("Simulation name", "Simulation");

		final String dir = System.getProperty("user.dir");
		Path workingPath = Paths.get(dir);
		String communicationPath = workingPath.getParent().getParent().toString();
		String endSimulationPath = communicationPath + "\\Results\\ADEMISResults\\" + simulation + "\\";

		Parameters.setEndSimulationPath(endSimulationPath);

		if (simulation != null) {
			Results.saveResults(simulation);
			Parameters.writeParameters(simulation);
			MyLog.endSimu(simulation);
		}
	}

}
