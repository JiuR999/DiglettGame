package cn.swust.firstcold.diglettgame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import cn.swust.firstcold.roomdatabase.User;
import cn.swust.firstcold.roomdatabase.UserDao;
import cn.swust.firstcold.roomdatabase.UserDataBase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String ACCOUNT = "account";
    private Button btnGame,btnLog;
    private TextView tvRegister;
    private UserDataBase db;
    private UserDao userDao;
    private EditText editTextUser,editTextPassword;
    private String account;
    //登录成功消息类型
    private final int LOGIN_SUCCEED = 1;
    //账号未注册消息类型
    private final int NO_ACCOUNT = 2;
    //账号密码错误消息类型
    private final int PASSWORD_ERROR = 3;
    private Handler logHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==LOGIN_SUCCEED){
                setDialog("欢迎进入疯狂斗地鼠!",1);
            }else if(msg.what == NO_ACCOUNT){
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("该账号暂时未完成注册！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }else if(msg.what == PASSWORD_ERROR){
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("账号或密码错误！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitView();
    }
    private void setDialog(String tip,int command) {
        Dialog dialog = new Dialog(MainActivity.this);
        View view = View.inflate(MainActivity.this,R.layout.dialog,null);
        TextView tv = view.findViewById(R.id.tv_dialog);
        tv.setText(tip);
        Button btn = view.findViewById(R.id.dig_btn);
        if(command == 0){
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }else{
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this,LevelSelectionActivity.class);
                    intent.putExtra(ACCOUNT,account);
                    startActivity(intent);
                }
            });
        }
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        Window window = dialog.getWindow();

        int width = getResources().getDisplayMetrics().widthPixels;

        int height = getResources().getDisplayMetrics().heightPixels;

        window.setLayout(width-200,height*1/4);
    }
    private void InitView() {
        btnLog = findViewById(R.id.btn_log);
        editTextUser = findViewById(R.id.edit_user);
        editTextPassword = findViewById(R.id.edit_password);
        btnGame.setOnClickListener(this);
        btnLog.setOnClickListener(this);
        tvRegister = findViewById(R.id.tv_register);
        tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
        tvRegister.setText(getClickableSpan());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_log:
                Login();
                break;
        }
    }

    /**
     * 登录
     */
    private void Login() {
           account = editTextUser.getText().toString();
            String password = editTextPassword.getText().toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    db = UserDataBase.getInstance(MainActivity.this);
                    userDao = db.userDao();
                    User user = userDao.selectByAccount(account);
                    if(user==null){
                        msg.what = NO_ACCOUNT;
                        logHandler.sendMessage(msg);
                    }else{
                        if(!password.equals(user.getPassword())){
                            msg.what = PASSWORD_ERROR;
                            logHandler.sendMessage(msg);
                        }else{
                            msg.what = LOGIN_SUCCEED;
                            logHandler.sendMessage(msg);
                        }
                    }
                }
            }).start();
    }

    //实现点击文字
    private SpannableString getClickableSpan() {
        SpannableString spannableString = new SpannableString(tvRegister.getText().toString());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                   startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
            //重写该方法去掉下划线
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(true);
            }
        };
        //设置文字的点击事件
        spannableString.setSpan(clickableSpan, 6, 11, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spannableString.setSpan(new ForegroundColorSpan(Color.GREEN), 6, 11, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

}