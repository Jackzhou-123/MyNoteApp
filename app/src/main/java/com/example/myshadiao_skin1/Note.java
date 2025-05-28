// 包名
package com.example.myshadiao_skin1;

// 导入 Room 相关注解
import androidx.room.Entity;        // 表明这个类是一个数据库表
import androidx.room.PrimaryKey;   // 表示主键
import androidx.room.ColumnInfo;   // 设置字段的列名
import androidx.room.Ignore;       // 忽略构造函数或字段，不参与数据库映射

// 用 @Entity 注解说明：这个类对应数据库中的一张表，表名为 notes
@Entity(tableName = "notes")
public class Note {

    // 主键字段，autoGenerate = true 表示主键自动递增
    @PrimaryKey(autoGenerate = true)
    private int id;

    // 列名为 title，用于保存笔记标题
    @ColumnInfo(name = "title")
    private String title;

    // 列名为 content，用于保存笔记内容
    @ColumnInfo(name = "content")
    private String content;

    // 列名为 timestamp，用于保存创建或修改时间（时间戳，单位毫秒）
    @ColumnInfo(name = "timestamp")
    private long timestamp;

    // 列名为 tag，用于保存笔记标签（比如 工作、学习、生活）
    @ColumnInfo(name = "tag")
    private String tag;

    // ✅ 这是 Room 必须要有的“无参构造函数”
    // Room 在创建对象时会调用这个构造方法
    public Note() {
    }

    // ✅ 这是我们自定义的“有参构造函数”，用于新建笔记时更方便赋值
    // 用 @Ignore 告诉 Room 不使用这个构造函数建表，只用于程序中调用
    @Ignore
    public Note(String title, String content, long timestamp, String tag) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.tag = tag;
    }

    // ===== Getter 和 Setter（用于读取和修改字段）=====

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    // ✅ 这是一个格式化方法，把时间戳转成“2025-05-16 14:30”这种格式的字符串
    // 你可以在 UI 上调用 note.getTime() 显示人类可读的时间
    public String getTime() {
        return android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", timestamp).toString();
    }
}