package entity.responsePayload.orderResponsePOJO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Builder
@Getter
@Setter
public class Datum {
    public String orderNumber;
    public String date;
    public String userId;
    public String username;
    public String restaurantId;
    public String restaurantName;
    public String ownerEmail;
    public String locality;
    public ArrayList<Item> items;
    public String deliveryAddress;
    public String contactMobile;
    public Integer subtotal;
    public String couponApplied;
    public Integer discount;
    public Integer totalAmount;
    public String status;
    public boolean accelerated;
    public String timeRemaining;
    public Object rating;
    public Object ratingComment;
    public Object ratedAt;
    public Refund refund;
}

