package utilities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Coordinate {

    private static int nextId = 0;

    @PrimaryKey
    public long id;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "latitude")
    public double latitude;

    public Coordinate(double latitude, double longitude) {
        this.id = nextId;
        this.longitude = longitude;
        this.latitude = latitude;
        nextId++;
    }

    public boolean equals(Coordinate c) {
        return this.longitude == c.longitude &&
                this.latitude == c.latitude;
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
