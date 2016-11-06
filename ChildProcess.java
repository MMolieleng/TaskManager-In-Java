import java.util.List;
import java.lang.reflect.Field; //for Field variable
import java.io.*;
import java.util.concurrent.TimeUnit;
import org.json.simple.JSONObject;
import java.util.Arrays;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChildProcess extends Thread
{
	private ProcessBuilder pb;
	private Process process;
	private List<String> command;
	private int processStatus;
	private String name;
	private	long pid;
	private boolean restart;
	private BufferedWriter log;

	private int numProcesses;
	private int startTime;
	private int stopTime;
	private String outputDir;
	private String errorDir;
	private String workingDir;
	private String unmusk;


	public ChildProcess()
	{
	}

	public ChildProcess(List<String> cmd)
	{
		command = cmd;
		outputDir = "runtime/out.out";
		setStartTime(0);
		new TaskDirectory("runtime").createFile("out.out");
		logToFile(cmd.get(0)+" process created");
	}

	public ChildProcess(JSONObject obj)
	{
		numProcesses = (Integer.parseInt(obj.get("NumProcs").toString()));
		setStartTime(Integer.parseInt(obj.get("StartTime").toString()));
		
		String dir  = obj.get("WorkingDir").toString();
		new TaskDirectory(dir);
		workingDir = dir;

		unmusk = obj.get("Unmusk").toString();

		setOutputDir(obj.get("Outfile").toString());
		setErrorDir(obj.get("Errfile").toString());
		setProcessName(obj.get("Command").toString());

		String cmd = obj.get("Command").toString();
		String lineSplit[] = cmd.split(" ");
		command = Arrays.asList(lineSplit);
	}

	public	void restartProcess()
	{
		try
		{	
			String[]ar = {"-9", ""+pid};
			Runtime.getRuntime().exec("kill -9 -f").waitFor();
			Thread.sleep(1000);
			
			boolean finished = process.waitFor(100, TimeUnit.MILLISECONDS);
			if (finished)
			{
				ProcessBuilder pbr = new ProcessBuilder("cat");
				process = pbr.start();
				logToFile(getProcessName() + " restarted successfully ");
			}
			else
			{
				logToFile(getProcessName() + " could not start");
			}
		}
		catch(Exception err)
		{
			logToFile("Could not restart process ");
		}
	}

	public void	setWorkingDir(String dir)
	{
		workingDir = dir;
	}

	public String getWorkingDir()
	{
		return workingDir;
	}

	public int getStatus()
	{
		return processStatus;
	}

	public int getNumProc()
	{
		return (numProcesses);
	}

	public String getProcessName()
	{
		return name;
	}

	public List<String> getCommand()
	{
		return command;
	}

	public void stopProcess()
	{
		try
		{
			logToFile(getProcessName() + " has been stopped");
			process.destroy();

			Thread.sleep(1000);
			if (p.exitValue() != 0)
            {
                p.destroyForcibly();
            }

		}catch (Exception e){
			logToFile(getProcessName() + " could not stopped");
		}
	}

	public void enableRestart()
	{
		restart = true;
	}

	public void setProcessName(String name)
	{
		this.name = name;
	}

	public	int processExitStatus()
	{
		return (int)process.exitValue();
	}

	public	long getPid()
	{
		return (pid);
	}

	public	void setOutputDir(String dir)
	{
		outputDir = dir;
	}

	public String getOutputDir()
	{
		return outputDir;
	}

	public	void setErrorDir(String dir)
	{
		errorDir = dir;
	}

	public int getStartTime()
	{
		return startTime;
	}

	public void setStartTime(int i)
	{
		startTime = i;
	}

	public String getErrorDir()
	{
		return errorDir;
	}

	public void redirectOutput()
	{
		try{
			//Redirect output to another file
			File file = new File(getOutputDir());

			//if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
			    bw.write(line+"\n");
			}
			//process.waitFor();
			bw.close();
		}catch(Exception e){System.out.print("");}
	}


	public void redirectError()
	{
		try{
			//Redirect output to another file
			File file = new File(getErrorDir());

			//if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line;
			while ((line = in.readLine()) != null) {
			    bw.write(line+"\n");
			}
			//process.waitFor();
			bw.close();
		}catch(Exception e){System.out.print("");}
	}

	public void logToFile(String msg)
	{
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("E yyyy.MM.dd  HH:mm:ss.SSS");
		try
		{
			File f = new File("runtime/log.log");

			log = new BufferedWriter(new FileWriter(f, true));
			log.append(sdf.format(cal.getTime())+"   "+msg+"\n");
			log.close();
		}
		catch(Exception e){

		}
	}

	public void run()
	{
		try
		{
			pb = new ProcessBuilder(command);

			//Umask
			Runtime.getRuntime().exec( "chmod "+unmusk+" "+workingDir);
			
			pb.redirectError(new File("runtime/errors.log"));
			
			//Working Directory
			pb.directory(new File(workingDir));
			
			name = command.get(0);
			processStatus = -1;

			int i = 0;
	        try
	        {
	            while (i < getStartTime())
	            {
	            	logToFile(name+ " will start in "+(getStartTime() - i) +" seconds");
	                Thread.sleep(1000);
	                i++;
	            }
	        }
	        catch (InterruptedException e) {
	            e.printStackTrace();
	        }

			process = pb.start();
			
			//Redirect output and error streams 
			redirectOutput();
			redirectError();

			logToFile(getProcessName()+" process started");

			try {
				if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
					Field f = process.getClass().getDeclaredField("pid");
					f.setAccessible(true);
					pid = f.getLong(process);
					f.setAccessible(false);
				}
			} catch (Exception e) {
				pid = -1;
				log.close();
			}
			processStatus = process.waitFor();

			
			//(getProcessName()+" started\n");
			///System.out.println("Echo command executed, with status: " + errCode);
		}
		catch (Exception e)
		{
			//System.out.println(e.getMessage());
			logToFile(getProcessName() +" count not start");
		}
	}
}
