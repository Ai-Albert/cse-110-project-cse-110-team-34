package model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.util.UUID;

@Entity
public class User {

    @NonNull
    @PrimaryKey
    @SerializedName("private_code")
    public String private_uid;

    @SerializedName("public_code")
    public String uid;

    @SerializedName("label")
    public String name;

    @SerializedName("longitude")
    public double longitude;

    @SerializedName("latitude")
    public double latitude;

    @JsonAdapter(TimestampAdapter.class)
    @SerializedName("updated_at")
    public long version = 0;

    @ColumnInfo(name = "is_main")
    public boolean is_main;

    public User(String name, double longitude, double latitude, boolean is_main) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.uid = UUID.randomUUID().toString();
        this.is_main = is_main;
        this.private_uid = UUID.randomUUID().toString();
    }

    public boolean equals(User user) {
        return this.uid == user.uid;
    }

    public String getName() {
        return name;
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

    public static User fromJSON(String json) {
        return new Gson().fromJson(json, User.class);
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }
}


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

