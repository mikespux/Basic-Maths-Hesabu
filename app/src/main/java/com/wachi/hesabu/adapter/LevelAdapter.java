package com.wachi.hesabu.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wachi.hesabu.R;
import com.wachi.hesabu.model.ProgressModel;
import com.wachi.hesabu.utils.Constant;

import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.ViewHolder> {


    private Context context;
    private ItemClick itemClick;
    private int practice_set;
    private List<ProgressModel> progressModels;
    private int themePosition;


    public LevelAdapter(Context context, int practice_set, List<ProgressModel> progressModels, int themePosition) {
        this.context = context;
        this.practice_set = practice_set;
        this.progressModels = progressModels;
        this.themePosition = themePosition;
    }


    public void setClickListener(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public LevelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_level, parent, false);
        return new LevelAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelAdapter.ViewHolder holder, final int position) {


        holder.tv_title.setText(String.valueOf((position + 1)));
        holder.tv_score.setText(String.valueOf(progressModels.get(position).score));


        if (progressModels.get(position).score <= 0) {
            holder.tv_score.setText(null);
        }


        holder.cell.setBackgroundResource(Constant.getDrawbles().get(themePosition).drawable);
        holder.progressView.removeAllViews();
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(layoutParams);


            if (progressModels.get(position).progress == 0) {
                imageView.setImageResource(R.drawable.ic_star_border_black_24dp);
            } else {
                if (Constant.getStarCount(progressModels.get(position).progress) >= (i + 1)) {
                    imageView.setImageResource(R.drawable.ic_star_black_24dp);
                } else {
                    imageView.setImageResource(R.drawable.ic_star_border_black_24dp);
                }

            }
            holder.progressView.addView(imageView);

        }

    }

    public interface ItemClick {
        void itemClick(int position);
    }

    @Override
    public int getItemCount() {
        return practice_set;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_score;
        LinearLayout progressView, cell;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_score = itemView.findViewById(R.id.tv_score);
            progressView = itemView.findViewById(R.id.progressView);
            cell = itemView.findViewById(R.id.cell);


            itemView.setOnClickListener(v -> {
                if (itemClick != null) {
                    itemClick.itemClick(getAdapterPosition());
                }
            });

        }
    }
}
