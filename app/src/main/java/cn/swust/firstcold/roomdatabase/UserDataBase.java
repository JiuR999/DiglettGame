package cn.swust.firstcold.roomdatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {User.class},version = 1,exportSchema = true)
public abstract class UserDataBase extends RoomDatabase {
    public  abstract UserDao userDao();
    private static UserDataBase userDataBase;
    public static UserDataBase getInstance(Context context){
        if (userDataBase == null){
            synchronized (UserDataBase.class){
                if (userDataBase == null){
                    userDataBase = Room.databaseBuilder(context.getApplicationContext(),UserDataBase.class,"user.db").build();
                }
            }
        }
        return userDataBase;
    }
}
