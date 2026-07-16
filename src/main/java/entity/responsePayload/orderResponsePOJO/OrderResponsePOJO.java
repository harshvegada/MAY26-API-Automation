package entity.responsePayload.orderResponsePOJO;

import lombok.Getter;

import java.util.ArrayList;


@Getter
public class OrderResponsePOJO {
    public ArrayList<Datum> data;
    public Integer total;
    public Integer page;
    public Integer limit;
    public Integer totalPages;
    public Boolean paginated;
}
