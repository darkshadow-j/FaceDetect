package app.plenczewski.facedetect.controller;

import app.plenczewski.facedetect.model.FaceObject;
import app.plenczewski.facedetect.model.ImageURL;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Stream;

@RestController
@RequestMapping("/face")
public class FaceApiController {

    @Value("${api.key}")
    public String API_KEY;
    public static String FACE_API_URL = "https://westeurope.api.cognitive.microsoft.com/face/v1.0/detect?";

    @GetMapping
    public FaceObject[] getInfo() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<ImageURL> imageURLHttpEntity = getHttpEntity("https://m.maccosmetics.pl/media/export/cms/products/640x600/mac_sku_MW3A53_640x600_1.jpg");
        ResponseEntity<FaceObject[]> responseEntity = restTemplate.exchange(getAPIUrl(), HttpMethod.POST, imageURLHttpEntity, FaceObject[].class);
        Stream.of(responseEntity.getBody()).forEach(System.out::println);
        return responseEntity.getBody();

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
