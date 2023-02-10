package utilities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Coordinate {
    @PrimaryKey
    public long id;

    @ColumnInfo(name = "label")
    public String label;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "latitude")
    public double latitude;

    public Coordinate(String label, double latitude, double longitude) {
        this.id = System.currentTimeMillis();
        this.label = label;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public boolean equals(Coordinate c) {
        return this.id == c.id &&
                this.label.equals(c.label) &&
                this.longitude == c.longitude &&
                this.latitude == c.latitude;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
