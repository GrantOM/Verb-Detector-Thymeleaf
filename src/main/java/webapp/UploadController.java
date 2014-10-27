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
    public String greetingSubmit(@RequestParam("file") MultipartFile file, @ModelAttribute Upload upload, Model model) {//, @RequestParam("name") String name,
            //@RequestParam("file") MultipartFile file) {
        SubmissionModel submission = new SubmissionModel();
        model.addAttribute("upload", upload);
        if (!file.isEmpty()) {
            try {
                //byte[] bytes = file.getBytes();
                //int read = 0; 
                //String line = null;
                
                List<String> fileText = new ArrayList<String>();
                String fileName = file.getOriginalFilename();
                BufferedInputStream inputStream = new BufferedInputStream(file.getInputStream()); 
                //BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File(fileName)));
 
            	Metadata metadata = new Metadata();
                BodyContentHandler ch = new BodyContentHandler(10*1024*1024);
                ch.startDocument();
            	AutoDetectParser parser = new AutoDetectParser();
            	
            	String mimeType = new Tika().detect(fileName);
                metadata.set(Metadata.CONTENT_TYPE, mimeType);
                
                parser.parse(inputStream, ch, metadata, new ParseContext());
                inputStream.close();
                
                for (int i = 0; i < metadata.names().length; i++) {
                    String item = metadata.names()[i];
                    fileText.add(item + " -- " + metadata.get(item));
                }
                
                VerbFinder verbs = new VerbFinder();
    
                /*BufferedOutputStream stream = 
                		new BufferedOutputStream(new FileOutputStream(new File("uploadedfiles/" + fileName)));
                while ((read = inputStream.read(bytes)) != -1) { 
                	stream.write(bytes, 0, read);               	
                }
                stream.close();
                inputStream.close();*/

                /*BufferedReader br = new BufferedReader(new FileReader("uploadedfiles/" + fileName));
                while ((line = br.readLine()) != null) {
                	fileText.add(line);
                }
                br.close();
                return "You successfully uploaded " + name + " into " + name + "-uploaded !" + fileName +
                		fileText + "\n" + ch.toString();*/
                
                
                //This will not work any more
                
                
                verbs.findVerbs(ch.toString());
                
                submission.setContent(ch.toString() + verbs.GetResults(0).toString());
                
                upload.setContent(ch.toString());
                //greeting.setCreating(verbs.findVerbs(ch.toString()).getJSONArray("creating").getJSONObject(0).getString("verb"));
                //for (int i = 0; i < verbs.GetResults(0).getJSONArray("creating").length(); i++) {
                //greeting.setTotal(verbs.GetResults(0).getJSONArray("creating").getJSONObject(0).getString("verb").toString());
                //}
                	
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
        upload.setContent("empty");
        return "upload";
        
    }

}