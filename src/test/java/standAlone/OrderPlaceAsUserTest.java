package standAlone;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OrderPlaceAsUserTest {


    @Test
    public void placeOrderAsUser() throws IOException {
        String baseURL = "http://34.173.201.53/api";
        String restaurantEndPoint = "/restaurants";
        String authEndPoint = "/auth/login";

        String accessCode = "DKYDDWKY";
        String studentId = "QJC89CQZPQ";

        String restaurantName = "Balance Brew Cafe";
        String menuName = "Cold Brew Coffee";
        int quantity = 100;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "*/*");
        headers.put("x-access-code", accessCode);
        headers.put("x-student-id", studentId);

        String payload = "{\"email\":\"user@technocredits.com\",\"password\":\"User@123\"}";

        Response response = RestAssured.given().baseUri(baseURL).headers(headers).when().body(payload).post(authEndPoint).then().extract().response();

        String token = response.jsonPath().getString("token");

        headers.put("Authorization", "Bearer " + token);

        Response restaurantResponse = RestAssured.given().baseUri(baseURL).headers(headers).when().get(restaurantEndPoint).then().extract().response();

//        System.out.println("Restaurant Response: " + restaurantResponse.prettyPrint());

//        System.out.println(restaurantResponse.jsonPath().getList("").size());

        String restaurantId = "";
        String itemId = "";

        for (int i = 0; i < restaurantResponse.jsonPath().getList("").size(); i++) {
            if (restaurantResponse.jsonPath().getString("[" + i + "].name").equals(restaurantName)) {
                restaurantId = restaurantResponse.jsonPath().getString("[" + i + "].id");
                for (int j = 0; j < restaurantResponse.jsonPath().getList("[" + i + "].menu").size(); j++) {
                    if (restaurantResponse.jsonPath().getString("[" + i + "].menu[" + j + "].name").equals(menuName)) {
                        itemId = restaurantResponse.jsonPath().getString("[" + i + "].menu[" + j + "].itemId");
                        break;
                    }
                }
            }
        }

        String payloadForOrder = "{\n" + "  \"restaurantId\": \"" + restaurantId + "\",\n" + "  \"items\": [\n" + "    {\n" + "      \"itemId\": \"" + itemId + "\",\n" + "      \"quantity\": " + quantity + "\n" + "    }\n" + "  ],\n" + "  \"deliveryAddress\": \"Wakad\",\n" + "  \"contactMobile\": \"7895461202\"\n" + "}";

        String orderEndPoint = "/orders";

        int newStock = quantity + 10;
        ownerUpdatedStockForItem(restaurantName, menuName, newStock);
        addValueToWallet(10000);

        Response orderResponse = RestAssured.given().log().all().baseUri(baseURL).headers(headers).when().body(payloadForOrder).post(orderEndPoint).then().extract().response();

        System.out.println(orderResponse.prettyPrint());

        Assert.assertEquals(orderResponse.statusCode(), 201, "Expected status code is 201");
        Assert.assertEquals(orderResponse.jsonPath().getString("restaurantName"), restaurantName, "Expected restaurant name is " + restaurantName);

        String orderNumber = orderResponse.jsonPath().getString("orderNumber");

        //Download Invoice API
        Response downloadInvoiceResponse = RestAssured.given().baseUri(baseURL).pathParam("orderNumber", orderNumber).headers(headers).when().get("/uploads/orders/{orderNumber}/invoice.txt").then().extract().response();

        Assert.assertEquals(downloadInvoiceResponse.statusCode(), 200, "Expected status code is 200");

        File directory = new File("target/invoice");
        if (!directory.exists()) {
            directory.mkdir();
        }

        File downloadInvoice = new File("target/invoice/downloaded_invoice" + orderNumber + ".txt");

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(downloadInvoice));
        bufferedWriter.write(downloadInvoiceResponse.getBody().asString());
        bufferedWriter.close();

    }


    @Test
    public void placeOrderWithOutOfStockItem() {
        String baseURL = "http://34.173.201.53/api";
        String restaurantEndPoint = "/restaurants";
        String authEndPoint = "/auth/login";

        String accessCode = "DKYDDWKY";
        String studentId = "QJC89CQZPQ";

        String restaurantName = "Balance Brew Cafe";
        String menuName = "Cold Brew Coffee";
        int quantity = 150;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "*/*");
        headers.put("x-access-code", accessCode);
        headers.put("x-student-id", studentId);


        String payload = "{\"email\":\"user@technocredits.com\",\"password\":\"User@123\"}";

        Response response = RestAssured.given().baseUri(baseURL).headers(headers).when().body(payload).post(authEndPoint).then().extract().response();

        String token = response.jsonPath().getString("token");
