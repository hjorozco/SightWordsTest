package com.weebly.hectorjorozco.sightwordstest.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

// This data access object (dao) is used by the Room persistence library to access the Students data
// on the applications database.

@Dao
public interface TestDao {

    @Query("SELECT * FROM tests WHERE id = :id ORDER BY date")
    LiveData<List<TestEntry>> loadTestsById(int id);

    @Query("DELETE FROM tests WHERE id = :id")
    void deleteTestsById(int id);

    @Insert
    void insertTest(TestEntry testEntry);

    @Delete
    void deleteTest(TestEntry testEntry);

}
