package database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;

import model.User;

@Dao
public interface UserDao {
    @Upsert
    public abstract long upsert(User user);

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE public_code = :public_code)")
    public abstract boolean exists(String public_code);

    @Query("SELECT * FROM User WHERE public_code = :public_code")
    public abstract LiveData<User> get(String public_code);

    @Query("SELECT * FROM User ORDER BY name")
    public abstract LiveData<List<User>> getAll();

    @Delete
    public abstract int delete(User user);
}