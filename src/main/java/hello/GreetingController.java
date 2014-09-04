package hello;

import java.io.InputStream;
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
public class GreetingController {

    @RequestMapping(value="/greeting", method=RequestMethod.GET)
    public String greetingForm(Model model) {
        model.addAttribute("greeting", new Greeting());
        return "greeting";
    }

    @RequestMapping(value="/greeting", method=RequestMethod.POST)
    public String greetingSubmit(@RequestParam("file") MultipartFile file, @ModelAttribute Greeting greeting, Model model) {//, @RequestParam("name") String name,
            //@RequestParam("file") MultipartFile file) {
        SubmissionModel submission = new SubmissionModel();
        model.addAttribute("greeting", greeting);
        if (!file.isEmpty()) {
            try {
                //byte[] bytes = file.getBytes();
                //int read = 0; 
                //String line = null;
                
                List<String> fileText = new ArrayList<String>();
                String fileName = file.getOriginalFilename();
                InputStream inputStream = file.getInputStream(); 
 
            	Metadata metadata = new Metadata();
                BodyContentHandler ch = new BodyContentHandler();
            	AutoDetectParser parser = new AutoDetectParser();
            	
            	String mimeType = new Tika().detect(fileName);
                metadata.set(Metadata.CONTENT_TYPE, mimeType);
                
                parser.parse(inputStream, ch, metadata, new ParseContext());
                
                for (int i = 0; i < metadata.names().length; i++) {
                    String item = metadata.names()[i];
                    fileText.add(item + " -- " + metadata.get(item));
                }
    
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
                submission.setContent(ch.toString());
                
                greeting.setContent(ch.toString());
                
                
                return "greeting";
            } catch (Exception e) {
                //return "You failed to upload => " + e.getMessage();
            	return "greeting";
            }
        }
		//return "You failed to upload";
        return "greeting";
        
    }

}
