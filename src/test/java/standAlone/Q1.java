package standAlone;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class Q1 {

    public static void main(String[] args) {
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

        Response response = RestAssured.given().baseUri(baseURL).headers(headers).when().body(payload).post(authEndPoint).then().extract().response();

        String token = response.jsonPath().getString("token");
        headers.put("authorization", "Bearer " + token);


        Response restaurantResponse = RestAssured.given().baseUri(baseURL).headers(headers).when().get("/restaurants").then().extract().response();

        int expectedPriceMoreThan = 300;
        Map<String, Integer> dish = new HashMap<>();

        for (int restaurantIndex = 0; restaurantIndex < restaurantResponse.jsonPath().getList("").size(); restaurantIndex++) {

            int totalMenuOfRestaurants = restaurantResponse.jsonPath().getList("[" + restaurantIndex + "].menu").size();

            for (int menuIndex = 0; menuIndex < totalMenuOfRestaurants; menuIndex++) {

                int actualPrice = restaurantResponse.jsonPath().getInt("[" + restaurantIndex + "].menu[" + menuIndex + "].price");

                if (actualPrice > expectedPriceMoreThan) {
                    String dishName = restaurantResponse.jsonPath().getString("[" + restaurantIndex + "].menu[" + menuIndex + "].name");
                    dish.put(dishName, actualPrice);
                }
            }
        }

        System.out.println(dish);


//        String expectedMenu = "Paneer Tikka";
//
//        List<String> restaurantNames = new ArrayList<>();
//
//        for (int restaurantIndex = 0; restaurantIndex < restaurantResponse.jsonPath().getList("").size(); restaurantIndex++) {
//
//            int totalNumberOfDishInRestaurant = restaurantResponse.jsonPath().getList("[" + restaurantIndex + "].menu").size();
//
//            for(int menuIndex = 0; menuIndex < totalNumberOfDishInRestaurant; menuIndex++){
//                String actualMenuName = restaurantResponse.jsonPath().getString("[" + restaurantIndex + "].menu[" + menuIndex +"].name");
//                if(expectedMenu.equalsIgnoreCase(actualMenuName)){
//                    String resName = restaurantResponse.jsonPath().getString("["+restaurantIndex+"].name");
//                    restaurantNames.add(resName);
//                }
//            }
//        }
//
//        System.out.println("Restaurant which servers " + expectedMenu + " : " + restaurantNames);

    }
}
