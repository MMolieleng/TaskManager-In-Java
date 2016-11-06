import java.io.*;

public class TaskDirectory{

	public TaskDirectory(String dirName){

		File theDir = new File(dirName);

		try{
			/* check if the directory exists and file is a valid directory */
			if (theDir.exists() && theDir.isDirectory()) {
			    
			    //System.out.println(theDir + " already exists, please chose another directory name");
			}
			else{
				theDir.mkdirs();
				//System.out.println(theDir + " has been created");
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void createFile(String fileName){

		boolean succes = false;
	    File file = new File(fileName);
	    
	    succes = true;

	    if(succes){
	    	try{
	    		/* check if the file exists and file is a valid file */
	    		if(file.exists() && file.isFile()){
	    			//System.out.println(file + " exists, please choose another file name");
	    		}
	    		else{
	    			/* create the file passed as parameter */
				    file.createNewFile();
				   // System.out.println(fileName + " has been created!");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}