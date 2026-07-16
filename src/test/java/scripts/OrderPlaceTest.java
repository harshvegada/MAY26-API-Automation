package scripts;

import constants.StatusCode;
import entity.responsePayload.orderResponsePOJO.OrderResponsePOJO;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import services.LoginServices;
import services.OrderServices;
import services.RestaurantServices;
import utility.ExcelOperations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class OrderPlaceTest {

    LoginServices loginServices = new LoginServices();
    OrderServices orderServices = new OrderServices();
    RestaurantServices restaurantServices = new RestaurantServices();

    @DataProvider(name = "testData")
    public Object[][] dataProvider() throws Exception {
        List<Map<String, Object>> listOfRecords = ExcelOperations.readRecordFromExcel("src/test/resources/foodTestData.xlsx", "Sheet1");
        Object data[][] = new Object[listOfRecords.size()][1];
        for (int i = 0; i < listOfRecords.size(); i++)
            data[i][0] = listOfRecords.get(i);
        return data;
    }

    @Test(dataProvider = "testData")
    public void placeOrderAsUser(Map<String, Object> testDataFromExcel) {
        String actualRestaurantName = (String) testDataFromExcel.get("restaurantName");
        String actualDishName = (String) testDataFromExcel.get("foodItem");
        Double actualQnty = (Double) testDataFromExcel.get("qnt");
        Integer qnty = actualQnty != null ? (int) Math.round(actualQnty) : null;


        loginServices.logInWithProfile("owner");
        restaurantServices.updateStockForItemAsOwn(actualRestaurantName, actualDishName, qnty + 10);

        loginServices.generateTokenFor("customer");
        Response orderPlacedResponse = orderServices.placeOrder(actualRestaurantName, actualDishName, qnty);

        String orderNumber = orderPlacedResponse.jsonPath().getString("orderNumber");

        Assert.assertEquals(orderPlacedResponse.statusCode(), StatusCode.ORDER_CREATION, "Status Code mis-matched");
        Assert.assertEquals(orderPlacedResponse.jsonPath().getString("restaurantName"), actualRestaurantName);
        Assert.assertEquals(orderPlacedResponse.jsonPath().getString("items[0].itemName"), actualDishName);

        List<String> getAllOrderNumber = orderServices.getAllOrderNumber();

        System.out.println("order: " + orderNumber);
        System.out.println("All Orders: " + getAllOrderNumber);

        Assert.assertTrue(getAllOrderNumber.contains(orderNumber), "Latest Order Does not existing in records");
    }

    @Test
    public void verifySortingOfTable() throws ParseException {
        loginServices.generateTokenFor("customer");

        // Getting All the records without applying sorting
        Response rawResponse = orderServices.getAllOrders();
        List<String> rawRestaurantNames = rawResponse.jsonPath().getList("data.restaurantName");
        List<String> rawRestaurantOrderNumber = rawResponse.jsonPath().getList("data.orderNumber");

        Collections.sort(rawRestaurantNames);
        Collections.reverse(rawRestaurantNames);

        // Restaurant Name Sorting
        // Apply sorting mechanism & Get all the records
        Response sortedResponse = orderServices.getRecordForColumnInSorting("restaurantName", "desc");
        List<String> restaurantNameList = sortedResponse.jsonPath().getList("data.restaurantName");

        // Compare both list
        Assert.assertEquals(restaurantNameList, rawRestaurantNames, "Sorting API Failed");


        Collections.sort(rawRestaurantOrderNumber);
        // orderNumber sorting
        sortedResponse = orderServices.getRecordForColumnInSorting("orderNumber", "asc");
        List<String> restaurantOrderList = sortedResponse.jsonPath().getList("data.orderNumber");

        Assert.assertEquals(restaurantOrderList, rawRestaurantOrderNumber, "Order Number is not in ASC Order");


        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("status", "Cancelled");
        queryParam.put("limit", "all");

        Response rawResponseOfStatus = orderServices.getRecordOfOrderBasedOnQueryParam(queryParam);
        List<String> orderStatusList = rawResponseOfStatus.jsonPath().getList("data.status");

        for (String single : orderStatusList) {
            Assert.assertEquals(single, "Cancelled", "Other Status also get logged");
        }

        Map<String, String> queryParamDate = new HashMap<>();
        queryParamDate.put("sort", "date");
        queryParamDate.put("limit", "all");
        queryParamDate.put("order", "asc");

        Response rawResponseOfDates = orderServices.getRecordOfOrderBasedOnQueryParam(queryParamDate);
        List<String> listOfDates = rawResponseOfDates.jsonPath().getList("data.date");

//        List<String> rawDates = rawResponse.jsonPath().getList("data.date");
//        Collections.sort(rawDates);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String firstDate = listOfDates.get(0);

        Date previous = simpleDateFormat.parse(firstDate);

        for (int index = 1; index < listOfDates.size(); index++) {
            Date currentDate = simpleDateFormat.parse(listOfDates.get(index));

            boolean isAfter = currentDate.after(previous);
            Assert.assertTrue(isAfter, "on index Date sorting failed " + index);
            previous = currentDate;
        }
    }

    /**
     * Display all orders in the system by retrieving and deserializing the response
     * Demonstrates response deserialization using OrderResponsePOJO with assertions
     */
    @Test
    public void displayAllOrdersTest() {
        System.out.println("========== Retrieving All Orders ==========");

        // Login as customer
        loginServices.generateTokenFor("customer");

        // Get all orders from the system
        Response allOrdersResponse = orderServices.getAllOrders();

        // Assert response status is successful
        Assert.assertEquals(allOrdersResponse.getStatusCode(), 200, "Failed to retrieve all orders");

        // Deserialize the response to POJO
        OrderResponsePOJO orderResponsePOJO = allOrdersResponse.as(OrderResponsePOJO.class);

        // Assert response data is not null
        Assert.assertNotNull(orderResponsePOJO.data, "Order data should not be null");

        // Assert there are orders in the system
        Assert.assertFalse(orderResponsePOJO.data.isEmpty(), "There should be at least one order in the system");
        System.out.println("Total Orders: " + orderResponsePOJO.data.size());

        // Display order details
        for (int i = 0; i < orderResponsePOJO.data.size(); i++) {
            // Assert order number is not null or empty
            String orderNumber = orderResponsePOJO.getData().get(i).orderNumber;
            Assert.assertNotNull(orderNumber, "Order number at index " + i + " should not be null");
            Assert.assertFalse(orderNumber.isEmpty(), "Order number at index " + i + " should not be empty");

            // Assert restaurant name is not null or empty
            String restaurantName = orderResponsePOJO.getData().get(i).restaurantName;
            Assert.assertNotNull(restaurantName, "Restaurant name at index " + i + " should not be null");
            Assert.assertFalse(restaurantName.isEmpty(), "Restaurant name at index " + i + " should not be empty");

            // Assert items list is not null and has at least one item
            Assert.assertNotNull(orderResponsePOJO.getData().get(i).items, "Items list at index " + i + " should not be null");
            Assert.assertFalse(orderResponsePOJO.getData().get(i).items.isEmpty(), "Order at index " + i + " should have at least one item");

            // Assert item name is not null or empty
//            String itemName = orderResponsePOJO.getData().get(i).items.getFirst().itemName;
//            Assert.assertNotNull(itemName, "Item name at index " + i + " should not be null");
//            Assert.assertFalse(itemName.isEmpty(), "Item name at index " + i + " should not be empty");

            // Assert status is not null or empty
            String status = orderResponsePOJO.getData().get(i).status;
            Assert.assertNotNull(status, "Order status at index " + i + " should not be null");
            Assert.assertFalse(status.isEmpty(), "Order status at index " + i + " should not be empty");

//            System.out.println("Order " + (i + 1) + ": [" + orderNumber + "] - Restaurant: " + restaurantName + " | Item: " + itemName + " | Status: " + status);
        }
    }

    /**
     * Test to retrieve all orders and iterate through them
     * Demonstrates extracting specific item data from order response with assertions
     */
    @Test
    public void retrieveAndIterateOrdersTest() {
        System.out.println("========== Retrieve and Iterate Orders ==========");

        // Login as customer
        loginServices.generateTokenFor("customer");

        // Get all orders
        Response placedOrderResponse = orderServices.getAllOrders();

        // Assert response status is successful
        Assert.assertEquals(placedOrderResponse.getStatusCode(), 200, "Failed to retrieve orders");

        // Deserialize response to POJO
        OrderResponsePOJO orderResponsePOJO = placedOrderResponse.as(OrderResponsePOJO.class);

        // Assert response data is not null
        Assert.assertNotNull(orderResponsePOJO.data, "Order data should not be null");

        // Assert there are orders in the system
        Assert.assertFalse(orderResponsePOJO.data.isEmpty(), "There should be at least one order in the system");
        System.out.println("Total Orders Found: " + orderResponsePOJO.data.size());

        // Iterate through all orders and extract item names
        for (int i = 0; i < orderResponsePOJO.data.size(); i++) {
            // Assert order number is not null or empty
            String orderNumber = orderResponsePOJO.getData().get(i).orderNumber;
            Assert.assertNotNull(orderNumber, "Order number should not be null");
            Assert.assertFalse(orderNumber.isEmpty(), "Order number should not be empty");

            // Assert items list is not null and has at least one item
            Assert.assertNotNull(orderResponsePOJO.getData().get(i).items, "Items should not be null");
            Assert.assertFalse(orderResponsePOJO.getData().get(i).items.isEmpty(), "Order should have at least one item");

            // Assert item name is not null or empty
//            String itemName = orderResponsePOJO.getData().get(i).items.getFirst().itemName;
//            Assert.assertNotNull(itemName, "Item name should not be null");
//            Assert.assertFalse(itemName.isEmpty(), "Item name should not be empty");

//            System.out.println("Order " + (i + 1) + " [" + orderNumber + "] Item: " + itemName);
        }
    }
}
