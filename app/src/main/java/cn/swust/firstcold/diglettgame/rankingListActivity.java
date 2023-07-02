package cn.swust.firstcold.diglettgame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class rankingListActivity extends AppCompatActivity {

    public class ListItem { //定义ListView中每个Item的类，包含一个图片，两个文本
        public ImageView imageView;
        public String title;
        public String subTitle;
        public ListItem(ImageView imageView, String title, String subTitle) {
            this.imageView = imageView;
            this.title = title;
            this.subTitle = subTitle;
        }
    }



    private ListView listView_HitMouse;
    private ImageView imageView;


    List<ListItem> list;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ranking_list);

        listView_HitMouse = findViewById(R.id.listView_HitMouse);//绑定排行榜的ListView
        imageView = findViewById(R.id.item_view);
        SharedPreferences sp = getSharedPreferences(MouseActivity.CONFIG_NAME,MouseActivity.CONFIG_MODE);

        list = new ArrayList<ListItem>();//将每个Item加入到list列表中
        list.add(new ListItem(imageView,"第一关最高分：", "1"));
        list.add(new ListItem(imageView,"第二关最高分：", "2"));
        list.add(new ListItem(imageView,"第三关最高分：", "3"));
        list.add(new ListItem(imageView,"第四关最高分：", "4"));
        list.add(new ListItem(imageView,"第五关最高分：", "5"));
        list.add(new ListItem(imageView,"第六关最高分：", "6"));
        list.add(new ListItem(imageView,"第七关最高分：", "7"));
        list.add(new ListItem(imageView,"第八关最高分：", "8"));
        list.add(new ListItem(imageView,"第九关最高分：", "9"));
        list.add(new ListItem(imageView,"第十关最高分：", "10"));
        ListItemAdapter adapter = new ListItemAdapter(
                this, R.layout.activity_ranking_list_item,list);
        listView_HitMouse.setAdapter(adapter);


        AdapterView.OnItemClickListener listviewListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            }
        };
        listView_HitMouse.setOnItemClickListener(listviewListener);
    }

    public class ListItemAdapter extends ArrayAdapter<ListItem>{
        private int[] images = {R.drawable.chuizi,R.drawable.chuizi,R.drawable.chuizi,R.drawable.chuizi,
                R.drawable.chuizi,R.drawable.chuizi,R.drawable.chuizi,R.drawable.chuizi
                ,R.drawable.chuizi,R.drawable.chuizi};
        // 将排行榜里每个Item要插入的图片加入一个数组
        //用于存储每个ListView的图片，并方便使用
        public ListItemAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects); // 构造函数
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view;
            if (convertView == null) {
                view = inflater.inflate(R.layout.activity_ranking_list_item,parent,false);
            } else {
                view = convertView;
            }
            final ImageView imageView = view.findViewById(R.id.item_view); // 排行榜每个List的图片
            final TextView tvTitle = view.findViewById(R.id.tvTitle);  // 排行榜每个List的主标题
            final TextView tvSubTitle = view.findViewById(R.id.tvSubTitle);// 排行榜每个List的副标题

            if (tvTitle == null || tvSubTitle == null) return null;
            String account = getIntent().getStringExtra(MainActivity.ACCOUNT);
            SharedPreferences sp = getSharedPreferences(account,MouseActivity.CONFIG_MODE);
            if(position<list.size()){
                final ListItem item = getItem(position); //得到每个位置的Item
                imageView.setImageResource(images[position]); //为每个Item设置对应的图片
                tvTitle.setText(item.title); // 为每个Item设置主标题
                //根据位置为每个Item设置副标题
                if (Objects.equals(item.subTitle, "1")) {
                    tvSubTitle.setText(sp.getString(MouseActivity.SCORE1,"").equals("")?
                            "您暂时还未解锁此关卡哦！":sp.getString(MouseActivity.SCORE1,"")+ "分");
                }
                if(Objects.equals(item.subTitle, "2")) {
                    tvSubTitle.setText(sp.getString(MouseActivity.SCORE2,"").equals("")?
                            "您暂时还未解锁此关卡哦！":sp.getString(MouseActivity.SCORE2,"")+ "分");
                }
                if(Objects.equals(item.subTitle, "3")) {
                    tvSubTitle.setText(sp.getString(MouseActivity.SCORE3,"").equals("")?
                            "您暂时还未解锁此关卡哦！":sp.getString(MouseActivity.SCORE3,"")+ "分");
                }
                if(Objects.equals(item.subTitle, "4")) {
                    tvSubTitle.setText(sp.getString(MouseActivity.SCORE4,"").equals("")?
                            "您暂时还未解锁此关卡哦！":sp.getString(MouseActivity.SCORE4,"")+ "分");
                }
                if(Objects.equals(item.subTitle, "5")) {
                    tvSubTitle.setText(sp.getString(MouseActivity.SCORE5,"").equals("")?
                            "您暂时还未解锁此关卡哦！":sp.getString(MouseActivity.SCORE5,"")+ "分");
                }
                if(Objects.equals(item.subTitle, "6")) {
                    tvSubTitle.setText(sp.getString(MouseActivity.SCORE6,"").equals("")?
                            "您暂时还未解锁此关卡哦！":sp.getString(MouseActivity.SCORE6,"")+ "分");
                }
                if(Objects.equals(item.subTitle, "7")) {
                    tvSubTitle.setText(sp.getString(MouseActivity.SCORE7,"").equals("")?
                            "您暂时还未解锁此关卡哦！":sp.getString(MouseActivity.SCORE7,"")+ "分");
                }
                if(Objects.equals(item.subTitle, "8")) {
                    tvSubTitle.setText(sp.getString(MouseActivity.SCORE8,"").equals("")?
                            "您暂时还未解锁此关卡哦！":sp.getString(MouseActivity.SCORE8,"")+ "分");
                }
                if(Objects.equals(item.subTitle, "9")) {
                    tvSubTitle.setText(sp.getString(MouseActivity.SCORE9,"").equals("")?
                            "您暂时还未解锁此关卡哦！":sp.getString(MouseActivity.SCORE9,"")+ "分");
                }
                if(Objects.equals(item.subTitle, "10")) {
                    tvSubTitle.setText(sp.getString(MouseActivity.SCORE10,"").equals("")?
                            "您暂时还未解锁此关卡哦！":sp.getString(MouseActivity.SCORE10,"")+ "分");
                }
            }

            return view;
        }
    }



}