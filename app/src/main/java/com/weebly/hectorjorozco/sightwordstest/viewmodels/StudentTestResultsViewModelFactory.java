package com.weebly.hectorjorozco.sightwordstest.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

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
