package utility;

import com.github.javafaker.Faker;

import java.util.Locale;

public class DataUtils {

    static Faker faker = new Faker();

    public static String getAddress() {
        return faker.address().fullAddress();
    }

    public static String getMobileNumber() {
        return faker.numerify("##########");
    }

}
