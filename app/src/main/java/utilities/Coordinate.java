package utilities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Coordinate {
    public Coordinate(String label, double longitude, double latitude) {
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

    @PrimaryKey
    public long id;

    @ColumnInfo(name = "label")
    public String label;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "latitude")
    public double latitude;
}
