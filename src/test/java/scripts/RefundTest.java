package scripts;

import com.fasterxml.jackson.core.JsonProcessingException;
import constants.FileConstant;
import constants.StatusCode;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import services.LoginServices;
import services.OrderServices;
import services.RestaurantServices;
import services.WalletServices;
import utility.JSONSchemaUtility;

import java.io.File;

public class RefundTest {

    LoginServices loginServices = new LoginServices();
    OrderServices orderServices = new OrderServices();
    WalletServices walletServices = new WalletServices();
    RestaurantServices restaurantServices = new RestaurantServices();

    @Test
    public void refundTestApproved() throws InterruptedException, JsonProcessingException {
        loginServices.logInWithProfile("owner");
        restaurantServices.updateStockForItemAsOwn("Balance Brew Cafe", "Cold Brew Coffee", 10);

        loginServices.generateTokenFor("customer");

        long beforePlaceOrderAmt = walletServices.getLatestTotalWalletAmt();
        Response orderPlacedResponse = orderServices.placeOrder("Balance Brew Cafe", "Cold Brew Coffee", 2);

        Assert.assertTrue(JSONSchemaUtility.compareSchemaWithResponse(orderPlacedResponse.asString(), new File(FileConstant.ORDER_PLACEMENT_SCHEMA)), "Order Placement Response body has been changed");

        String orderNumber = orderPlacedResponse.jsonPath().getString("orderNumber");
        long afterPlaceOrderAmt = walletServices.getLatestTotalWalletAmt();

        Assert.assertTrue(beforePlaceOrderAmt > afterPlaceOrderAmt, "Wallet Amt not getting deducted");

        Assert.assertEquals(orderPlacedResponse.statusCode(), StatusCode.ORDER_CREATION, "Status Code mis-matched");
        Thread.sleep(10000);

        Response refundResponse = orderServices.initRefundForOrder(orderNumber);
        Assert.assertEquals(refundResponse.statusCode(), StatusCode.REFUND_INIT_CODE, "Unable to init the refund process");

        loginServices.generateTokenFor("owner");

        Response refundFromOwnerResponse = orderServices.approvedRefundFor(orderNumber, "Approved API");
        int refundAmt = refundFromOwnerResponse.jsonPath().getInt("refund.amount");

        long afterRefundDone = afterPlaceOrderAmt + refundAmt;

        Assert.assertEquals(afterRefundDone, beforePlaceOrderAmt, "Wallet amt not getting updated after refund");
    }


    @Test
    public void refundTestRejected() throws InterruptedException, JsonProcessingException {
        loginServices.logInWithProfile("owner");
        restaurantServices.updateStockForItemAsOwn("Balance Brew Cafe", "Cold Brew Coffee", 10);

        loginServices.generateTokenFor("customer");

        long beforePlaceOrderAmt = walletServices.getLatestTotalWalletAmt();
        Response orderPlacedResponse = orderServices.placeOrder("Balance Brew Cafe", "Cold Brew Coffee", 2);

        String orderNumber = orderPlacedResponse.jsonPath().getString("orderNumber");
        long afterPlaceOrderAmt = walletServices.getLatestTotalWalletAmt();

        Assert.assertTrue(beforePlaceOrderAmt > afterPlaceOrderAmt, "Wallet Amt not getting deducted");

        Assert.assertEquals(orderPlacedResponse.statusCode(), StatusCode.ORDER_CREATION, "Status Code mis-matched");
        Thread.sleep(10000);

        Response refundResponse = orderServices.initRefundForOrder(orderNumber);
        Assert.assertEquals(refundResponse.statusCode(), StatusCode.REFUND_INIT_CODE, "Unabel to init the refund process");

        loginServices.generateTokenFor("owner");

        Response refundFromOwnerResponse = orderServices.rejectRefundFor(orderNumber, "Rejected Refund");
        int refundAmt = refundFromOwnerResponse.jsonPath().getInt("refund.amount");

        Assert.assertNotEquals(beforePlaceOrderAmt, afterPlaceOrderAmt);

    }
}
