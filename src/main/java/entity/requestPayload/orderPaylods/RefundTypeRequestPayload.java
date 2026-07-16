package entity.requestPayload.orderPaylods;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class RefundTypeRequestPayload {

    String action;
    String note;
}
