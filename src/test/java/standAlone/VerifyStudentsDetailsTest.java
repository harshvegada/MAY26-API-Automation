package standAlone;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class VerifyStudentsDetailsTest {


    @Test
    public void verifyStudentsDetails() {
        String baseURL = "http://34.173.201.53";
        String basePath = "/api";

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "*/*");

        String body = "{\"studentId\":\"QJC89CQZPQ\",\"accessCode\":\"DKYDDWKY\"}";

        Response response = RestAssured.given().baseUri(baseURL).basePath(basePath).headers(headers).when().body(body).post("/access/verify").then().extract().response();

        Assert.assertEquals(response.statusCode(), 200, "Expected status code is 200");

        String grantToken = response.jsonPath().getString("grant");
        String studentId = response.jsonPath().getString("student.studentId");
        String studentName = response.jsonPath().getString("student.name");

        System.out.println("Grant Token: " + grantToken);
        System.out.println("Student ID: " + studentId);
        System.out.println("Student Name: " + studentName);
    }

    @Test
    public void verifyStudentsDetailsNotDisplayedForWrongStudentID() {
        String baseURL = "http://34.173.201.53";
        String basePath = "/api";
        String endPoint = "/access/verify";

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "*/*");

        String body = "{\"studentId\":\"QJC89CQZPQ1\",\"accessCode\":\"DKYDDWKY\"}";

        Response response = RestAssured.given().baseUri(baseURL).basePath(basePath).headers(headers).when().body(body).post(endPoint).then().extract().response();

        System.out.println("Response Time: " + response.time() + " ms");
        Assert.assertTrue(response.time() < 2000, "Response time is greater than 1 second");
        Assert.assertEquals(response.statusCode(), 403, "Expected status code is 403");
    }
}
