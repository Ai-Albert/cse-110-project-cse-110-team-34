package utilities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Location {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "label")
    public String label;

    @ColumnInfo(name = "longitude")
    public float longitude;

    @ColumnInfo(name = "latitude")
    public float latitude;
}
