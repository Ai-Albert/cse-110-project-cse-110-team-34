package utilities;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM User WHERE name = :name")
    User getByName(String name);

    @Query("SELECT * from User")
    List<User> getAll();
}