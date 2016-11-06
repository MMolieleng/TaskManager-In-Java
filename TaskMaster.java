import java.util.Scanner;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import org.json.simple.JSONObject;

public class TaskMaster
{
	private static ChildProcess process;
	private static ArrayList<ChildProcess> processList;

	private static void commandPrompt(Scanner sc)
	{
		System.out.print("taskmaster:> ");
		String line = sc.nextLine();

		if (line.equals("exit"))
		{
			for (ChildProcess p:processList)
			{
				if (p.isAlive())
					p.stopProcess();
			}
			return;
		}

		else if (line.equals("clear"))
		{
			try{System.out.print("\033[H\033[2J");}
            catch(Exception e){System.out.println("Command not found");}
		}

		else if (line.equals("status"))
		{
			for (ChildProcess p:processList)
			{
				System.out.print(p.getProcessName());
				if (p.isAlive())
					System.out.println(" is running.");
				else
					System.out.println(" has stopped.");
			}
		}
		else if (line.equals("reload"))
		{
			reloadConfig();
		}
		else
		{
			if (!line.equals(""))
			{
				String lineSplit[] = line.split(" ");
				List<String> command = Arrays.asList(lineSplit);

				if (command.get(0).equals("stop") && command.size() == 2)
				{
					for (ChildProcess p:processList)
					{
						if (p.getProcessName().equals(command.get(1)))
							p.stopProcess();
					}
				}
				else if (command.get(0).equals("restart") && command.size() == 2)
				{
					for (ChildProcess p:processList)
					{
						if (p.getProcessName().equals(command.get(1)))
						{
							if (!p.isAlive())
							{
								int i = processList.indexOf(p);
								ChildProcess proc = new ChildProcess(p.getCommand());
								proc.setProcessName(p.getProcessName());
								proc.logToFile(p.getProcessName() +" has been restarted");
								processList.set(i, proc);
								processList.get(i).start();
							}
							else
							{
								System.out.println("Error: " + p.getProcessName() + " is still running.");
							}
						}
					}
				}
				else
				{
					process = new ChildProcess(command);
					process.start();
					processList.add(process);
				}
			}
		}
		commandPrompt(sc);
	}

	public static void reloadConfig()
	{
		ConfigFile config = new ConfigFile("config3.json");
		ArrayList<JSONObject> programs = config.loadPrograms();
		long i = (Long)programs.get(0).get("NumProcs") + (Long)programs.get(1).get("NumProcs");
		
		ChildProcess configChiled = new ChildProcess();
		configChiled.logToFile("Configurations file reoading...");

		try{
			for (JSONObject obj:programs)
			{
				int numProcs = (Integer.parseInt(obj.get("NumProcs").toString()));
				int startTime = (Integer.parseInt(obj.get("StartTime").toString()));
				while (numProcs > 0)
				{
					process = new ChildProcess(obj);
					process.start();
					processList.add(process);
					numProcs--;
				}
			}
			configChiled.logToFile("Configurations file successfully reloaded");
		}
		catch(Exception er){
			configChiled.logToFile("Configurations file reload FAILED");
		}
	}

	public static void main(String argv[])
	{
		Scanner sc = new Scanner(System.in);
		processList = new ArrayList<ChildProcess>();
		ConfigFile config = new ConfigFile("config3.json");
		ArrayList<JSONObject> programs = config.loadPrograms();
		long i = (Long)programs.get(0).get("NumProcs") + (Long)programs.get(1).get("NumProcs");
		
		for (JSONObject obj:programs)
		{
			int numProcs = (Integer.parseInt(obj.get("NumProcs").toString()));
			int startTime = (Integer.parseInt(obj.get("StartTime").toString()));
			while (numProcs > 0)
			{
				process = new ChildProcess(obj);
				process.start();
				processList.add(process);
				numProcs--;
			}
		}
		commandPrompt(sc);
	}
}