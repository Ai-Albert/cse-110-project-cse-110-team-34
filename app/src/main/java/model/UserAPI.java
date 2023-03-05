package model;

import android.os.StrictMode;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UserAPI {
    private volatile static UserAPI instance = null;

    private OkHttpClient client;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public UserAPI() {
        this.client = new OkHttpClient();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static UserAPI provide() {

        if (instance == null) {
            instance = new UserAPI();
        }
        return instance;
    }


    public User get(String public_code) {
        // URLs cannot contain spaces, so we replace them with %20.
        public_code = public_code.replace(" ", "%20");

        Request request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + public_code)
                .method("GET", null)
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            assert response.body() != null;

            String body = response.body().string();
            if (body.equals("{\"detail\":\"User not found.\"}")) {
                return null;
            }
            return User.fromJSON(body);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void put(String public_code, User user) {
        // URLs cannot contain spaces, so we replace them with %20.
        public_code = public_code.replace(" ", "%20");
        RequestBody requestBody = RequestBody.create(user.toJSON(), JSON);
        Request request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + public_code)
                .method("PUT", requestBody)
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String body = response.body().string();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
