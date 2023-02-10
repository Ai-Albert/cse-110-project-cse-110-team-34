package utilities;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {Coordinate.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public abstract CoordinateDao getCoordinateDao();
}
