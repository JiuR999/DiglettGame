package cn.swust.firstcold.roomdatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface UserDao {
    @Query("select * from User")
    List<User> selectAll();
    //根据账号查找
    @Query("select * from User where account = (:account)")
    User selectByAccount(String account);
    //修改密码
    @Update
    void updatePassword(User user);
    //增加用户
    @Insert
    void addUser(User nuser);
    //删除用户
    @Query("delete from User where account = (:account)")
    void deleteUser(String account);
}
