package entity.responsePayload.orderResponsePOJO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Item {
    public String itemId;
    public String itemName;
    public String category;
    public Integer quantity;
    public Integer price;
}