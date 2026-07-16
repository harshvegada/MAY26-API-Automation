package services;

import base.APIControlActions;
import constants.APIEndPoints;
import constants.FileConstant;
import entity.requestPayload.orderPaylods.users.UserLoginRequestPayload;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import utility.ObjectToJsonString;
import utility.PropertyReader;

import java.util.HashMap;
import java.util.Map;

public class CommonServices extends APIControlActions {

    PropertyReader propertyReader = new PropertyReader(FileConstant.CONFIG_PROPERTIES);

    @Step("Generating token for profile {0}")
    public void generateTokenFor(String profile) {
        switch (profile.toLowerCase()) {
            case "customer":
                tokenForCustomer();
                break;

            case "owner":
                tokenForOwner();
                break;

            case "admin":
                tokenForAdmin();
                break;
        }
    }

    @Step("Authenticate and generate token for Customer using credentials from config")
    public void tokenForCustomer() {
        setHeaders(commonHeaders());
        UserLoginRequestPayload loginRequestPayload = UserLoginRequestPayload.builder()
                .email(propertyReader.getValue(getENV() + ".USEREMAIL"))
                .password(propertyReader.getValue(getENV() + ".USERPWD"))
                .app("food")
                .build();
        String payload = ObjectToJsonString.converClassToJsonString(loginRequestPayload);
        Response response = executePostRequest(APIEndPoints.AUTH_LOGIN, payload);
        String token = response.jsonPath().getString("token");
        authTokenThread.set(token);
    }

    @Step("Authenticate and generate token for Owner using credentials from config")
    public void tokenForOwner() {
        setHeaders(commonHeaders());
        UserLoginRequestPayload loginRequestPayload = UserLoginRequestPayload.builder()
                .email(propertyReader.getValue(getENV() + ".OWNEREMAIL"))
                .password(propertyReader.getValue(getENV() + ".OWNERPWD"))
                .app("food")
                .build();
        String payload = ObjectToJsonString.converClassToJsonString(loginRequestPayload);
        Response response = executePostRequest(APIEndPoints.AUTH_LOGIN, payload);
        String token = response.jsonPath().getString("token");
        authTokenThread.set(token);
    }

    @Step("Authenticate and generate token for Admin using credentials from config")
    public void tokenForAdmin() {
        setHeaders(commonHeaders());
        UserLoginRequestPayload loginRequestPayload = UserLoginRequestPayload.builder()
                .email(propertyReader.getValue(getENV() + ".ADMINEMAIL"))
                .password(propertyReader.getValue(getENV() + ".ADMINPWD"))
                .app("food")
                .build();
        String payload = ObjectToJsonString.converClassToJsonString(loginRequestPayload);
        Response response = executePostRequest(APIEndPoints.AUTH_LOGIN, payload);
        String token = response.jsonPath().getString("token");
        authTokenThread.set(token);
    }

    @Step("Build common request headers with Content-Type, Accept, X-Access-Code and X-Student-Id")
    public Map<String, String> commonHeaders() {
        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", "application/json");
        headers.put("Accept", "*/*");
        headers.put("x-access-code", "DKYDDWKY");
        headers.put("x-student-id", "QJC89CQZPQ");

        return headers;
    }

}
