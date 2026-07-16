package services;

import constants.APIEndPoints;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

public class MyProfileService extends CommonServices {


    @Step("Retrieve all profile addresses for the authenticated user")
    public List getProfileAddress() {
        setHeaders(commonHeaders());
        Response response = executeGetRequest(APIEndPoints.PROFILE_ME);
        List list = response.jsonPath().getList("addresses");
        return list;
//        for (int i = 0; i < list.size(); i++) {
//            Map<String, Object> entryObj = (Map<String, Object>) list.get(i);
//            boolean isDefault = (Boolean) entryObj.get("isDefault");
//            if (isDefault)
//                Assert.assertEquals(entryObj.get("category"), "HOME", "Default address might be different");
//        }
    }

    @Step("Add a new address with category {0}, address {1} and label {2} to the profile")
    public void addAddress(String category, String address, String label){

    }


}
