package base;

import constants.FileConstant;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import utility.PropertyReader;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class APIControlActions {

    static ThreadLocal<RequestSpecBuilder> requestSpecBuilderThread = new ThreadLocal<>();
    protected static ThreadLocal<String> authTokenThread = new ThreadLocal<>();
    PropertyReader propertyReader = new PropertyReader(FileConstant.CONFIG_PROPERTIES);

    private String getBaseURIBasedOnEnv() {
        return propertyReader.getValue(getENV() + ".BASEURI");
    }

    protected String getENV() {
        String env = System.getProperty("env") == null ? "QA" : System.getProperty("env");
        return env;
    }

    public Response executePostRequest(String endPoint, String payload) {
        Response response = given()
                .spec(requestSpecBuilderThread.get().build())
                .filter(new AllureRestAssured())
                .baseUri(getBaseURIBasedOnEnv())
                .when()
                .body(payload)
                .post(endPoint)
                .then()
                .extract().response();
        requestSpecBuilderThread.set(null);
        return response;
    }

    public Response executeGetRequest(String endPoint) {
        Response response = given()
                .spec(requestSpecBuilderThread.get().build())
                .baseUri(getBaseURIBasedOnEnv())
                .filter(new AllureRestAssured())
                .when()
                .get(endPoint)
                .then()
                .extract().response();
        requestSpecBuilderThread.set(null);
        return response;
    }

    public Response executePutRequest(String endPoint, String payload) {
        Response response = given()
                .spec(requestSpecBuilderThread.get().build())
                .filter(new AllureRestAssured())
                .baseUri(getBaseURIBasedOnEnv())
                .when()
                .body(payload)
                .put(endPoint)
                .then()
                .extract()
                .response();
        requestSpecBuilderThread.set(null);
        return response;
    }

    public void executePatchRequest(String endPoint) {
    }

    public void executeDeleteRequest(String endPoint) {
    }

    public void setHeaders(Map<String, String> headers) {
        initRequestSpecBuilder();
        if (authTokenThread.get() != null) {
            setToken();
        }
        requestSpecBuilderThread.get().addHeaders(headers);
    }

    public void setQueryParams(Map<String, String> queryParams) {
        initRequestSpecBuilder();
        requestSpecBuilderThread.get().addQueryParams(queryParams);
    }

    public void setQueryParam(String key, String value) {
        initRequestSpecBuilder();
        requestSpecBuilderThread.get().addQueryParam(key, value);
    }

    public void setBody(String payload) {
        initRequestSpecBuilder();
        requestSpecBuilderThread.get().setBody(payload);
    }

    public void setBody(Object payload) {
        initRequestSpecBuilder();
        requestSpecBuilderThread.get().setBody(payload);
    }

    public void setToken(String token) {
        initRequestSpecBuilder();
//        requestSpecBuilder.addHeader("Authorization", "Bearer " + token);
        authTokenThread.set(token);
    }

    public void setToken() {
        initRequestSpecBuilder();
        requestSpecBuilderThread.get().addHeader("Authorization", "Bearer " + authTokenThread.get());
    }

    private void initRequestSpecBuilder() {
        if (requestSpecBuilderThread.get() == null) {
            requestSpecBuilderThread.set(new RequestSpecBuilder());
        }
    }
}
