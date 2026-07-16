package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import constants.APIEndPoints;
import entity.requestPayload.orderPaylods.*;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.Assert;
import utility.DataUtils;
import utility.ObjectToJsonString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderServices extends CommonServices {

    RestaurantServices restaurantServices = new RestaurantServices();

    @Step("Place order from restaurant name {0} and menu is {1} and quantity is {2}")
    public Response placeOrder(String restaurantName, String menuName, int qnt) {

        //Getting Restaurant information
        String restaurantID = restaurantServices.getRestaurantIdByName(restaurantName);
        String itemID = restaurantServices.getItemIDFromRestaurant(restaurantName, menuName);

        //Dynamic Data Generation
        String address = DataUtils.getAddress();
        String mobileNumber = DataUtils.getMobileNumber();

        ArrayList<Item> itemArrayList = new ArrayList<>();
        itemArrayList.add(Item.builder().itemId(itemID).quantity(qnt).build());

        UserOrderPlaceRequestPayload userRequestPayload = UserOrderPlaceRequestPayload.builder()
                .restaurantId(restaurantID)
                .deliveryAddress(address)
                .contactMobile(mobileNumber)
                .paymentMethod("UPI")
                .items(itemArrayList)
                .build();

        String userPlaceOrderPayload = ObjectToJsonString.converClassToJsonString(userRequestPayload);

        //User Placed Order
        setHeaders(commonHeaders());
        return executePostRequest(APIEndPoints.ORDER, userPlaceOrderPayload);
    }

    @Step("Place order from {0} restaurant with food item {1} with quantity {2} to the address {3} and contact number {4}")
    public Response placeOrder(String restaurantName, String menuName, int qnt, String address, String mobileNumber) {
        //Getting Restaurant information
        String restaurantID = restaurantServices.getRestaurantIdByName(restaurantName);
        String itemID = restaurantServices.getItemIDFromRestaurant(restaurantName, menuName);

        ArrayList<Item> itemArrayList = new ArrayList<>();
        itemArrayList.add(Item.builder().itemId(itemID).quantity(qnt).build());

        UserOrderPlaceRequestPayload userRequestPayload = UserOrderPlaceRequestPayload.builder()
                .restaurantId(restaurantID)
                .deliveryAddress(address)
                .contactMobile(mobileNumber)
                .paymentMethod("UPI")
                .items(itemArrayList)
                .build();

        String userPlaceOrderPayload = ObjectToJsonString.converClassToJsonString(userRequestPayload);

        //User Placed Order
        setHeaders(commonHeaders());
        return executePostRequest(APIEndPoints.ORDER, userPlaceOrderPayload);
    }

    @Step("Get All Orders")
    public Response getAllOrders() {
        setHeaders(commonHeaders());
        setQueryParam("limit", "all");

        return executeGetRequest(APIEndPoints.ORDER);
    }

    @Step("Get All the Order Number")
    public List<String> getAllOrderNumber() {
        return getAllOrders().jsonPath().getList("data.orderNumber");
    }

    @Step("Retrieve all restaurant names from the system")
    public List<String> getAllRestaurantNames() {
        return getAllOrders().jsonPath().getList("data.restaurantName");
    }

    @Step("Getting Records for column {0} in sorting type {1}")
    public Response getRecordForColumnInSorting(String columnName, String sortingType) {
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("sort", columnName);
        queryParam.put("order", sortingType);
        queryParam.put("limit", "all");

        setQueryParams(queryParam);
        setHeaders(commonHeaders());
        return executeGetRequest(APIEndPoints.ORDER);
    }

    @Step("Get Records Based on given parameter {0}")
    public Response getRecordOfOrderBasedOnQueryParam(Map<String, String> queryParams) {
        setQueryParams(queryParams);
        setHeaders(commonHeaders());

        return executeGetRequest(APIEndPoints.ORDER);
    }

    @Step("Init the Refund process for order {0}")
    public Response initRefundForOrder(String orderNumber) throws JsonProcessingException {
        setHeaders(commonHeaders());
//        String payload = "{\"action\":\"request\",\"reason\":\"Something Refund\"}";
        RefundInitRequestPayload refundObject = RefundInitRequestPayload.builder()
                .action("request")
                .reason("Something Refund")
                .build();


        String payload = ObjectToJsonString.converClassToJsonString(refundObject);

        return executePutRequest(APIEndPoints.ORDER + "/" + orderNumber + APIEndPoints.REFUND, payload);
    }

    @Step("Approved refund for the order number {0} and note as {1}")
    public Response approvedRefundFor(String orderNumber, String note) throws JsonProcessingException {
        setHeaders(commonHeaders());
//        String payload = "{\"action\":\"approve\",\"note\":\"" + note + "\"}";
        RefundTypeRequestPayload approveObject = RefundTypeRequestPayload.builder().note(note).action("approve").build();

        String payload = ObjectToJsonString.converClassToJsonString(approveObject);

        return executePutRequest(APIEndPoints.ORDER + "/" + orderNumber + APIEndPoints.REFUND, payload);
    }

    @Step("Rejected refund for the order number {0} and note as {1}")
    public Response rejectRefundFor(String orderNumber, String note) throws JsonProcessingException {
        setHeaders(commonHeaders());
//        String payload = "{\"action\":\"reject\",\"note\":\"" + note + "\"}";
        RefundTypeRequestPayload rejectedObject = RefundTypeRequestPayload.builder().note(note).action("approve").build();

        String payload = ObjectToJsonString.converClassToJsonString(rejectedObject);

        return executePutRequest(APIEndPoints.ORDER + "/" + orderNumber + APIEndPoints.REFUND, payload);
    }

    @Step("Retrieve order details by order number {0}")
    public Response getOrderResponse(String orderNumber) {
        setHeaders(commonHeaders());
        return executeGetRequest(APIEndPoints.ORDER + "/" + orderNumber);
    }

}
