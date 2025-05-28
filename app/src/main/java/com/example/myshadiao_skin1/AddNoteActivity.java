package com.example.myshadiao_skin1;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddNoteActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private Button btnWork, btnStudy, btnLife;
    private Button saveButton;
    private Note currentNote;

    // 当前选中的标签，默认空字符串表示无选中
    private String selectedTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // 绑定控件
        titleEditText = findViewById(R.id.edit_note_title);
        contentEditText = findViewById(R.id.edit_note_content);

        btnWork = findViewById(R.id.btn_work);
        btnStudy = findViewById(R.id.btn_study);
        btnLife = findViewById(R.id.btn_life);

        saveButton = findViewById(R.id.save_note_button);

        // 按钮点击事件，点击选中对应标签，且只有一个按钮被选中
        btnWork.setOnClickListener(v -> selectTag("工作"));
        btnStudy.setOnClickListener(v -> selectTag("学习"));
        btnLife.setOnClickListener(v -> selectTag("生活"));

        // 获取传入的笔记 ID
        int noteId = getIntent().getIntExtra("note_id", -1);

        if (noteId != -1) {
            // 编辑模式，加载数据
            new Thread(() -> {
                NoteDatabase db = NoteDatabase.getInstance(this);
                currentNote = db.noteDao().getNoteById(noteId);

                runOnUiThread(() -> {
                    if (currentNote != null) {
                        titleEditText.setText(currentNote.getTitle());
                        contentEditText.setText(currentNote.getContent());

                        selectedTag = currentNote.getTag();
                        highlightSelectedButton(selectedTag);
                    }
                });
            }).start();
        }

        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String content = contentEditText.getText().toString().trim();

            if (title.isEmpty() && content.isEmpty()) {
                Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedTag.isEmpty()) {
                Toast.makeText(this, "请选择标签", Toast.LENGTH_SHORT).show();
                return;
            }

            long timestamp = System.currentTimeMillis();

            new Thread(() -> {
                NoteDatabase db = NoteDatabase.getInstance(this);

                if (currentNote != null) {
                    // 编辑模式更新
                    currentNote.setTitle(title);
                    currentNote.setContent(content);
                    currentNote.setTag(selectedTag);
                    currentNote.setTimestamp(timestamp);
                    db.noteDao().updateNote(currentNote);
                } else {
                    // 新建模式插入
                    Note newNote = new Note(title, content, timestamp, selectedTag);
                    db.noteDao().insertNote(newNote);
                }

                runOnUiThread(this::finish);
            }).start();
        });
    }

    // 选择标签按钮处理，设置选中标签并更新按钮样式
    private void selectTag(String tag) {
        selectedTag = tag;
        highlightSelectedButton(tag);
    }

    // 根据标签高亮对应按钮，取消其他按钮高亮
    private void highlightSelectedButton(String tag) {
        // 简单用背景色区分选中状态
        int selectedColor = Color.parseColor("#FFBB86FC"); // 紫色背景
        int defaultColor = Color.TRANSPARENT;

        btnWork.setBackgroundColor(tag.equals("工作") ? selectedColor : defaultColor);
        btnStudy.setBackgroundColor(tag.equals("学习") ? selectedColor : defaultColor);
        btnLife.setBackgroundColor(tag.equals("生活") ? selectedColor : defaultColor);
    }
}