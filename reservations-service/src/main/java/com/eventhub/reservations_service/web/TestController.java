package com.eventhub.reservations_service.web;

import com.eventhub.reservations_service.clients.UserClient;
import com.eventhub.reservations_service.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final UserClient userClient;
    private final RestTemplate restTemplate;

    @GetMapping("/test")
    public String testFeign(HttpServletRequest request) {
        String incoming = request.getHeader("Authorization");
        System.out.println("Incoming Authorization header: " + incoming);
        UserDTO user = userClient.getUserById(1L);
        return "Feign OK → User récupéré : " + user.getUsername();
    }

    @GetMapping("/test-rest")
    public ResponseEntity<String> testRest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        System.out.println("test-rest incoming Authorization header: " + token);

        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.set("Authorization", token);
        }
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(
            "http://localhost:8088/api/users/1",
            HttpMethod.GET,
            entity,
            String.class
        );

        System.out.println("test-rest response status: " + resp.getStatusCode());
        System.out.println("test-rest response body: " + resp.getBody());
        return resp;
    }
}
