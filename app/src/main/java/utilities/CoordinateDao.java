package utilities;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CoordinateDao {
    @Insert
    void insert(Coordinate coordinate);

    @Query("SELECT * FROM Coordinate WHERE label = :label")
    Coordinate getByLabel(String label);

    @Query("SELECT * from Coordinate")
    List<Coordinate> getAll();
}