package com.example.myshadiao_skin1;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    long insertNote(Note note);

    @Update
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    Note getNoteById(int noteId);

    @Query("SELECT * FROM notes WHERE tag = :tag ORDER BY timestamp DESC")
    List<Note> getNotesByTag(String tag);

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    List<Note> getAllNotes();

    @Query("SELECT DISTINCT tag FROM notes")
    List<String> getAllTags();  // 获取所有不同的标签
}