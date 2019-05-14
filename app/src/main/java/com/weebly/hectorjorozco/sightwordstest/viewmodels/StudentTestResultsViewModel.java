package com.weebly.hectorjorozco.sightwordstest.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.weebly.hectorjorozco.sightwordstest.database.AppDatabase;
import com.weebly.hectorjorozco.sightwordstest.database.TestEntry;

import java.util.List;

public class StudentTestResultsViewModel extends ViewModel {

    private final LiveData<List<TestEntry>> testEntries;

    // Constructor that calls loadTaskById of the testDao to initialize the testEntries variable
    // The constructor receives the database and the studentId
    StudentTestResultsViewModel(AppDatabase appDatabase, int studentId) {
        testEntries = appDatabase.testDao().loadTestsById(studentId);
    }

    // COMPLETED (7) Create a getter for the task variable
    public LiveData<List<TestEntry>> getTestEntries() {
        return testEntries;
    }
}
