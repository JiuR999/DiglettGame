package cn.swust.firstcold.diglettgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnGame;
    private TextView tvRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitView();
    }

    private void InitView() {
        btnGame = findViewById(R.id.btn_game);
        btnGame.setOnClickListener(this);
        tvRegister = findViewById(R.id.tv_register);
        tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
        tvRegister.setText(getClickableSpan());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_game:
                startActivity(new Intent(MainActivity.this,MouseActivity.class));
                break;
        }
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
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 6, 11, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

}