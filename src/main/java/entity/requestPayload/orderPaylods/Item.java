package entity.requestPayload.orderPaylods;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Item {

    public String itemId;
    public Integer quantity;

}
