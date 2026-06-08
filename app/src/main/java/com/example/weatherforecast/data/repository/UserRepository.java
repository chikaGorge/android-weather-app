package com.example.weatherforecast.data.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.weatherforecast.common.db.AppDatabase;
import com.example.weatherforecast.data.dao.UserDao;
import com.example.weatherforecast.data.model.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private UserDao userDao;
    private ExecutorService executorService;

    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        userDao = db.userDao();
        executorService = Executors.newSingleThreadExecutor();  // 使用单线程池来执行数据库操作
    }

    // 插入用户（异步）
    public void insertUser(User user, Callback<Long> callback) {
        if (user == null) {
            Log.e("UserRepository", "insertUser: User is null");
            callback.onResult(-1L);  // 返回错误值
            return;
        }
        Log.d("UserRepository", "insertUser: Inserting user with username = " + user.getUsername());
        executorService.execute(() -> {
            long result = userDao.insertUser(user);
            if (result == -1) {
                Log.e("UserRepository", "insertUser: Failed to insert user");
            } else {
                Log.d("UserRepository", "insertUser: Successfully inserted user");
            }
            callback.onResult(result);  // 回调返回结果
        });
    }

    // 删除用户（异步）
    public void deleteUser(User user, Callback<Integer> callback) {
        if (user == null) {
            Log.e("UserRepository", "deleteUser: User is null");
            callback.onResult(0);  // 返回失败值
            return;
        }
        Log.d("UserRepository", "deleteUser: Deleting user with username = " + user.getUsername());
        executorService.execute(() -> {
            int result = userDao.deleteUser(user);
            if (result == 0) {
                Log.e("UserRepository", "deleteUser: Failed to delete user");
            } else {
                Log.d("UserRepository", "deleteUser: Successfully deleted user");
            }
            callback.onResult(result);  // 回调返回结果
        });
    }

    // 更新用户（异步）
    public void updateUser(User user, Callback<Integer> callback) {
        if (user == null) {
            Log.e("UserRepository", "updateUser: User is null");
            callback.onResult(0);  // 返回失败值
            return;
        }
        Log.d("UserRepository", "updateUser: Updating user with username = " + user.getUsername());
        executorService.execute(() -> {
            int result = userDao.updateUser(user);
            if (result == 0) {
                Log.e("UserRepository", "updateUser: Failed to update user");
            } else {
                Log.d("UserRepository", "updateUser: Successfully updated user");
            }
            callback.onResult(result);  // 回调返回结果
        });
    }

    // 根据用户ID获取用户（异步）
    public void getUserById(int userId, Callback<User> callback) {
        if (userId <= 0) {
            Log.e("UserRepository", "getUserById: Invalid userId = " + userId);
            callback.onResult(null);
            return;
        }
        Log.d("UserRepository", "getUserById: Fetching user with userId = " + userId);
        executorService.execute(() -> {
            User user = userDao.getUserById(userId);
            if (user == null) {
                Log.e("UserRepository", "getUserById: User not found with userId = " + userId);
            } else {
                Log.d("UserRepository", "getUserById: Found user with username = " + user.getUsername());
            }
            callback.onResult(user);  // 回调返回结果
        });
    }

    // 根据用户名和密码检查账号（异步）
    public void checkAccount(String username, String password, UserRepository.Callback<User> callback) {
        if (username == null || password == null) {
            Log.e("UserRepository", "checkAccount: Username or password is null");
            callback.onResult(null);  // 返回 null，表示错误
            return;
        }

        // 使用 Executor 将查询操作放入后台线程
        executorService.execute(() -> {
            User user = userDao.checkAccount(username, password);
            if (user == null) {
                Log.e("UserRepository", "checkAccount: Account not found for username = " + username);
            } else {
                Log.d("UserRepository", "checkAccount: Found account for username = " + username);
            }
            callback.onResult(user);  // 回调返回结果
        });
    }

    // 根据微博ID检查账号
    public void checkAccountByWb(String wbId,Callback<User> callback) {
        if (wbId == null) {
            Log.e("UserRepository", "checkAccountByWb: wbId is null");
            callback.onResult(null);
        }
        Log.d("UserRepository", "checkAccountByWb: Checking account for wbId = " + wbId);
        executorService.execute(() -> {
            User user = userDao.checkAccountByWb(wbId);
            if (user == null) {
                Log.e("UserRepository", "checkAccountByWb: Account not found for wbId = " + wbId);
            } else {
                Log.d("UserRepository", "checkAccountByWb: Found account for wbId = " + wbId);
            }
            callback.onResult(user);  // 回调返回结果

        });
    }
    // 检查用户名是否存在（异步）
    public void isUsernameExists(String username, UserRepository.Callback<Boolean> callback) {
        if (username == null || username.isEmpty()) {
            Log.e("UserRepository", "isUsernameExists: Username is null or empty");
            callback.onResult(false); // 如果用户名为空，返回 false
            return;
        }

        // 使用 Executor 将查询操作放入后台线程
        executorService.execute(() -> {
            // 查询数据库中是否有相同的用户名
            boolean exists = userDao.isUsernameExists(username);
            callback.onResult(exists); // 通过回调返回查询结果
        });
    }



    // 获取所有用户（异步）
    public void getAllUsers(Callback<List<User>> callback) {
        Log.d("UserRepository", "getAllUsers: Fetching all users");
        executorService.execute(() -> {
            List<User> users = userDao.getAllUsers();
            if (users == null || users.isEmpty()) {
                Log.e("UserRepository", "getAllUsers: No users found");
            } else {
                Log.d("UserRepository", "getAllUsers: Found " + users.size() + " users");
            }
            callback.onResult(users);  // 回调返回结果
        });
    }

    // 删除所有用户（异步）
    public void deleteAllUsers(Callback<Integer> callback) {
        Log.d("UserRepository", "deleteAllUsers: Deleting all users");
        executorService.execute(() -> {
            int result = userDao.deleteAllUsers();
            if (result == 0) {
                Log.e("UserRepository", "deleteAllUsers: Failed to delete all users");
            } else {
                Log.d("UserRepository", "deleteAllUsers: Successfully deleted all users");
            }
            callback.onResult(result);  // 回调返回结果
        });
    }

    // 通用回调接口
    public interface Callback<T> {
        void onResult(T result);
    }
}
