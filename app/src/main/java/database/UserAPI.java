package database;

import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import model.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UserAPI {
    private volatile static UserAPI instance = null;

    private OkHttpClient client;

    private String default_link;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public UserAPI() {
        this.client = new OkHttpClient();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        default_link = "https://socialcompass.goto.ucsd.edu/location/";
        // In case of sabotage
//        default_link = "https://vs2961.pythonanywhere.com/";

    }

    public UserAPI(String new_link) {
        this.client = new OkHttpClient();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        default_link = new_link;
    }


    public static UserAPI provide() {

        if (instance == null) {
            instance = new UserAPI();
        }
        return instance;
    }

    /**
     * Sends a GET request to the server.
     * @param public_code - Public UID of the user.
     * @return a User object, if the public code is found.
     */
    public User get(String public_code) {
        // URLs cannot contain spaces, so we replace them with %20.
        public_code = public_code.replace(" ", "%20");

        Request request = new Request.Builder()
                .url(default_link + public_code)
                .method("GET", null)
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            assert response.body() != null;

            String body = response.body().string();
            if (body.equals("{\"detail\":\"Location not found.\"}")) {
                return null;
            }
            return User.fromJSON(body);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Sends a PUT request to the server.
     * @param private_code - Private password of the user.
     * @param user - The user to be updated
     */
    public void put(String private_code, User user) {
        // URLs cannot contain spaces, so we replace them with %20.
        String public_code = user.getUid();
        public_code = public_code.replace(" ", "%20");
        RequestBody requestBody = RequestBody.create(user.toPutJSON(private_code), JSON);
        Request request = new Request.Builder()
                .url(default_link + public_code)
                .method("PUT", requestBody)
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String body = response.body().string();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a PATCH request to the server.
     * @param private_code - Private password of the user.
     * @param user - The user to be updated
     */
    public void patch(String private_code, User user) {
        // URLs cannot contain spaces, so we replace them with %20.
        String public_code = user.getUid();
        public_code = public_code.replace(" ", "%20");
        RequestBody requestBody = RequestBody.create(user.toPatchJSON(private_code), JSON);
        Request request = new Request.Builder()
                .url(default_link + public_code)
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
