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

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE name = :name)")
    public abstract boolean exists(String name);

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE is_main = 1)")
    public abstract boolean existsMain();

    @Query("SELECT * FROM User WHERE name = :name")
    public abstract LiveData<User> get(String name);

    @Query("SELECT 1 FROM User WHERE is_main = 1")
    public abstract LiveData<User> getMain();

    @Query("SELECT * FROM User ORDER BY name")
    public abstract LiveData<List<User>> getAll();

    @Delete
    public abstract int delete(User user);
}