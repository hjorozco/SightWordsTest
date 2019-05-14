package com.weebly.hectorjorozco.sightwordstest.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

// This data access object (dao) is used by the Room persistence library to access the Students data
// on the applications database.

@Dao
public interface StudentDao {

    @Query("SELECT * FROM students")
    LiveData<List<StudentEntry>> loadAllStudentsLiveData();

    // Used by the application widget
    @Query("SELECT * FROM students" )
    List<StudentEntry> loadAllStudentsData();

    @Query("SELECT * FROM students WHERE first_name = :firstName AND last_name = :lastName")
    List<StudentEntry> findStudentsWithName(String firstName, String lastName);

    @Insert
    void insertStudent(StudentEntry studentEntry);

    @Delete
    void deleteStudent(StudentEntry studentEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateStudent(StudentEntry studentEntry);

}
