package services;

import constants.APIEndPoints;
import entity.requestPayload.orderPaylods.OwnerUpdateQntyRequestPayload;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import utility.ObjectToJsonString;

import java.util.List;

public class RestaurantServices extends CommonServices {

    @Step("Get Restaurant ID by Name {0}")
    public String getRestaurantIdByName(String restaurantName) {
        setHeaders(commonHeaders());
        Response response = executeGetRequest(APIEndPoints.RESTAURANTS);
        List<String> restaurantNames = response.jsonPath().getList("name");
        int index = restaurantNames.indexOf(restaurantName);
        return response.jsonPath().getString("[" + index + "].id");
    }

    @Step("Get Menu item ID by restaurant name {0} and menu item {1}")
    public String getItemIDFromRestaurant(String restaurantName, String menuName) {
        setHeaders(commonHeaders());
        Response response = executeGetRequest(APIEndPoints.RESTAURANTS);
        List<String> restaurantNames = response.jsonPath().getList("name");
        int restaurantIndex = restaurantNames.indexOf(restaurantName);
        List<String> menuList = response.jsonPath().getList("[" + restaurantIndex + "].menu.name");
        int menuIndex = menuList.indexOf(menuName);
        return response.jsonPath().getString("[" + restaurantIndex + "].menu[" + menuIndex + "].itemId");
    }

    @Step("Update stock level of item {1} to {2} in restaurant {0} as Owner")
    public void updateStockForItemAsOwn(String restaurantName, String itemName, int stock) {
        OwnerUpdateQntyRequestPayload updateObjectPayload = OwnerUpdateQntyRequestPayload.builder()
                .name(itemName)
                .price(150)
                .stockLevel(stock)
                .description("Update stock for item")
                .category("Veg")
                .build();

        String resId = getRestaurantIdByName(restaurantName);
        String menuId = getItemIDFromRestaurant(restaurantName, itemName);

        String payload = ObjectToJsonString.converClassToJsonString(updateObjectPayload);

        setHeaders(commonHeaders());
        executePutRequest(APIEndPoints.restaurantMenuById(resId, menuId), payload);
    }
}
