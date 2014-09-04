package hello;

import org.springframework.web.multipart.MultipartFile;

public class Greeting {

    private long id;
    private String content;
    private MultipartFile file;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public MultipartFile getFile() {
    	return file;
    }
    
    public void setFile(MultipartFile file) {
    	this.file = file;
    }

}
