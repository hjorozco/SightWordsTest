package com.weebly.hectorjorozco.sightwordstest.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

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
