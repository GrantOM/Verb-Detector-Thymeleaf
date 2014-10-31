package webapp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.json.*;

/* This class facilitates the verb finding functionality of the application.
 * Upon construction, it pulls Bloom's Taxonomy verbs from a text file located
 * in the project directory upon construction. The text file holds the format as follows:
 * 
 * 	The first line contains the 6 titles of Bloom's Taxonomy's tiers delimited by
 * 			the tab key.
 * 	Each subsequent line contains verbs corresponding to the taxonomy in the
 * 			categorical order of the first line, delimited by the tab key.
 * 
 * 	It is possible to change this text of this file, provided it still has 6 categories.
 *
 */

public class VerbFinder {
	
	private ResultModel rM;
	
	private BufferedReader buf;
	private ArrayList<String[]> taxonomy;
	private String lineJustFetched;
	private String[] verbArray;
	
    //Constructor
	public VerbFinder() throws FileNotFoundException{
		
		rM = new ResultModel();
		
		buf = new BufferedReader(new FileReader("Verbs.txt"));
        taxonomy = new ArrayList<>();
        lineJustFetched = null;
        
        try{
            
            while(true){
            	
                lineJustFetched = buf.readLine();
                
                if(lineJustFetched == null){  
                	
                    break; 
                }else{
                	
                    verbArray = lineJustFetched.split("\t");
                    taxonomy.add(verbArray);
                }
            }

            buf.close();
            
        }catch(IOException e) {
    		e.printStackTrace();
        }
        
	}
	
	
	//Function - findVerbs(String fileText) - This function accepts a string containing
	// the text of any uploaded files and finds matches according to the verbs supplied in
	// verbs.txt. It stores the results in Results.txt and as such, may be called as many
	// times as needed with as many texts as needed. These results can be accessed using
	// the GetResults(int index) function
    public void findVerbs(String fileText ) throws JSONException, IOException {
    	
    	int totalVerbCount = 0;
    	JSONObject results = new JSONObject();
    	JSONArray resultY = new JSONArray();
    	JSONObject resultX = new JSONObject();
    	JSONObject countResults = new JSONObject();
    	int count = 0;
    	
    	File file = new File("Results.txt");

    	fileText = fileText.toLowerCase();
    	
    	for (int i = 0; i < taxonomy.get(0).length; i++){
    		
    		for (int j = 1; j < taxonomy.size(); j++){
    			
    			if (taxonomy.get(j)[i] != ""){
    				
    				count = StringUtils.countMatches(fileText, taxonomy.get(j)[i]); 
    				
    				if (count > 0){	
    					
    					totalVerbCount += count;
    					resultX.put("result", count);
    					resultX.put("verb", taxonomy.get(j)[i]);
    					
    				}
    				
    			}	
    			
    			if (count > 0){
    				
    				resultY.put(resultX);
    				resultX = new JSONObject();
    				
    			}
    		}
    		
        	countResults.accumulate("total", totalVerbCount);
        	results.put(taxonomy.get(0)[i] , resultY);
    		resultY = new JSONArray();
    		totalVerbCount = 0;
    	}
    	
    	if (!file.exists()) {
			file.createNewFile();
		}
    		
    	FileWriter fw = new FileWriter(file.getAbsoluteFile());
    	BufferedWriter bw = new BufferedWriter(fw);
    	bw.write(results.toString() + "\t");
    	bw.write(countResults.toString());
    	bw.close();
    		
    	rM.Refresh();
    
    } 
    
    //Function - GetResults(int index) - The function will retrieve the results of any search
    // given its chronological index i.e. the results of the first call to
    // findVerbs(String filetext) will be given by VerbFinder.GetResults(0)
    public JSONObject GetResults(int index) throws JSONException{
    	
    	return rM.GetResults(index);
    }
    
}   