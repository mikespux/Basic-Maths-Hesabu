package com.wachi.hesabu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wachi.hesabu.R;
import com.wachi.hesabu.ui.AllPdfActivity;

import java.io.File;
import java.util.List;

public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.ViewHolder> {


    private Context context;
    private ItemClick itemClick;
    private boolean isDelete;
    private List<String> strings;


    public PDFAdapter(Context context, List<String> strings) {
        this.context = context;
        this.strings = strings;
    }

    public void isDelete(boolean isDelete) {
        this.isDelete = isDelete;
        notifyDataSetChanged();
    }


    public void setListener(ItemClick itemClick){
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public PDFAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pdf, parent, false);
        return new PDFAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PDFAdapter.ViewHolder holder, final int position) {

        holder.tv_no.setText(String.valueOf((position + 1)));
        holder.tv_title.setText(new File(strings.get(position)).getName());


        if (AllPdfActivity.deletePathList.contains(strings.get(position))){
            holder.checkBox.setImageResource(R.drawable.ic_check_box_black_24dp);
        }else {
            holder.checkBox.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        }

        if (isDelete) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setOnClickListener(v -> {
                if (itemClick != null) {
                    itemClick.setSelectPath(true,strings.get(position));
                }
            });
        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.cell.setOnClickListener(v -> {
                if (itemClick != null) {
                    itemClick.itemClick(position);
                }
            });
        }


    }

    public interface ItemClick {
        void itemClick(int position);
        void setSelectPath(boolean isCheck,String path);
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_no, tv_title;
        ImageView checkBox;
        LinearLayout cell;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_no = itemView.findViewById(R.id.tv_no);
            checkBox = itemView.findViewById(R.id.check_box);
            tv_title = itemView.findViewById(R.id.tv_title);
            cell = itemView.findViewById(R.id.cell);


        }
    }
}
