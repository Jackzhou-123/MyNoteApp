package com.example.myshadiao_skin1;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> noteList;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public NoteAdapter(List<Note> noteList) {
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note_card, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);

        holder.titleTextView.setText(note.getTitle());
        holder.contentTextView.setText(note.getContent());

        String formattedTime = formatTimestamp(note.getTimestamp());
        holder.timestampTextView.setText(formattedTime);

        String tag = note.getTag();
        holder.tagText.setText(tag); // 设置标签文本

        Context context = holder.itemView.getContext();
        int colorRes;

        // 设置卡片背景和标签背景颜色
        switch (tag) {
            case "工作":
                colorRes = R.color.blue;
                holder.cardContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_note_work));
                break;
            case "生活":
                colorRes = R.color.green;
                holder.cardContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_note_life));
                break;
            case "学习":
                colorRes = R.color.orange;
                holder.cardContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_note_study));
                break;
            default:
                colorRes = R.color.gray;
                holder.cardContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_note_default));
                break;
        }

        // 安全设置标签背景颜色
        Drawable bg = ContextCompat.getDrawable(context, R.drawable.tag_background);
        if (bg != null) {
            Drawable tinted = bg.mutate(); // 防止多个 TextView 共用同一个 drawable 实例
            tinted.setTint(ContextCompat.getColor(context, colorRes));
            holder.tagText.setBackground(tinted);
        } else {
            Log.e("NoteAdapter", "tag_background 获取失败，使用默认颜色");
            holder.tagText.setBackgroundColor(ContextCompat.getColor(context, colorRes));
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, timestampTextView, tagText;
        ConstraintLayout cardContainer;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.note_title);
            contentTextView = itemView.findViewById(R.id.note_content);
            timestampTextView = itemView.findViewById(R.id.note_timestamp);
            tagText = itemView.findViewById(R.id.noteTag);
            cardContainer = itemView.findViewById(R.id.card_container);
        }
    }
}