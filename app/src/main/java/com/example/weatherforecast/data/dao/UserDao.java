package com.example.weatherforecast.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.weatherforecast.data.model.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(User user);
    @Delete
    int deleteUser(User user);
    @Update
    int updateUser(User user);
    @Query("SELECT * FROM users WHERE id=:userId")
    User getUserById(int userId);
    @Query("SELECT * FROM users WHERE (username=:username AND password=:password)")
    User checkAccount(String username,String password);
    @Query("SELECT * FROM users WHERE wbId=:wbId")
    User checkAccountByWb(String wbId);
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)")
    boolean isUsernameExists(String username);
    @Query("SELECT * FROM users")
    List<User> getAllUsers();
    @Query("DELETE FROM users")
    int deleteAllUsers();
}
