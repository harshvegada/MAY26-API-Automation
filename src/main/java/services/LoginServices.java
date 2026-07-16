package services;

import io.qameta.allure.Step;

public class LoginServices extends CommonServices {

    @Step("Login with profile {0}")
    public void logInWithProfile(String profile) {
        generateTokenFor(profile);
    }

}
