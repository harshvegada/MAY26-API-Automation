package services;

import constants.APIEndPoints;
import io.qameta.allure.Step;

public class WalletServices extends CommonServices {


    @Step("Retrieve current wallet balance for the authenticated user")
    public long getLatestTotalWalletAmt(){
        setHeaders(commonHeaders());
        return executeGetRequest(APIEndPoints.WALLET).jsonPath().getLong("balance");
    }

}
