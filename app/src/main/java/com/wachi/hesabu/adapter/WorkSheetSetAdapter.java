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
import com.wachi.hesabu.model.PDFSetModel;
import com.wachi.hesabu.utils.Constant;

import java.util.List;

import static com.wachi.hesabu.ui.WorkSheetActivity.operationSet;

public class WorkSheetSetAdapter extends RecyclerView.Adapter<WorkSheetSetAdapter.ViewHolder> {


    private Activity context;
    private ItemClick itemClick;
    private List<PDFSetModel> pdfSetModels;

    public WorkSheetSetAdapter(Activity context, List<PDFSetModel> pdfSetModels) {
        this.context = context;
        this.pdfSetModels = pdfSetModels;

    }

    public void setClickListener(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public WorkSheetSetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_worksheet_set, parent, false);
        return new WorkSheetSetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final WorkSheetSetAdapter.ViewHolder holder, final int position) {

        final PDFSetModel setModel = pdfSetModels.get(position);
        Constant.setDefaultLanguage(context);

        holder.textView.setText(Constant.getTitleList(setModel.tableName, context).get((setModel.id - 1)).title);


        if (operationSet.contains(setModel.title)) {
            holder.imageView.setImageResource(R.drawable.ic_check_box_black_24dp);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        }


        holder.imageView.setOnClickListener(v -> {
            if (itemClick != null) {
                itemClick.itemClick(position, Constant.getTitleList(setModel.tableName, context).get((setModel.id - 1)).id, Constant.getTitleList(setModel.tableName, context).get((setModel.id - 1)).category);
            }
        });


    }


    public interface ItemClick {
        void itemClick(int position, int id, String category);
    }

    @Override
    public int getItemCount() {
        return pdfSetModels.size();
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
