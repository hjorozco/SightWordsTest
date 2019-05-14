package com.weebly.hectorjorozco.sightwordstest.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Represents an entry in the tests table. Used by Room Persistence Library.
 */
@Entity(tableName = "tests", primaryKeys = {"id", "date"})
public class TestEntry {

    private final int id;
    @NonNull
    private final Date date;
    private final int grade;
    @ColumnInfo(name = "unknown_words")
    private final String unknownWords;

    // Constructor used by Room library and TestActivity
    public TestEntry(int id, @NonNull Date date, int grade, String unknownWords) {
        this.id = id;
        this.date = date;
        this.grade = grade;
        this.unknownWords = unknownWords;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public int getGrade() {
        return grade;
    }

    public String getUnknownWords() {
        return unknownWords;
    }

}
