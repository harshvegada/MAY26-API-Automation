package entity.responsePayload.menuRequestPOJO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class MenuAddingRequestPayload {
    public String name;
    public String description;
    public Integer price;
    public String category;
    public Integer stockLevel;
}