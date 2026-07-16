package entity.requestPayload.orderPaylods;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Builder
@Getter
@Setter
public class UserOrderPlaceRequestPayload {

    public String restaurantId;
    public ArrayList<Item> items;
    public String deliveryAddress;
    public String contactMobile;
    public String paymentMethod;

}
