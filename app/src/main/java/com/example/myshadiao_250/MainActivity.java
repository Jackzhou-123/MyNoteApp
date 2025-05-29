package com.example.myshadiao_250;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private RecyclerView recyclerView;              // 笔记列表 RecyclerView
    private NoteAdapter noteAdapter;                // 自定义适配器
    private List<Note> noteList = new ArrayList<>(); // 存储所有笔记

    private TextView titleText;
    // 顶部标题
    private ImageView arrowIcon;    // 顶部的下拉图标
    private Spinner filterSpinner;  // 标签筛选 Spinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 加载主页面布局

        // 1. 绑定布局中的控件
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fab = findViewById(R.id.fab);
        titleText = findViewById(R.id.titleText);
        arrowIcon = findViewById(R.id.arrowIcon);
        filterSpinner = findViewById(R.id.filterSpinner); // 获取标签筛选控件

        // 2. 初始化 RecyclerView 适配器
        noteAdapter = new NoteAdapter(noteList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);

        // 3. 设置点击事件：点击笔记跳转到编辑页面
        noteAdapter.setOnItemClickListener(position -> {
            Note clickedNote = noteList.get(position);
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            intent.putExtra("note_id", clickedNote.getId()); // 传递笔记 ID
            startActivity(intent);
        });

        // 4. 设置长按事件：删除笔记
        noteAdapter.setOnItemLongClickListener(position -> {
            Note note = noteList.get(position);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("删除笔记")
                    .setMessage("确定要删除这条笔记吗？")
                    .setPositiveButton("删除", (dialog, which) -> {
                        new Thread(() -> {
                            NoteDatabase db = NoteDatabase.getInstance(MainActivity.this);
                            db.noteDao().deleteNote(note); // 删除笔记
                            loadNotesFromDatabase();       // 删除后刷新
                        }).start();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        // 5. 设置悬浮按钮：新增笔记
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivity(intent);
        });

        // 6. 设置顶部标题和箭头：展开或折叠列表
        titleText.setOnClickListener(v -> toggleRecyclerViewVisibility());
        arrowIcon.setOnClickListener(v -> toggleRecyclerViewVisibility());

        // 7. 加载标签列表并设置 Spinner 数据源
        loadTagsIntoSpinner();

        // 8. 设置标签筛选：监听标签选择变化
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                String selectedTag = parentView.getItemAtPosition(position).toString();
                filterNotesByTag(selectedTag);  // 根据标签筛选笔记
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 当没有选择任何项时的处理
            }
        });
    }

    // 页面重新进入时刷新数据（避免新增或编辑后数据不刷新）
    @Override
    protected void onResume() {
        super.onResume();
        loadNotesFromDatabase(); // 重新加载数据
    }

    // 从数据库加载所有笔记
    private void loadNotesFromDatabase() {
        new Thread(() -> {
            NoteDatabase db = NoteDatabase.getInstance(MainActivity.this);
            List<Note> latestNotes = db.noteDao().getAllNotes(); // 获取全部笔记

            runOnUiThread(() -> {
                noteList.clear();                 // 清空原列表
                noteList.addAll(latestNotes);     // 加载新数据
                noteAdapter.notifyDataSetChanged(); // 通知适配器刷新
            });
        }).start();
    }

    // 加载标签数据到 Spinner 中
    // 加载标签数据到 Spinner 中
    private void loadTagsIntoSpinner() {
        new Thread(() -> {
            NoteDatabase db = NoteDatabase.getInstance(MainActivity.this);
            List<String> tags = db.noteDao().getAllTags(); // 获取所有标签
            tags.add(0, "全部"); // 增加“全部”选项

            runOnUiThread(() -> {
                // 自定义 ArrayAdapter，设置字体颜色
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, tags) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = (TextView) view;
                        textView.setTextColor(getResources().getColor(R.color.text_primary)); // 设置默认显示字体颜色
                        return view;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView textView = (TextView) view;
                        textView.setTextColor(getResources().getColor(R.color.text_primary)); // 设置下拉项字体颜色
                        return view;
                    }
                };

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                filterSpinner.setAdapter(adapter); // 设置 Spinner 数据适配器
            });
        }).start();
    }

    // 根据标签筛选笔记
    private void filterNotesByTag(String tag) {
        new Thread(() -> {
            NoteDatabase db = NoteDatabase.getInstance(MainActivity.this);
            List<Note> filteredNotes;

            if ("全部".equals(tag)) {
                filteredNotes = db.noteDao().getAllNotes(); // 获取所有笔记
            } else {
                filteredNotes = db.noteDao().getNotesByTag(tag); // 获取指定标签的笔记
            }

            runOnUiThread(() -> {
                noteList.clear();                 // 清空原列表
                noteList.addAll(filteredNotes);   // 加载筛选后的数据
                noteAdapter.notifyDataSetChanged(); // 通知适配器刷新
            });
        }).start();
    }

    // 展开或折叠笔记列表
    private void toggleRecyclerViewVisibility() {
        if (recyclerView.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.GONE);
            arrowIcon.setImageResource(android.R.drawable.arrow_down_float); // 替换为你自己的图标也可以
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            arrowIcon.setImageResource(android.R.drawable.arrow_up_float);
        }
    }
}