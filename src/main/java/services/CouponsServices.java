package services;

import constants.APIEndPoints;
import entity.requestPayload.orderPaylods.CouponRequestPayload;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import utility.ObjectToJsonString;

public class CouponsServices extends CommonServices {

    @Step("Add coupon with code {0} for restaurant {1}")
    public Response addCoupons(String code, Integer discount, String discountType, String restaurantId, String description, Integer threshold, Integer usageLimit) {
        setHeaders(commonHeaders());

        CouponRequestPayload couponRequestPayload = CouponRequestPayload.builder()
                .code(code)
                .discount(discount)
                .discountType(discountType)
                .restaurantId(restaurantId)
                .description(description)
                .threshold(threshold)
                .usageLimit(usageLimit)
                .build();

        String payload = ObjectToJsonString.converClassToJsonString(couponRequestPayload);
        return executePostRequest(APIEndPoints.COUPONS, payload);
    }


}
