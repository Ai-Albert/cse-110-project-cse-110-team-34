package utilities;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;


@androidx.room.Database(entities = {User.class}, version = 1)

public abstract class Database extends RoomDatabase {

    private static Database instance = null;

    public abstract UserDao getUserDao();

    public synchronized static Database getInstance(Context context) {
        if (instance == null) {
            instance = Database.makeDatabase(context);
        }
        return instance;
    }

    private static Database makeDatabase(Context context) {
        return Room.databaseBuilder(context, Database.class, "app.db").allowMainThreadQueries().fallbackToDestructiveMigration().build();
    }
}
