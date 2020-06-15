package com.wachi.hesabu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wachi.hesabu.R;
import com.wachi.hesabu.model.ColorModel;
import com.wachi.hesabu.utils.Constant;

import java.util.List;

public class SubAdapter extends RecyclerView.Adapter<SubAdapter.ViewHolder> {


    private Context context;
    private SubItemClick subItemClick;
    private List<ColorModel> drawableList;
    private List<String> stringList;
    private int main_id;
    private int width;


    SubAdapter(Context context, List<String> strings, int main_id, int width) {
        this.context = context;
        this.stringList = strings;
        this.main_id = main_id;
        this.width = width - 50;
        drawableList = Constant.getDrawbles();
    }


    void setSubClickListener(SubItemClick subClickListener) {
        this.subItemClick = subClickListener;
    }

    @NonNull
    @Override
    public SubAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sub, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubAdapter.ViewHolder holder, int position) {


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width / 4, width / 4);
        layoutParams.gravity = Gravity.CENTER;
        holder.view.setLayoutParams(layoutParams);

        holder.cell.setBackgroundResource(drawableList.get(position + main_id).drawable);
        Drawable drawable;
        if (main_id == 4) {
            drawable = context.getDrawable(Constant.getMixedIcons().get(position));

        } else {

            drawable = context.getDrawable(Constant.getDefaultIcons1().get(position));
        }

        assert drawable != null;
        drawable.mutate();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        holder.imageView.setImageDrawable(drawable);

        holder.textView.setText(stringList.get(position));

    }


    public interface SubItemClick {
        void subItemClick(int position, String title, int themePosition);
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RelativeLayout cell;
        LinearLayout view;
        ImageView imageView;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            cell = itemView.findViewById(R.id.cell);
            imageView = itemView.findViewById(R.id.imageView);
            view = itemView.findViewById(R.id.view);

            itemView.setOnClickListener(v -> {
                if (subItemClick != null) {
                    subItemClick.subItemClick(getAdapterPosition(), stringList.get(getAdapterPosition()), (getAdapterPosition() + main_id));
                }
            });

        }
    }
}
