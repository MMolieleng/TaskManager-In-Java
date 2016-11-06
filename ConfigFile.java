import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.Iterator;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;

public class ConfigFile
{
    private ArrayList<JSONObject> programs;
    private Object obj;

    public ConfigFile(String jsonFile)
    {
        JSONParser parser = new JSONParser();
        try {
		    obj = parser.parse(new FileReader(jsonFile));
        }
        catch (FileNotFoundException e)
        {
            System.out.println(jsonFile + ": File not found");
        }
        catch (IOException e)
        {
            System.out.println(jsonFile + ": Error reading file");
        }
        catch (ParseException e)
        {
            System.out.println(jsonFile + ": File parsing error");
        }
    }

    public ArrayList<JSONObject> loadPrograms()
    {
        JSONObject jsonObject = (JSONObject)obj;
        JSONArray prgArray = (JSONArray)jsonObject.get("Programs");
        Iterator<JSONObject> iterator = prgArray.iterator();
        programs = new ArrayList<JSONObject>();

        while (iterator.hasNext())
        {
            programs.add(iterator.next());
        }
        return programs;
    }
}