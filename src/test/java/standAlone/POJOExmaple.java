package standAlone;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

public class POJOExmaple {

    String id;
    String username;
    String email;
    String role;
    String mobile;
    Integer wallet;
    Boolean active;
    String createdAt;
    List<String> addresses;
    String country;
    String currency;
    List<String> members;

    public static void main(String[] args) {
        POJOExmaple pojoExmaple = new POJOExmaple();
        pojoExmaple.setActive(true);
        pojoExmaple.setAddresses(Arrays.asList(""));

        System.out.println(pojoExmaple);
    }

    @Override
    public String toString(){
        return " \"id\": \""+id+"\",\n" +
                "    \"username\": \""+username+"\",\n" +
                "    \"email\": \""+email+"\",\n" +
                "    \"role\": \""+role+"\",\n" +
                "    \"mobile\": null,\n" +
                "    \"wallet\": "+wallet+",\n" +
                "    \"active\": true,\n" +
                "    \"createdAt\": \"2026-07-10T08:38:18.071Z\",\n" +
                "    \"addresses\": [],\n" +
                "    \"country\": \"IN\",\n" +
                "    \"currency\": \"INR\",\n" +
                "    \"members\": []";
    }

    @JsonProperty("id")
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("username")
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("email")
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("role")
    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty("mobile")
    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @JsonProperty("wallet")
    public Integer getWallet() {
        return this.wallet;
    }

    public void setWallet(int wallet) {
        this.wallet = wallet;
    }

    @JsonProperty("active")
    public Boolean getActive() {
        return this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @JsonProperty("createdAt")
    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("addresses")
    public List<String> getAddresses() {
        return this.addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    @JsonProperty("country")
    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("currency")
    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("members")
    public List<String> getMembers() {
        return this.members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

}
