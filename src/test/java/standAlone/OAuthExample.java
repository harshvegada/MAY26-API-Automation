package standAlone;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OAuthExample {


    @Test
    public void m1() {

        RestAssured.baseURI = "http://localhost:5000";

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("client_id", "mock_client_id_technocredits_12345");
        queryParams.put("redirect_uri", "http://localhost:5000/auth/google/callback");
        queryParams.put("response_type", "code");
        queryParams.put("scope", "openid email profile");
        queryParams.put("state", "72ed86eba0883760f3708aa1f3157311");


        Response response = given()
                .queryParams(queryParams)
                .when()
                .get("/auth/mock-google/authorize")
                .then()
                .extract().response();

        Assert.assertEquals(200, response.statusCode());

        Map<String, String> responseCookies = response.cookies();

        queryParams.put("password", "123456");

        Response authorizedRes = given()
                .log().all()
                .cookies(responseCookies)
                .formParams(queryParams)
                .when()
                .post("/auth/mock-google/authorize")
                .then()
                .extract().response();

        System.out.println(authorizedRes.prettyPrint());

    }


}
