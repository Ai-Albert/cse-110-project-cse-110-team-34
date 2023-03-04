package model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;

@Dao
public interface UserDao {
    @Upsert
    public abstract long upsert(User user);

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE public_code = :public_code)")
    public abstract boolean exists(String public_code);

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE is_main = 1)")
    public abstract boolean existsMain();

    @Query("SELECT * FROM User WHERE public_code = :public_code")
    public abstract LiveData<User> get(String public_code);

    @Query("SELECT * FROM User WHERE is_main = 1 LIMIT 1")
    public abstract User getMain();

    @Query("SELECT * FROM User ORDER BY name")
    public abstract LiveData<List<User>> getAll();

    @Delete
    public abstract int delete(User user);
}