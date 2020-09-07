package com.weebly.hectorjorozco.sightwordstest.ui;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.weebly.hectorjorozco.sightwordstest.R;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.NUMBER_OF_STUDENTS_KEY;

public class AddStudentActivity extends AppCompatActivity implements AddStudentFragment.StudentAddedListener {

    public static final String STUDENT_ADDED_FIRST_NAME_KEY = "student_added_first_name";
    public static final String STUDENT_ADDED_LAST_NAME_KEY = "student_added_last_name";
    public static final String STUDENT_ADDED_NUMBER_OF_STUDENTS_KEY = "student_added_number_of_students";

    // Will store the number of students passed to the activity from MainActivity.java
    int numberOfStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null && bundle.containsKey(NUMBER_OF_STUDENTS_KEY)) {
            numberOfStudents = bundle.getInt(NUMBER_OF_STUDENTS_KEY);
        }

        setContentView(R.layout.activity_add_student);
    }

    // Listens to the name of the student added on AddStudentFragment
    @Override
    public void onStudentAdded(String firstName, String lastName, int numberOfStudents) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(STUDENT_ADDED_FIRST_NAME_KEY, firstName);
        resultIntent.putExtra(STUDENT_ADDED_LAST_NAME_KEY, lastName);
        resultIntent.putExtra(STUDENT_ADDED_NUMBER_OF_STUDENTS_KEY, numberOfStudents);
        setResult(Activity.RESULT_OK, resultIntent);
    }
}
