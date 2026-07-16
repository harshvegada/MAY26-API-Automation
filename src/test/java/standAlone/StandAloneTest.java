package standAlone;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class StandAloneTest {

    @Test
    public void script1() {
        String baseURL = "http://34.173.201.53/api";
        String restaurantEndPoint = "/restaurants";
        String authEndPoint = "/auth/login";

        String accessCode = "DKYDDWKY";
        String studentId = "QJC89CQZPQ";

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "*/*");
        headers.put("x-access-code", accessCode);
        headers.put("x-student-id", studentId);

        String payload = "{\"email\":\"user@technocredits.com\",\"password\":\"User@123\"}";

        Response response = given().baseUri(baseURL).headers(headers).when().body(payload).post(authEndPoint).then().extract().response();

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setBaseUri(baseURL);
        requestSpecBuilder.addHeaders(headers);
        requestSpecBuilder.setBody(payload);

        given().spec(requestSpecBuilder.build()).when().post(authEndPoint).then().extract().response();


        String token = response.jsonPath().getString("token");
        headers.put("Authorization", "Bearer " + token);

        Response restaurantResponse = given().baseUri(baseURL).headers(headers).when().get(restaurantEndPoint).then().extract().response();

        System.out.println(restaurantResponse.asString());
    }


}
