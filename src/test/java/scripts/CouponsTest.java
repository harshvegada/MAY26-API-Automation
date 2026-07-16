package scripts;

import constants.StatusCode;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import services.CouponsServices;
import services.LoginServices;
import services.RestaurantServices;

public class CouponsTest {

    LoginServices loginServices = new LoginServices();
    CouponsServices couponsServices = new CouponsServices();
    RestaurantServices restaurantServices = new RestaurantServices();

    @Test
    public void TC_API_COUPON_001_AddCouponAsOwner() {
        // Login as owner to add coupon
        loginServices.logInWithProfile("owner");

        // Get restaurant ID dynamically
        String restaurantId = restaurantServices.getRestaurantIdByName("Balance Brew Cafe");

        // Add a coupon with flat discount
        Response couponResponse = couponsServices.addCoupons(
                "SAVE50",
                50,
                "flat",
                restaurantId,
                "Restaurant Flat Discount",
                200,
                50
        );

        // Verify the coupon was created successfully
        Assert.assertEquals(couponResponse.getStatusCode(), StatusCode.COUPON_CREATION, "Coupon should be created successfully with status 201.");

        // Verify response contains coupon code
        String code = couponResponse.jsonPath().getString("code");
        Assert.assertEquals(code, "SAVE50", "Coupon code in response should match the requested code.");

        // Verify discount value
        Integer discount = couponResponse.jsonPath().getInt("discount");
        Assert.assertEquals(discount, 50, "Discount value should be 50.");

        // Verify discount type
        String discountType = couponResponse.jsonPath().getString("discountType");
        Assert.assertEquals(discountType, "flat", "Discount type should be flat.");

        // Verify restaurant id
        String responseRestaurantId = couponResponse.jsonPath().getString("restaurantId");
        Assert.assertEquals(responseRestaurantId, restaurantId, "Restaurant ID in response should match the requested restaurant ID.");
    }

    @Test
    public void TC_API_COUPON_002_AddCouponWithPercentageDiscount() {
        // Login as owner to add coupon
        loginServices.logInWithProfile("owner");

        // Get restaurant ID dynamically
        String restaurantId = restaurantServices.getRestaurantIdByName("Balance Brew Cafe");

        // Add a coupon with percentage discount
        Response couponResponse = couponsServices.addCoupons(
                "DISCOUNT20",
                20,
                "percentage",
                restaurantId,
                "20% Restaurant Discount",
                100,
                100
        );

        // Verify the coupon was created successfully
        Assert.assertEquals(couponResponse.getStatusCode(), StatusCode.COUPON_CREATION, "Coupon with percentage discount should be created successfully.");

        // Verify discount percentage
        Integer discount = couponResponse.jsonPath().getInt("discount");
        Assert.assertEquals(discount, 20, "Discount percentage should be 20.");

        // Verify usage limit
        Integer usageLimit = couponResponse.jsonPath().getInt("usageLimit");
        Assert.assertEquals(usageLimit, 100, "Usage limit should be 100.");
    }

}

