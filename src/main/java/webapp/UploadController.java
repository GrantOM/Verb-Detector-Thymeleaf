package webapp;

import java.io.BufferedInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
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

    @RequestMapping(value="/upload", method=RequestMethod.GET)
    public String greetingForm(Model model) {
        model.addAttribute("upload", new Upload());
        return "upload";
    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public String greetingSubmit(@RequestParam("file") MultipartFile file, /*@RequestParam("file2") MultipartFile file2,*/ 
    		@ModelAttribute Upload upload, Model model) {
        model.addAttribute("upload", upload);
        if (!file.isEmpty()) {
            try {
                
                List<String> fileText = new ArrayList<String>();
                //List<String> fileText2 = new ArrayList<String>();
                String fileName = file.getOriginalFilename();
                //String fileName2 = file2.getOriginalFilename();
                if (fileName == null) {
                	fileName = "";
                }
                
                BufferedInputStream inputStream = new BufferedInputStream(file.getInputStream());
                //BufferedInputStream inputStream2 = new BufferedInputStream(file2.getInputStream()); 
 
            	Metadata metadata = new Metadata();
            	//Metadata metadata2 = new Metadata();
                BodyContentHandler ch = new BodyContentHandler(10*1024*1024);
                //BodyContentHandler ch2 = new BodyContentHandler(10*1024*1024);
                ch.startDocument();
                //ch2.startDocument();
            	AutoDetectParser parser = new AutoDetectParser();
            	//AutoDetectParser parser2 = new AutoDetectParser();
            	
            	String mimeType = new Tika().detect(fileName);
            	//String mimeType2 = new Tika().detect(fileName2);
                metadata.set(Metadata.CONTENT_TYPE, mimeType);
                //metadata2.set(Metadata.CONTENT_TYPE, mimeType2);
                
                parser.parse(inputStream, ch, metadata, new ParseContext());
                //parser2.parse(inputStream2, ch2, metadata2, new ParseContext());
                inputStream.close();
                //inputStream2.close();
                
                for (int i = 0; i < metadata.names().length; i++) {
                    String item = metadata.names()[i];
                    fileText.add(item + " -- " + metadata.get(item));
                }
                
                VerbFinder verbs = new VerbFinder();
                //VerbFinder verbs2 = new VerbFinder();
                verbs.findVerbs(ch.toString());
                //verbs.findVerbs(ch2.toString());
                
                upload.setContent(ch.toString());
                //upload.setContent(ch.toString() + ch2.toString());
                	
                upload.setCreating(verbs.GetResults(0).getJSONArray("creating").toString());
                upload.setEvaluating(verbs.GetResults(0).getJSONArray("evaluating").toString());
                upload.setAnalysing(verbs.GetResults(0).getJSONArray("analysing").toString());
                upload.setApplying(verbs.GetResults(0).getJSONArray("applying").toString());
                upload.setUnderstanding(verbs.GetResults(0).getJSONArray("understanding").toString());
                upload.setRemembering(verbs.GetResults(0).getJSONArray("remembering").toString());
                
                upload.setTotalVal(verbs.GetResults(1).getJSONArray("total"));
                upload.setCreatingVal(verbs.GetResults(1).getJSONArray("total").get(0).toString());
                upload.setEvaluatingVal(verbs.GetResults(1).getJSONArray("total").get(1).toString());
                upload.setAnalysingVal(verbs.GetResults(1).getJSONArray("total").get(2).toString());
                upload.setApplyingVal(verbs.GetResults(1).getJSONArray("total").get(3).toString());
                upload.setUnderstandingVal(verbs.GetResults(1).getJSONArray("total").get(4).toString());
                upload.setRememberingVal(verbs.GetResults(1).getJSONArray("total").get(5).toString());
                
                ch.endDocument();
                
                return "upload";
            } catch (Exception e) {
                //return "You failed to upload => " + e.getMessage();
            	upload.setContent(e.getMessage());
            	return "upload";
            }
        }
		//return "You failed to upload";
        upload.setContent("Error: File is empty.");
        return "upload";
        
    }

}
