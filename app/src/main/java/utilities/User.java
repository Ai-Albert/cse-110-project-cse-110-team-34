package utilities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    private static int nextId = 0;

    @PrimaryKey
    public long id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "UID")
    public String uid;



    public User(String name, double longitude, double latitude) {
        this.id = nextId;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.uid = Calculation.getRandomUID(10);
        nextId++;
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

}
