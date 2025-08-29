package com.example.demo_mansi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class MainRunner implements CommandLineRunner {

    private final RestTemplate restTemplate;

    public MainRunner(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run(String... args) throws Exception {

        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Mansi Saxena");
        requestBody.put("regNo", "REG22664");
        requestBody.put("email", "mansi@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(generateUrl, entity, Map.class);

        if (response.getBody() == null) {
            System.out.println("No response from server!");
            return;
        }

        System.out.println("Raw Response: " + response.getBody());

        String webhookUrl = (String) response.getBody().get("webhookUrl");
        if (webhookUrl == null) {
            webhookUrl = (String) response.getBody().get("webhook");
        }

        String accessToken = (String) response.getBody().get("accessToken");


        System.out.println("Webhook: " + webhookUrl);
        System.out.println("Token: " + accessToken);

        String finalQuery = "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, "
                + "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT "
                + "FROM EMPLOYEE e1 "
                + "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID "
                + "LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT "
                + "AND e2.DOB > e1.DOB "
                + "GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME "
                + "ORDER BY e1.EMP_ID DESC;";

        Map<String, String> submitBody = new HashMap<>();
        submitBody.put("finalQuery", finalQuery);

        HttpHeaders submitHeaders = new HttpHeaders();
        submitHeaders.setContentType(MediaType.APPLICATION_JSON);
        submitHeaders.set("Authorization", accessToken);

        HttpEntity<Map<String, String>> submitEntity = new HttpEntity<>(submitBody, submitHeaders);

        ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhookUrl, submitEntity, String.class);

        System.out.println("Submission Response: " + submitResponse.getBody());
    }
}
