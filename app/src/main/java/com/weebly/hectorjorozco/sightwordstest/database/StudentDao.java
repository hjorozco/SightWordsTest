package com.weebly.hectorjorozco.sightwordstest.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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
