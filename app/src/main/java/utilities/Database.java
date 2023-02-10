package utilities;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {Location.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public abstract LocationDao locationDao();
}
