package constants;

public class APIEndPoints {
    private APIEndPoints() {
    }

    public static final String PROFILE_ME = "/auth/me";

    public static final String AUTH_LOGIN = "/auth/login";

    //RESTAURANT
    public static final String RESTAURANTS = "/restaurants";
    public static final String MENU = "/menu";

    //WALLET
    public static final String WALLET = "/wallet";

    //COUPONS
    public static final String COUPONS = "/coupons";

    //ORDER
    public static final String ORDER = "/orders";
    public static final String REFUND = "/refund";

    public static String restaurantMenuById(String restaurantId, String menuId) {
        return RESTAURANTS + "/" + restaurantId + MENU + "/" + menuId;
    }

}
