package com.wachi.hesabu.adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wachi.hesabu.R;
import com.wachi.hesabu.model.MainModel;
import com.wachi.hesabu.utils.Constant;

import java.util.List;

import static com.wachi.hesabu.utils.Constant.getTextString;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {


    private Activity context;
    private MainItemClick mainItemClick;
    private List<MainModel> mainModels;
    private int width;
    private int height;
    SharedPreferences mSharedPrefs;
    public MainAdapter(Activity context, List<MainModel> mainModels, int width, int height) {
        this.context = context;
        this.mainModels = mainModels;
        this.width = width;
        this.height = height;
    }

    public interface MainItemClick {
        void mainItemClick(int main_id, int position, String title, int themePosition);
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_main, parent, false);
        return new ViewHolder(view);
    }

    public void setMainClickListener(MainItemClick mainClickListener) {
        this.mainItemClick = mainClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, final int pos) {

        Constant.setDefaultLanguage(context);
        holder.textView.setText(mainModels.get(pos).title);


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height / 4);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layoutParams.leftMargin = (int) context.getResources().getDimension(R.dimen.dp_10);
        layoutParams.rightMargin = (int) context.getResources().getDimension(R.dimen.dp_10);
        layoutParams.topMargin = (int) context.getResources().getDimension(R.dimen.dp_3);
        layoutParams.bottomMargin = (int) context.getResources().getDimension(R.dimen.dp_3);
        holder.cell.setLayoutParams(layoutParams);
        final MainModel mainModel = mainModels.get(pos);

        if (pos == 0 || pos == 1) {
            holder.text_total_question.setText(getTextString((mainModels.get(pos).totalQuestion) + context.getString(R.string.str_space) + context.getString(R.string.quiz)));
        } else {
            holder.text_total_question.setText(getTextString(mainModel.totalQuestion + context.getString(R.string.str_space) + context.getString(R.string.quiz)));
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        SubAdapter subAdapter = new SubAdapter(context, mainModel.operationList, mainModel.id, width);
        holder.recyclerView.setAdapter(subAdapter);
        subAdapter.setSubClickListener((position, title, themePosition) -> {
            if (mainItemClick != null) {
                mainItemClick.mainItemClick(mainModel.id, position, title, themePosition);
            }
        });

        if (pos == 3) {
            holder.btn_next.setOnClickListener(view -> {
                holder.recyclerView.smoothScrollToPosition(5);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mainModels.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        RelativeLayout cell;
        TextView textView, text_total_question;
        ImageView btn_next;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            textView = itemView.findViewById(R.id.textView);
            text_total_question = itemView.findViewById(R.id.text_total_question);
            btn_next = itemView.findViewById(R.id.btn_next);
            mSharedPrefs= PreferenceManager.getDefaultSharedPreferences(context);
            if (mSharedPrefs.getBoolean("enablethemeMode", false) == true) {
                btn_next.setImageResource(R.drawable.ic_arrow_forward_white_24dp);
            }

            cell = itemView.findViewById(R.id.cell);


        }
    }
}
