package entity.requestPayload.orderPaylods;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CouponRequestPayload {

    public String code;
    public Integer discount;
    public String discountType;
    public String restaurantId;
    public String description;
    public Integer threshold;
    public Integer usageLimit;

}

