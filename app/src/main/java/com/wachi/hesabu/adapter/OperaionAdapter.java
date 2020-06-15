package com.wachi.hesabu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wachi.hesabu.R;
import com.wachi.hesabu.model.OperationModel;
import com.wachi.hesabu.utils.Constant;

import java.util.List;

public class OperaionAdapter extends RecyclerView.Adapter<OperaionAdapter.ViewHolder> {


    private Activity context;
    private ItemClick itemClick;
    private List<OperationModel> operationModels;


    public OperaionAdapter(Activity context, List<OperationModel> operationModels) {
        this.context = context;
        this.operationModels = operationModels;
    }

    public void setClickListener(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public OperaionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_operation, parent, false);
        return new OperaionAdapter.ViewHolder(view);
    }

    public void setSelectPosition(List<OperationModel> operationModels) {
        this.operationModels = operationModels;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final OperaionAdapter.ViewHolder holder, final int position) {

        Constant.setDefaultLanguage(context);
        holder.textView.setText(operationModels.get(position).name);

        if (operationModels.get(position).isCheck) {
            holder.imageView.setImageResource(R.drawable.ic_check_box_black_24dp);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        }

        holder.imageView.setOnClickListener(v -> {
            if (itemClick != null) {
                itemClick.itemClick(position);
            }
        });


    }


    public interface ItemClick {
        void itemClick(int position);
    }

    @Override
    public int getItemCount() {
        return operationModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;


        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);


        }
    }
}
