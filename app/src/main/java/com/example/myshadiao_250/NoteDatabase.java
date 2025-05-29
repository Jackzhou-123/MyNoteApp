package com.example.myshadiao_250;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// 声明使用的实体类和版本号
@Database(entities = {Note.class}, version = 2, exportSchema = false)  // 关闭 schema 导出
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance;

    // 提供 DAO
    public abstract NoteDao noteDao();

    // 单例模式获取数据库实例
    public static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            NoteDatabase.class,
                            "note_database"
                    )
                    .allowMainThreadQueries()  // 这里是为了简化开发，建议在生产环境中移除
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}