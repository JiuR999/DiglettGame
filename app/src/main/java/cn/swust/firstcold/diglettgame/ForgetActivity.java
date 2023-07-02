package cn.swust.firstcold.diglettgame;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import cn.swust.firstcold.roomdatabase.User;
import cn.swust.firstcold.roomdatabase.UserDao;
import cn.swust.firstcold.roomdatabase.UserDataBase;

public class ForgetActivity extends AppCompatActivity {
    private EditText editTextAccount,editTextPassword;
    private Button btn_enter;
    private UserDataBase db;
    private UserDao userDao;
    private User user;
    public static final int RE_FAILED = 2;
    public static final int RE_SUCCEED = 3;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==RE_FAILED){
                Toast.makeText(ForgetActivity.this, "账号验证信息不匹配!", Toast.LENGTH_SHORT).show();
            }else if(msg.what==RE_SUCCEED){
                user.setPassword(editTextPassword.getText().toString());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        userDao.updatePassword(user);
                    }
                }).start();
                Toast.makeText(ForgetActivity.this, "重置密码成功！", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        initView();

    }

    private void initView() {
        editTextAccount = findViewById(R.id.ed_fgt_account);
        editTextPassword = findViewById(R.id.ed_fgt_repassword);
        btn_enter = findViewById(R.id.btn_fgt);
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = getIntent().getStringExtra(MainActivity.ACCOUNT);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        db = UserDataBase.getInstance(ForgetActivity.this);
                        userDao = db.userDao();
                        user = userDao.selectByAccount(account);
                        if(editTextAccount.getText().toString().equals(user.getAccount())){
                             msg.what = RE_SUCCEED;
                             handler.sendMessage(msg);
                        }else{
                              msg.what = RE_FAILED;
                              handler.sendMessage(msg);
                        }
                    }
                }).start();
            }
        });
    }
}