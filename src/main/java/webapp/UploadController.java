/**
 * REST controller for the web application. Handles uploading and displaying of document contents and verb detector results.
 * 
 * 	Authors:
 * 		Grant O'Meara
 * 		Christian O'Grady-Mott
 * 		Austin Horn	
 */

package webapp;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController {

	/**
	 * Function that displays the upload.html page with data retrieved from the text file
	 * 
	 * @param model
	 * @return
	 */
    @RequestMapping(value="/upload", method=RequestMethod.GET)
    public String greetingForm(Model model) {
        model.addAttribute("upload", new Upload());
        return "upload";
    }

    /**
     * Function that handles the data entered into the web application when the submit button is pressed.
     * 
     * @param file - the file uploaded and its contents
     * @param upload - model that contains objects used to store JSON information
     * @param model
     * @return - JSON objects and specific data retrieved from JSON arrays using thymeleaf
     */
    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public String greetingSubmit(@RequestParam("file") MultipartFile file, @ModelAttribute Upload upload, Model model) {
        model.addAttribute("upload", upload);
        if (!file.isEmpty()) {
            try {
                
            	//create array list to store document metadata and store filename to a variable
                List<String> fileText = new ArrayList<String>();
                String fileName = file.getOriginalFilename();
                if (fileName == null) {
                	fileName = "";
                }
                
                BufferedInputStream inputStream = new BufferedInputStream(file.getInputStream());
 
                //create varialbes and objects that are required to parse documents
            	Metadata metadata = new Metadata();
                BodyContentHandler ch = new BodyContentHandler(10*1024*1024); //object to store text from document
                ch.startDocument(); //initialise object for parsing
            	AutoDetectParser parser = new AutoDetectParser();
            	
            	//determine what type of file is requesting to be parsed, set metadata accordingly
            	String mimeType = new Tika().detect(fileName);
                metadata.set(Metadata.CONTENT_TYPE, mimeType);
                
                //parse text and close the text stream when finished
                parser.parse(inputStream, ch, metadata, new ParseContext());
                inputStream.close();
                
                //store metadata in arraylist
                for (int i = 0; i < metadata.names().length; i++) {
                    String item = metadata.names()[i];
                    fileText.add(item + " -- " + metadata.get(item));
                }
                
                //create verbfinder object and detect verbs in the document
                VerbFinder verbs = new VerbFinder();
                verbs.findVerbs(ch.toString());
                
                //add file contents to object model
                upload.setContent(ch.toString());
                
                //add json array contents to object model
                upload.setCreating(verbs.GetResults(0).getJSONArray("creating").toString());
                upload.setEvaluating(verbs.GetResults(0).getJSONArray("evaluating").toString());
                upload.setAnalysing(verbs.GetResults(0).getJSONArray("analysing").toString());
                upload.setApplying(verbs.GetResults(0).getJSONArray("applying").toString());
                upload.setUnderstanding(verbs.GetResults(0).getJSONArray("understanding").toString());
                upload.setRemembering(verbs.GetResults(0).getJSONArray("remembering").toString());
                
                //add array totals to object model
                upload.setCreatingVal(verbs.GetResults(1).getJSONArray("total").get(0).toString());
                upload.setEvaluatingVal(verbs.GetResults(1).getJSONArray("total").get(1).toString());
                upload.setAnalysingVal(verbs.GetResults(1).getJSONArray("total").get(2).toString());
                upload.setApplyingVal(verbs.GetResults(1).getJSONArray("total").get(3).toString());
                upload.setUnderstandingVal(verbs.GetResults(1).getJSONArray("total").get(4).toString());
                upload.setRememberingVal(verbs.GetResults(1).getJSONArray("total").get(5).toString());
                
                //close the document
                ch.endDocument();
                
                return "upload";
            } catch (Exception e) {
                //returns an error message if upload fails
            	upload.setContent(e.getMessage());
            	return "upload";
            }
        }
		//if no file was uploaded or the file is blank this error will be displayed instead
        upload.setContent("Error: File is empty.");
        return "upload";        
    }
}