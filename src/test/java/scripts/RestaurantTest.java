package scripts;

import constants.StatusCode;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import services.LoginServices;
import services.OrderServices;
import services.RestaurantServices;
import services.WalletServices;

public class RestaurantTest {

    LoginServices loginServices = new LoginServices();
    RestaurantServices restaurantServices = new RestaurantServices();
    WalletServices walletServices = new WalletServices();
    OrderServices orderServices = new OrderServices();

    @Test
    public void TC_API_ORD_05() {
        loginServices.logInWithProfile("owner");

        // Update the stock for "Paneer Tikka" to 0 for the restaurant "Balance Brew Cafe"
        restaurantServices.updateStockForItemAsOwn("Balance Brew Cafe", "Paneer Tikka", 0);

        loginServices.logInWithProfile("customer");
        long beforePlaceOrderWalletBalance = walletServices.getLatestTotalWalletAmt();

        //Place order for "Paneer Tikka" from "Balance Brew Cafe"
        Response orderResponse = orderServices.placeOrder("Balance Brew Cafe", "Paneer Tikka", 1);

        Assert.assertEquals(orderResponse.getStatusCode(), StatusCode.ORDER_OUT_OF_STOCK, "Order should not be placed successfully as the item is out of stock.");
        Assert.assertEquals(orderResponse.jsonPath().getString("error"),"cannot place order running out of stocks", "Error message should indicate that the item is out of stock.");

        long afterPlaceOrderWalletBalance = walletServices.getLatestTotalWalletAmt();
        Assert.assertEquals(afterPlaceOrderWalletBalance, beforePlaceOrderWalletBalance, "Order gets placed even it's Out of stock");
    }

}
