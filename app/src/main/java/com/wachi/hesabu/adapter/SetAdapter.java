package com.wachi.hesabu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.wachi.hesabu.R;
import com.wachi.hesabu.model.SetModel;
import com.wachi.hesabu.utils.Constant;

import java.util.List;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.ViewHolder> {


    private Activity context;
    private ItemClick itemClick;
    private List<SetModel> setModels;
    public int themePosition;
    private String tableName;

    private int practice_set;

    public SetAdapter(Activity context, List<SetModel> setModels, String tableName) {
        this.context = context;
        this.setModels = setModels;
        this.tableName = tableName;
    }


    public void setClickListener(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public SetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_set, parent, false);
        return new SetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SetAdapter.ViewHolder holder, final int position) {

        SetModel setModel = setModels.get(position);
        Constant.setDefaultLanguage(context);
        holder.tv_title.setText(Constant.getTitleList(tableName, context).get((setModel.id - 1)).title);
        holder.tv_practice.setText(String.valueOf(setModel.practice_set));
        holder.tv_example.setText(setModel.example);


        practice_set = practice_set + setModel.practice_set;


        themePosition = getThemePosition(position);

        int[] colors = {ContextCompat.getColor(context, Constant.getDrawbles().get(themePosition).color), ContextCompat.getColor(context, Constant.getDrawbles().get(themePosition).DarkColor)};

        holder.btn_next.setBackground(Constant.customViewOval(colors));
        holder.cell.setBackgroundResource(Constant.getDrawbles().get(themePosition).cell);


    }

    private int getThemePosition(int position) {

        if (position % 4 == 0) {
            themePosition = 0;
        } else if (position % 4 == 1) {
            themePosition = 1;
        } else if (position % 4 == 2) {
            themePosition = 2;
        } else {
            themePosition = 3;
        }

        return themePosition;
    }

    public interface ItemClick {
        void itemClick(int position, int themePosition, String title);
    }

    @Override
    public int getItemCount() {
        return setModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_example, tv_practice;
        ImageView btn_next;
        LinearLayout cell;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_example = itemView.findViewById(R.id.tv_example);
            btn_next = itemView.findViewById(R.id.btn_next);
            tv_practice = itemView.findViewById(R.id.tv_practice);
            cell = itemView.findViewById(R.id.cell);


            itemView.setOnClickListener(v -> {
                if (itemClick != null) {
                    itemClick.itemClick(getAdapterPosition(), getThemePosition(getAdapterPosition()), tv_title.getText().toString());
                }
            });

        }
    }
}
