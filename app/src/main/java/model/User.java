package model;

import android.os.health.SystemHealthManager;

import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.Instant;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;


@Entity(indices = {@Index(value = {"public_code"}, unique = true)})

public class User {

    /** The UID shared by the user to their friends. Used as the primary key everywhere. **/
    @NonNull
    @PrimaryKey
    @SerializedName("public_code")
    public String public_code;

    /** The name of the user. **/
    @SerializedName("label")
    public String name;

    /** Represents the location of the user. **/
    @SerializedName("longitude")
    public double longitude;

    @SerializedName("latitude")
    public double latitude;

    /** The last time the user's location was updated. **/
    @JsonAdapter(TimestampAdapter.class)
    @SerializedName("updated_at")
    public long version = 0;

    /** Constructor of a user. **/
    public User(String name, String public_code, double longitude, double latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.public_code = public_code;
        this.version = Instant.now().getEpochSecond();
    }

    public boolean equals(User user) {
        return this.public_code.equals(user.public_code);
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return public_code;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getLastUpdated() {
        return version;
    }

    public static User fromJSON(String json) {
        return new Gson().fromJson(json, User.class);
    }

    /** Creates the JSON body and removes fields to be compliant with the API for put. **/
    public String toPutJSON(String private_code) {
        JsonObject json = (JsonObject) new Gson().toJsonTree(this);
        json.remove("public_code");
        json.remove("updated_at");
        json.addProperty("private_code", private_code);
        return json.toString();
    }

    /** Creates the JSON body and removes fields to be compliant with the API for patch. **/
    public String toPatchJSON(String private_code) {
        JsonObject json = (JsonObject) new Gson().toJsonTree(this);
        json.remove("public_code");
        json.remove("updated_at");
        json.remove("label");
        json.addProperty("private_code", private_code);
        return json.toString();
    }
}


/** Adapter for converting between dates on the API into longs. **/
class TimestampAdapter extends TypeAdapter<Long> {
    @Override
    public void write(JsonWriter out, Long value) throws java.io.IOException {
        Instant instant = Instant.ofEpochSecond(value);
        out.value(instant.toString());
    }

    @Override
    public Long read(JsonReader in) throws java.io.IOException {
        Instant instant = Instant.parse(in.nextString());
        return instant.getEpochSecond();
    }
}