//        System.out.println("Token: " + token);

        headers.put("Authorization", "Bearer " + token);

        Response restaurantResponse = RestAssured.given().baseUri(baseURL).headers(headers).when().get(restaurantEndPoint).then().extract().response();

//        System.out.println("Restaurant Response: " + restaurantResponse.prettyPrint());

//        System.out.println(restaurantResponse.jsonPath().getList("").size());

        String restaurantId = "";
        String itemId = "";

        for (int i = 0; i < restaurantResponse.jsonPath().getList("").size(); i++) {
            if (restaurantResponse.jsonPath().getString("[" + i + "].name").equals(restaurantName)) {
                restaurantId = restaurantResponse.jsonPath().getString("[" + i + "].id");
                for (int j = 0; j < restaurantResponse.jsonPath().getList("[" + i + "].menu").size(); j++) {
                    if (restaurantResponse.jsonPath().getString("[" + i + "].menu[" + j + "].name").equals(menuName)) {
                        itemId = restaurantResponse.jsonPath().getString("[" + i + "].menu[" + j + "].itemId");
                        break;
                    }
                }
            }
        }

        String payloadForOrder = "{\n" + "  \"restaurantId\": \"" + restaurantId + "\",\n" + "  \"items\": [\n" + "    {\n" + "      \"itemId\": \"" + itemId + "\",\n" + "      \"quantity\": " + quantity + "\n" + "    }\n" + "  ],\n" + "  \"deliveryAddress\": \"Wakad\",\n" + "  \"contactMobile\": \"7895461202\"\n" + "}";

        String orderEndPoint = "/orders";

        Response orderResponse = RestAssured.given().log().all().baseUri(baseURL).headers(headers).when().body(payloadForOrder).post(orderEndPoint).then().extract().response();


        Assert.assertEquals(orderResponse.jsonPath().getString("error"), "cannot place order running out of stocks");
        Assert.assertEquals(orderResponse.statusCode(), 400, "Expected status code is 400");

    }


    public void ownerUpdatedStockForItem(String restaurantName, String menuName, int newStock) {
        String baseURL = "http://34.173.201.53/api";
        String authEndPoint = "/auth/login";

        String accessCode = "DKYDDWKY";
        String studentId = "QJC89CQZPQ";

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "*/*");
        headers.put("x-access-code", accessCode);
        headers.put("x-student-id", studentId);


        String payload = "{\"email\":\"owner.cafe@technocredits.com\",\"password\":\"Owner@123\"}";

        Response response = RestAssured.given().baseUri(baseURL).headers(headers).when().body(payload).post(authEndPoint).then().extract().response();

        String token = response.jsonPath().getString("token");
        headers.put("Authorization", "Bearer " + token);


        String restaurantId = "";
        String itemId = "";
        String restaurantEndPoint = "/restaurants";

        Response restaurantResponse = RestAssured.given().baseUri(baseURL).headers(headers).when().get(restaurantEndPoint).then().extract().response();


        for (int i = 0; i < restaurantResponse.jsonPath().getList("").size(); i++) {
            if (restaurantResponse.jsonPath().getString("[" + i + "].name").equals(restaurantName)) {
                restaurantId = restaurantResponse.jsonPath().getString("[" + i + "].id");
                for (int j = 0; j < restaurantResponse.jsonPath().getList("[" + i + "].menu").size(); j++) {
                    if (restaurantResponse.jsonPath().getString("[" + i + "].menu[" + j + "].name").equals(menuName)) {
                        itemId = restaurantResponse.jsonPath().getString("[" + i + "].menu[" + j + "].itemId");
                        break;
                    }
                }
            }
        }

        String api = "http://34.173.201.53/api/restaurants/{restaurantID}/menu/{itemID}";

        String payloadForUpdateMenu = "{\n" + "  \"name\": \"" + menuName + "\",\n" + "  \"description\": \"Slow-steeped 18h cold brew\",\n" + "  \"price\": 180,\n" + "  \"category\": \"Veg\",\n" + "  \"stockLevel\": " + newStock + "\n" + "}";

        Response updatedMenuResponse = RestAssured.given().headers(headers).pathParam("restaurantID", restaurantId).pathParam("itemID", itemId).when().body(payloadForUpdateMenu).put(api).then().extract().response();

        Assert.assertEquals(updatedMenuResponse.statusCode(), 200, "Expected status code is 200");
    }


    public void addValueToWallet(int amount) {
        String baseURL = "http://34.173.201.53/api";
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
        headers.put("Authorization", "Bearer " + token);

        String payloadForWallet = "{\n" + "  \"amount\": " + amount + "\n" + "}";

        Response amtResponse = RestAssured.given().baseUri("http://34.173.201.53").headers(headers).when().body(payloadForWallet).post("/api/wallet/add").then().extract().response();

        Assert.assertEquals(amtResponse.statusCode(), 200, "Expected status code is 200");

    }


}
