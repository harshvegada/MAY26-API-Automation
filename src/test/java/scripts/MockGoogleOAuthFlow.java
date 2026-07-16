package scripts;

import io.restassured.RestAssured;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.response.Response;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * End-to-end mock-Google OAuth 2.0 flow against the Technocredits testbed:
 * start -> mock authorize (form POST) -> callback -> session-token (JWT)
 * -> call a protected /api/* endpoint.
 * <p>
 * Run: compile with the RestAssured deps above, then run main().
 * Precondition: the account EMAIL/PASSWORD must already exist in
 * data/mock-google.json (sign in through the UI once, or POST the signup form).
 */
public class MockGoogleOAuthFlow {

    static final String BASE = "http://localhost:5000";
    static final String APP = "food";                       // "food" or "shop"
    static final String EMAIL = "harsh.vegada@gmail.com";
    static final String PASSWORD = "123456";

    public static void main(String[] args) {
        RestAssured.baseURI = BASE;

        CookieFilter session = new CookieFilter();

        Response start = RestAssured.given().filter(session).redirects().follow(false).queryParam("app", APP).when().get("/auth/google");
        require(start.statusCode() == 302, "step1 expected 302, got " + start.statusCode());

        Map<String, String> q = parseQuery(start.getHeader("Location"));
        String clientId = q.get("client_id");
        String redirectUri = q.get("redirect_uri");                 // decoded
        String scope = q.getOrDefault("scope", "openid email profile");
        String state = q.get("state");                        // <-- from session
        System.out.println("state        = " + state);
        System.out.println("redirect_uri = " + redirectUri);

        // --- Step 2 (optional): load the sign-in page (same session). --------
        RestAssured.given().filter(session).redirects().follow(false).queryParam("client_id", clientId).queryParam("redirect_uri", redirectUri).queryParam("state", state).queryParam("scope", scope).when().get("/auth/mock-google/authorize").then().statusCode(200);

        // --- Step 3: submit the mock Google form. FORM BODY, not query! ------
        Response authorize = RestAssured.given().filter(session).redirects().follow(false).contentType("application/x-www-form-urlencoded").formParam("client_id", clientId).formParam("redirect_uri", redirectUri).formParam("state", state).formParam("scope", scope).formParam("email", EMAIL).formParam("password", PASSWORD).when().post("/auth/mock-google/authorize");
        require(authorize.statusCode() == 302, "step3 expected 302; 400=params-in-query, 401=bad creds/no account. got " + authorize.statusCode() + " body=" + authorize.asString());

        String callbackLocation = authorize.getHeader("Location"); // /auth/google/callback?code=..&state=..
        System.out.println("callback     = " + callbackLocation);

        // --- Step 4: hit the callback with the SAME session -> 302 /auth/bridge
        Response callback = RestAssured.given().filter(session).redirects().follow(false).when().get(pathAndQuery(callbackLocation));
        require(callback.statusCode() == 302, "step4 expected 302, got " + callback.statusCode() + " next=" + callback.getHeader("Location"));
        System.out.println("bridge       = " + callback.getHeader("Location"));

        // --- Step 5: exchange the OAuth session for the app JWT + creds. -----
        Response tok = RestAssured.given().filter(session).queryParam("app", APP).when().get("/auth/session-token");
        require(tok.statusCode() == 200, "step5 expected 200, got " + tok.statusCode() + " body=" + tok.asString());

        String token = tok.jsonPath().getString("token");
        String studentId = tok.jsonPath().getString("student.studentId");
        String accessCode = tok.jsonPath().getString("accessCode");
        System.out.println("token        = " + token);
        System.out.println("studentId    = " + studentId);
        System.out.println("accessCode   = " + accessCode);

        // --- Step 6: call a protected application API. ----------------------
        // /api/* requires BOTH the Bearer JWT and the student-gate headers.
        Response me = RestAssured.given().header("Authorization", "Bearer " + token).header("X-Student-Id", studentId).header("X-Access-Code", accessCode).when().get("/api/auth/me");
        System.out.println("API status   = " + me.statusCode());
        System.out.println("API body     = " + me.asString());

        // Another example: list restaurants (same three auth pieces).
        Response restaurants = RestAssured.given().header("Authorization", "Bearer " + token).header("X-Student-Id", studentId).header("X-Access-Code", accessCode).when().get("/api/restaurants");
        System.out.println("restaurants  = " + restaurants.statusCode() + " (" + restaurants.asString().length() + " bytes)");
    }

    // -------------------------------- helpers --------------------------------

    static void require(boolean cond, String msg) {
        if (!cond) throw new IllegalStateException(msg);
    }

    /**
     * Location may be absolute or relative; RestAssured.get() wants path+query.
     */
    static String pathAndQuery(String url) {
        try {
            URI u = URI.create(url);
            String pq = u.getRawPath();
            if (u.getRawQuery() != null) pq += "?" + u.getRawQuery();
            return pq;
        } catch (Exception e) {
            return url;
        }
    }

    static Map<String, String> parseQuery(String url) {
        Map<String, String> map = new HashMap<>();
        int i = url.indexOf('?');
        if (i < 0) return map;
        for (String pair : url.substring(i + 1).split("&")) {
            int eq = pair.indexOf('=');
            if (eq < 0) continue;
            String k = URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8);
            String v = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
            map.put(k, v);
        }
        return map;
    }
}