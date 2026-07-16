package entity.requestPayload.orderPaylods;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class OwnerUpdateQntyRequestPayload {

    public String name;
    public String category;
    public Integer price;
    public String description;
    public Integer stockLevel;

}
