package com.weebly.hectorjorozco.sightwordstest.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.weebly.hectorjorozco.sightwordstest.database.AppDatabase;
import com.weebly.hectorjorozco.sightwordstest.database.StudentEntry;

import java.util.List;

// ViewModel that will observe MainActivity for changes on the students data

public class MainViewModel extends AndroidViewModel {

    // Cash the list of StudentEntry objects wrapped in a LiveData object
    private final LiveData<List<StudentEntry>> students;

    public MainViewModel(Application application) {
        super(application);
        // Gets all students from the Database
        AppDatabase appDatabase = AppDatabase.getInstance(this.getApplication());
        students = appDatabase.studentDao().loadAllStudentsLiveData();
    }

    public LiveData<List<StudentEntry>> getStudents() {
        return students;
    }
}
