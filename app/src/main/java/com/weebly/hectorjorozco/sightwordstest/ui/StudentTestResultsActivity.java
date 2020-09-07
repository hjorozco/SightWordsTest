package com.weebly.hectorjorozco.sightwordstest.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.weebly.hectorjorozco.sightwordstest.R;

import com.weebly.hectorjorozco.sightwordstest.database.StudentEntry;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.STUDENT_ENTRY_KEY;

// Displays the test results on a phone
public class StudentTestResultsActivity extends AppCompatActivity {

    // Will store the Student info passed to the activity
    StudentEntry studentEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(STUDENT_ENTRY_KEY)) {
            studentEntry = bundle.getParcelable(STUDENT_ENTRY_KEY);
        }

        // If this activity was called from the Widget on a Tablet start MainActivity
        if (studentEntry ==null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_student_test_results);
    }

}
