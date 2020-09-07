package com.weebly.hectorjorozco.sightwordstest.viewmodels;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.weebly.hectorjorozco.sightwordstest.database.AppDatabase;

public class StudentTestResultsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mStudentId;

    public StudentTestResultsViewModelFactory(AppDatabase database, int studentId) {
        mDb = database;
        mStudentId = studentId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new StudentTestResultsViewModel(mDb, mStudentId);
    }
}
