package utility;

import io.restassured.module.jsv.JsonSchemaValidator;

import java.io.File;

public class JSONSchemaUtility {

    public static void compareSchemaWithResponse(String responseBody, String fileContent){

    }

    public static boolean compareSchemaWithResponse(String responseBody, File filePath){
        return JsonSchemaValidator.matchesJsonSchema(filePath).matches(responseBody);
    }

}
