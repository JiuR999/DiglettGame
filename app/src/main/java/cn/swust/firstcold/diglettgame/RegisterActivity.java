package cn.swust.firstcold.diglettgame;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.swust.firstcold.roomdatabase.User;
import cn.swust.firstcold.roomdatabase.UserDao;
import cn.swust.firstcold.roomdatabase.UserDataBase;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextName,editTextAccount,editTextPassword,editTextRePassword;
    private CheckBox checkBoxAgree;
    private Button btnReg;
    private UserDataBase db;
    private UserDao userDao;
    private User one;
    private boolean isExist = false;
    public static final int REGISTER_FAILED = 2;
    public static final int REGISTER_SUCCEED = 3;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                editTextName.setText((String)msg.obj);
            }else if(msg.what==REGISTER_FAILED){
                Toast.makeText(RegisterActivity.this, "账号已经注册!", Toast.LENGTH_SHORT).show();
            }else if(msg.what==REGISTER_SUCCEED){
                Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();

    }

    /**
     *向数据库插入新用户
     * @param user 要加入数据库的用户
     */
    private void insertUser(User user) {
        new Thread(new Runnable() {
            @Override

            public void run() {
                db = UserDataBase.getInstance(RegisterActivity.this);
                userDao = db.userDao();
                if(userDao.selectByAccount(user.getAccount())!=null){
                    Message msg = new Message();
                    msg.what = REGISTER_FAILED;
                    handler.sendMessage(msg);
                }else{
                    userDao.addUser(user);
                    Message msg = new Message();
                    msg.what = REGISTER_SUCCEED;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
    /**
     * 字符串是否包含中文
     * @param str 待校验字符串
     * @return true 包含中文字符  false 不包含中文字符
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    private void initView() {
        editTextName = findViewById(R.id.ed_reg_name);
        editTextAccount = findViewById(R.id.ed_reg_account);
        editTextPassword = findViewById(R.id.ed_reg_password);
        editTextRePassword = findViewById(R.id.ed_reg_repassword);
        btnReg = findViewById(R.id.btn_reg);
        checkBoxAgree = findViewById(R.id.cb_agree);
        SpannableString spannableString = new SpannableString("已阅读并且同意《相关协议》");
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE),7,13, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        checkBoxAgree.setText(spannableString);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String account = editTextAccount.getText().toString();
                String password = editTextPassword.getText().toString();
                String repassword = editTextRePassword.getText().toString();
                Register(name,account,password,repassword);
            }
        });
    }

    private void Register(String name,String account, String password, String repassword) {

           if(password.length()<6||password.length()>=10){
               Toast.makeText(this, "密码长度过长或过短!", Toast.LENGTH_SHORT).show();
           } else if(password.equals("")||name.equals("")){
               Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
           }else if (isContainChinese(password)||isContainChinese(account)) {
               Toast.makeText(this, "密码或密码不能包含中文!", Toast.LENGTH_SHORT).show();
           }else if(!password.equals(repassword)){
               Toast.makeText(this, "两次密码输入不一致，请检查后重试！", Toast.LENGTH_SHORT).show();
           }
           else{
               if (!checkBoxAgree.isChecked()){
//                AlertDialog builder =  new AlertDialog.Builder(this).setTitle("账号注册")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        }).setView(View.inflate(this,R.layout.dialog,null))
//                        .show();
//                TextView tv = builder.findViewById(R.id.tv_dialog);
//                tv.setText("请先同意相关协议");
                   Toast.makeText(this, "请先同意相关协议!", Toast.LENGTH_SHORT).show();
               }else{
                   User nuser = new User(name,account,password);
                   insertUser(nuser);
               }
           }
       }
}