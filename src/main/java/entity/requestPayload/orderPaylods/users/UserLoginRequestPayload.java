package entity.requestPayload.orderPaylods.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import utility.ObjectToJsonString;

@Builder
@Getter
@Setter
public class UserLoginRequestPayload {

    public String email;
    public String password;
    public String app;

}

