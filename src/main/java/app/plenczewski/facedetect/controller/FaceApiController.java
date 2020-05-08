package app.plenczewski.facedetect.controller;

import app.plenczewski.facedetect.model.FaceObject;
import app.plenczewski.facedetect.model.ImageURL;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Controller
@RequestMapping("/face")
public class FaceApiController {

    @Value("${api.key}")
    public String API_KEY;
    public static String FACE_API_URL = "https://westeurope.api.cognitive.microsoft.com/face/v1.0/detect?";
    private final String UPLOAD_DIR = "./uploads/";

    @GetMapping
    public String getInfo() throws URISyntaxException {
        return "index";
    }

    private FaceObject[] analizeImage(String nameFile) throws URISyntaxException {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<ImageURL> imageURLHttpEntity = getHttpEntity("http://46.41.138.231/"+nameFile);
            ResponseEntity<FaceObject[]> responseEntity = restTemplate.exchange(getAPIUrl(), HttpMethod.POST, imageURLHttpEntity, FaceObject[].class);
          /*  Stream.of(responseEntity.getBody()).forEach(System.out::println);*/
        System.out.println(responseEntity.getBody()[0]);
            return responseEntity.getBody();
    }

    @PostMapping()
    public String get(@RequestParam("file") MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws URISyntaxException {
        System.out.println("Upload");
        System.out.println(multipartFile.getName());

        // normalize the file path
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        // check if file is empty
        if (multipartFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/face";
        }
        // save the file on the local file system
        try {
            Path path = Paths.get("/var/www/html/" + fileName);
            Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return success response
        redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');
        FaceObject[] faceObjects= analizeImage(fileName);
        redirectAttributes.addFlashAttribute("gender", faceObjects[0].getFaceAttributes().getGender());
        redirectAttributes.addFlashAttribute("age", faceObjects[0].getFaceAttributes().getAge());
        redirectAttributes.addFlashAttribute("hair", faceObjects[0].getFaceAttributes().getHair().getHairColor().get(0).getColor());
        redirectAttributes.addFlashAttribute("smile", faceObjects[0].getFaceAttributes().getSmile());
        redirectAttributes.addFlashAttribute("emotion", faceObjects[0].getFaceAttributes().getEmotion().getSadness());
        return "redirect:/face";
    }

    private HttpEntity<ImageURL> getHttpEntity(String image) {
        ImageURL imageURL = new ImageURL(image);
        HttpHeaders httpHeaders = getHttpHeaders();
        return new HttpEntity<>(imageURL, httpHeaders);
    }

    private URI getAPIUrl() throws URISyntaxException {

        URIBuilder uriBuilder = new URIBuilder(FACE_API_URL);
        uriBuilder.addParameter("returnFaceId", "true");
        uriBuilder.addParameter("returnFaceLandmarks", "false");
        uriBuilder.addParameter("returnFaceAttributes", "makeup,hair,age,gender,smile,facialHair,headPose,glasses,emotion");
        uriBuilder.addParameter("recognitionModel", "recognition_01");
        uriBuilder.addParameter("returnRecognitionModel", "false");
        uriBuilder.addParameter("detectionModel", "detection_01");
        return uriBuilder.build();
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Ocp-Apim-Subscription-Key", API_KEY);
        return httpHeaders;
    }

}
