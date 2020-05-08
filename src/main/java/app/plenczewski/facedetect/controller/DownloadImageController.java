package app.plenczewski.facedetect.controller;

import org.apache.http.entity.ContentType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin
@Controller
@RequestMapping("/file")
public class DownloadImageController {
    @GetMapping
    public ResponseEntity<Resource> getImage(){
        System.out.println("sss");
        Resource resource = null;
/*        String fileBasePath = "C:\\Users\\Pawel\\IdeaProjects\\facedetect\\";*/

        Path path = Paths.get( "3_Beautiful-girl-with-a-gentle-smile.jpg");
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
