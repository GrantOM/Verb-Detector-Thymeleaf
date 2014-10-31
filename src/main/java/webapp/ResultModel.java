package webapp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.*;

/* This class handles reading from the Results.txt file and converting the contents
 * back to a JSON object for use within the web app.
 */
public class ResultModel {
	
	private JSONObject[] ResultsArray;
	private BufferedReader buf;
	private String lineJustFetched;
	private String entireContents = "";
	private String[] StringResultsArray;
	
	
	//Function - Refresh() - This function repopulates ResultsArray with whatever is
	// is currently in Results.txt. It is called within the definition of
	// VerbFinder.findVerbs() 
	public void Refresh() throws FileNotFoundException{
		
		buf = new BufferedReader(new FileReader("Results.txt"));
	    lineJustFetched = null;
	    
	    try{
	        
	        while(true){
	        	
	            lineJustFetched = buf.readLine();
	            
	            if(lineJustFetched == null){  
	            	
	                break; 
	                
	            }else{
	            	
	                entireContents += lineJustFetched;
	    
	            }
	        }
	        
	    StringResultsArray = entireContents.split("\t");
	
	    buf.close();
	        
	    }catch(IOException e) {
			e.printStackTrace();
	    }
	    
	    ResultsArray = new JSONObject[StringResultsArray.length];
		
		for (int i = 0; i < StringResultsArray.length; i++) {
			
			
			ResultsArray[i] = new JSONObject(StringResultsArray[i]);
			
		}
		
	}
	
	//Function - GetResults(int index) - Returns the JSON object located at the index
	public JSONObject GetResults(int index) throws JSONException {
			
		return ResultsArray[index];
		
	}

}