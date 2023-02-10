package utilities;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LocationDao {
    @Insert
    public void insertLocation(Location location);

    @Query("SELECT * from location")
    public List<Location> getAll();
}