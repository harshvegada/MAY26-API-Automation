package scripts;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class JSONPathTest {

    @Test
    public void m1() {

        Response response = given().when().get("https://mocki.io/v1/3e710b22-0bf3-4d53-9eac-899cbddaa15e").then().extract().response();

        //Find Emp Name where ID = 2
//        List list = response.jsonPath().getList("employees");
//        for (Object empObj : list) {
//            Map<String, Object> map = (Map) empObj;
//            if ((Integer) map.get("id") == 2) {
//                System.out.println(map.get("name"));
//            }
//        }

        //Find Emp Name where ID = 2
        List<String> name = (List<String>) response.jsonPath().get("employees.find{ it.id == 2 }.skills");
        System.out.println(name);
        System.out.println("=========================================");

        //Find All Emp who having active as true
//        List list = response.jsonPath().get("employees.findAll { it.active == true }");
        List activeLIst = response.jsonPath().get("employees.findAll { it.active }");
        List inactiveList = response.jsonPath().get("employees.findAll { !it.active }");

        System.out.println(activeLIst);
        System.out.println("=========================================");
        System.out.println(inactiveList);

        System.out.println("=========================================");

        //To get All the skills under the emp -> List inside list [[],[],[]]
        // flatten() method --> it will convert all the sublist to single list
        List<String> list = (List) response.jsonPath().get("employees.skills.flatten()");
        System.out.println(list);
        System.out.println("=========================================");

        //Do Sum of All employee salary
        List<String> salaryList = response.jsonPath().get("employees.findAll { it.salary }.salary");
        System.out.println(salaryList);
        int sum = 0;
        for (String singleSalary : salaryList)
            sum += Integer.parseInt(singleSalary);
        System.out.println(sum);
        System.out.println("----------------------------");
        System.out.println((Integer) response.jsonPath().get("employees.collect { it.salary.toInteger() }.sum()"));

        System.out.println("=========================================");
        //Find Max Salary from emp
//        System.out.println((String) response.jsonPath().get("employees.salary.max()"));;
        // If you get salary in String then you need to convert to int & then max function called
//        System.out.println((Integer) response.jsonPath().get("employees.collect { it.salary.toInteger() }.max()"));
        //Max Age from emp
        System.out.println((Integer) response.jsonPath().get("employees.age.max()"));
        System.out.println((Integer) response.jsonPath().get("employees.age.min()"));

        System.out.println("=========================================");

        //Check if there any emp with active flag as false
        boolean anyCheck = response.jsonPath().get("employees.any { !it.salary }");
        System.out.println(anyCheck);

        System.out.println("=========================================");
        // To Check every emp must have active as true
        boolean everyCheck = response.jsonPath().get("employees.every { it.active }");
        System.out.println(everyCheck);

        System.out.println("=========================================");

        //Looking for all emp name who having salary more than 60K & still active as true
        List<String> nameList = response.jsonPath().get("employees.findAll { it.active && it.salary.toInteger() > 60000 }.name");
        System.out.println(nameList);
    }

}
