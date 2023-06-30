package cn.swust.firstcold.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.swust.firstcold.diglettgame.R;
import cn.swust.firstcold.source.Level;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder>{
    private List<Level> levels;
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public LevelAdapter(List<Level> levels){
        this.levels = levels;
    }
    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建关卡视图，返回到LevelViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.level_item, parent, false);
        return new LevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {
        //绑定数据到关卡视图
        Level level = levels.get(position);
        if (level.isLocked()){
            holder.lockIon.setClickable(false);
        }
        holder.levelName.setText(level.getName());
        //根据关卡的解锁状态设置锁图标的可见性
        if(!level.isLocked()){
            //未锁，替换为黄方块
            holder.lockIon.setImageResource(R.drawable.fk);
//            holder.lockIon.setVisibility(View.INVISIBLE);
        }

        // 设置关卡项之间的间距
        int margin = 40; // 设置关卡项之间的间距，单位为dp
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        layoutParams.setMargins(margin, margin*2, margin, margin);
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    public class LevelViewHolder extends RecyclerView.ViewHolder {
        TextView levelName;
        ImageView lockIon;

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);

            levelName = itemView.findViewById(R.id.level_name);
            lockIon = itemView.findViewById(R.id.lock_icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (mListener!=null){
                        mListener.onItemClick(position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
