package scripts;

import constants.StatusCode;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import services.LoginServices;
import services.OrderServices;
import services.RestaurantServices;
import services.WalletServices;

public class MultiProfileTest {

    /**
     * When I POST "/api/orders" with one {{itemId}}, address "Flat 12, Kothrud", contactMobile "9990001234"
     * Then the response status is 201 and orderNumber saved as {{orderNumber}}, status "Pending"
     * When I GET "/api/orders/{{orderNumber}}"
     */

    LoginServices loginServices = new LoginServices();
    WalletServices walletServices = new WalletServices();
    OrderServices orderServices = new OrderServices();
    RestaurantServices restaurantServices = new RestaurantServices();

    @Test
    @Epic("Multi Profile Test Case")
    public void TC_API_E2E_MP_01() {
        String restaurantName = "Balance Brew Cafe";
        String menuName = "Cold Brew Coffee";
        int qnt = 100;
        String address = "Wakad";
        String mobileNumber = "7896451230";

        loginServices.logInWithProfile("owner");
        Allure.step("Login with Owner profile");
        restaurantServices.updateStockForItemAsOwn(restaurantName, menuName, qnt);
        Allure.step("Updated stock for the restaurant " + restaurantName);

        loginServices.logInWithProfile("customer");
        long beforePlacingOrderAmt = walletServices.getLatestTotalWalletAmt();
        Response orderResponseFromCustomer = orderServices.placeOrder(restaurantName, menuName, qnt, address, mobileNumber);

        long totalAmtOfOrder = orderResponseFromCustomer.jsonPath().getLong("totalAmount");

        String orderNumber = orderResponseFromCustomer.jsonPath().getString("orderNumber");
        Assert.assertEquals(orderResponseFromCustomer.statusCode(), StatusCode.ORDER_CREATION, "Order not placed..");
        Assert.assertEquals(orderResponseFromCustomer.jsonPath().getString("status"), "Pending", "Initial status is somethign else from Pending");


        Response orderResponse = orderServices.getOrderResponse(orderNumber);
        Assert.assertTrue(orderResponse.asString().contains(restaurantName));
        Assert.assertTrue(orderResponse.asString().contains(menuName));
        Assert.assertTrue(orderResponse.asString().contains(mobileNumber));
        Assert.assertTrue(orderResponse.asString().contains(address));

        long afterPlacingOrderAmt = walletServices.getLatestTotalWalletAmt();

        Assert.assertEquals(beforePlacingOrderAmt, (afterPlacingOrderAmt + totalAmtOfOrder));
    }

}
