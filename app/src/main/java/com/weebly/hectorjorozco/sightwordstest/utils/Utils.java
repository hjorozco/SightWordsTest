package com.weebly.hectorjorozco.sightwordstest.utils;

import android.content.Context;
import android.os.Vibrator;
import android.widget.EditText;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.database.AppDatabase;
import com.weebly.hectorjorozco.sightwordstest.database.StudentEntry;
import com.weebly.hectorjorozco.sightwordstest.database.TestEntry;
import com.weebly.hectorjorozco.sightwordstest.executors.AppExecutors;
import com.weebly.hectorjorozco.sightwordstest.models.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.EMPTY_STRING;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.STUDENT_WITH_NO_TESTS_GRADE;

public class Utils {

    private static final long ONE_DAY_IN_MILLISECONDS = 86400000;
    private static final long ONE_HOUR_IN_MILLISECONDS = 3600000;

    private static AppDatabase mAppDatabase;


    /**
     * If an EditText text is empty sets an error on it.
     *
     * @param firstNameEditText The EditText that will be checked
     * @return true if the EditText is empty, false otherwise.
     */
    public static boolean atLeastOneEditTextEmpty(EditText firstNameEditText, EditText lastNameEditText, Context context) {

        boolean firstNameEmpty = false;
        boolean lastNameEmpty = false;
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();

        if (firstName.isEmpty()) {
            firstNameEditText.requestFocus();
            firstNameEditText.setError(context.getString(R.string.fragment_add_student_first_name_error_message));
            firstNameEmpty = true;
        }

        if (lastName.isEmpty()) {
            if (!firstNameEmpty) {
                lastNameEditText.requestFocus();
            }
            lastNameEditText.setError(context.getString(R.string.fragment_add_student_last_name_error_message));
            lastNameEmpty = true;
        }

        return firstNameEmpty || lastNameEmpty;

    }


    // Helper method that creates a sample class by inserting 5 students with some results
    public static void createSampleClass(Context context) {

        final int defaultTestType = TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(context);
        final int differentTestType;
        if (defaultTestType == 0) {
            differentTestType = 1;
        } else if (defaultTestType < 6) {
            differentTestType = defaultTestType - 1;
        } else if (defaultTestType == 6) {
            differentTestType = 7;
        } else {
            differentTestType = defaultTestType - 1;
        }

        mAppDatabase = AppDatabase.getInstance(context);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String firstName, lastName, unknownWords;
                int numberOfUnknownWords, testType, testResult, studentId;
                StudentEntry studentEntry;
                TestEntry testEntry;
                Date date;

                // Inserts student 1 with 4 tests.
                testType = defaultTestType;
                firstName = "Vanessa";
                lastName = "Brown";

                mAppDatabase.studentDao().insertStudent(new StudentEntry(firstName, lastName,
                        STUDENT_WITH_NO_TESTS_GRADE, testType, new Date(), EMPTY_STRING));

                studentEntry = mAppDatabase.studentDao().findStudentsWithName(firstName, lastName).get(0);
                studentId = studentEntry.getId();

                numberOfUnknownWords = 10;
                testEntry = new TestEntry(studentId, new Date(new Date().getTime() - ONE_DAY_IN_MILLISECONDS - ONE_HOUR_IN_MILLISECONDS),
                        WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords,
                        getSampleTestUnknownWords(testType, numberOfUnknownWords));
                mAppDatabase.testDao().insertTest(testEntry);

                numberOfUnknownWords = 8;
                testEntry = new TestEntry(studentId, new Date(new Date().getTime() - ONE_DAY_IN_MILLISECONDS),
                        WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords,
                        getSampleTestUnknownWords(testType, numberOfUnknownWords));
                mAppDatabase.testDao().insertTest(testEntry);

                numberOfUnknownWords = 3;
                testEntry = new TestEntry(studentId, new Date(new Date().getTime() - 3 * ONE_HOUR_IN_MILLISECONDS),
                        WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords,
                        getSampleTestUnknownWords(testType, numberOfUnknownWords));
                mAppDatabase.testDao().insertTest(testEntry);

                numberOfUnknownWords = 0;
                unknownWords = getSampleTestUnknownWords(testType, numberOfUnknownWords);
                testResult = WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords;
                date = new Date();
                testEntry = new TestEntry(studentId, date, testResult, unknownWords);
                mAppDatabase.testDao().insertTest(testEntry);

                studentEntry = new StudentEntry(firstName, lastName, testResult, testType, date, unknownWords);
                studentEntry.setId(studentId);
                mAppDatabase.studentDao().updateStudent(studentEntry);


                // Inserts student 2 with 5 tests.
                testType = defaultTestType;
                firstName = "Kathy";
                lastName = "Johnson";

                mAppDatabase.studentDao().insertStudent(new StudentEntry(firstName, lastName,
                        STUDENT_WITH_NO_TESTS_GRADE, testType, new Date(), EMPTY_STRING));

                studentEntry = mAppDatabase.studentDao().findStudentsWithName(firstName, lastName).get(0);
                studentId = studentEntry.getId();

                numberOfUnknownWords = 15;
                testEntry = new TestEntry(studentId, new Date(new Date().getTime() - 2* ONE_DAY_IN_MILLISECONDS - 5* ONE_HOUR_IN_MILLISECONDS),
                        WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords,
                        getSampleTestUnknownWords(testType, numberOfUnknownWords));
                mAppDatabase.testDao().insertTest(testEntry);

                numberOfUnknownWords = 10;
                testEntry = new TestEntry(studentId, new Date(new Date().getTime() - ONE_DAY_IN_MILLISECONDS - 2* ONE_HOUR_IN_MILLISECONDS),
                        WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords,
                        getSampleTestUnknownWords(testType, numberOfUnknownWords));
                mAppDatabase.testDao().insertTest(testEntry);

                numberOfUnknownWords = 13;
                testEntry = new TestEntry(studentId, new Date(new Date().getTime() -  ONE_DAY_IN_MILLISECONDS),
                        WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords,
                        getSampleTestUnknownWords(testType, numberOfUnknownWords));
                mAppDatabase.testDao().insertTest(testEntry);

                numberOfUnknownWords = 0;
                testEntry = new TestEntry(studentId, new Date(new Date().getTime() - 2 * ONE_HOUR_IN_MILLISECONDS),
                        WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords,
                        getSampleTestUnknownWords(testType, numberOfUnknownWords));
                mAppDatabase.testDao().insertTest(testEntry);

                numberOfUnknownWords = 3;
                unknownWords = getSampleTestUnknownWords(testType, numberOfUnknownWords);
                testResult = WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords;
                date = new Date();
                testEntry = new TestEntry(studentId, date, testResult, unknownWords);
                mAppDatabase.testDao().insertTest(testEntry);

                studentEntry = new StudentEntry(firstName, lastName, testResult, testType, date, unknownWords);
                studentEntry.setId(studentId);
                mAppDatabase.studentDao().updateStudent(studentEntry);


                // Inserts student 3 with 9 tests.
                testType = differentTestType;
                firstName = "Julie";
                lastName = "Jones";

                mAppDatabase.studentDao().insertStudent(new StudentEntry(firstName, lastName,
                        STUDENT_WITH_NO_TESTS_GRADE, testType, new Date(), EMPTY_STRING));

                studentEntry = mAppDatabase.studentDao().findStudentsWithName(firstName, lastName).get(0);
                studentId = studentEntry.getId();

                numberOfUnknownWords = 25;
                testEntry = new TestEntry(studentId, new Date(new Date().getTime() - 4* ONE_DAY_IN_MILLISECONDS - ONE_HOUR_IN_MILLISECONDS),
                        WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords,
                        getSampleTestUnknownWords(testType, numberOfUnknownWords));
                mAppDatabase.testDao().insertTest(testEntry);

                numberOfUnknownWords = 28;
                testEntry = new TestEntry(studentId, new Date(new Date().getTime() - 4* ONE_DAY_IN_MILLISECONDS),
                        WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords,
                        getSampleTestUnknownWords(testType, numberOfUnknownWords));
                mAppDatabase.testDao().insertTest(testEntry);

                numberOfUnknownWords = 15;
                testEntry = new TestEntry(studentId, new Date(new Date().getTime() -  3* ONE_DAY_IN_MILLISECONDS - 2 * ONE_HOUR_IN_MILLISECONDS),
                        WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords,
                        getSampleTestUnknownWords(testType, numberOfUnknownWords));
                mAppDatabase.testDao().insertTest(testEntry);

                numberOfUnknownWords = 12;
                testEntry = new TestEntry(studentId, new Date(new Date().getTime() - 3 * ONE_DAY_IN_MILLISECONDS),
                        WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords,
                        getSampleTestUnknownWords(testType, numberOfUnknownWords));
                mAppDatabase.testDao().insertTest(testEntry);

                numberOfUnknownWords = 10;
                testEntry = new TestEntry(studentId, new Date(new Date().getTime() - 2* ONE_DAY_IN_MILLISECONDS - 2* ONE_HOUR_IN_MILLISECONDS),
                        WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords,
                        getSampleTestUnknownWords(testType, numberOfUnknownWords));
                mAppDatabase.testDao().insertTest(testEntry);

                numberOfUnknownWords = 5;
                testEntry = new TestEntry(studentId, new Date(new Date().getTime() - 2* ONE_DAY_IN_MILLISECONDS - ONE_HOUR_IN_MILLISECONDS),
                        WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords,
                        getSampleTestUnknownWords(testType, numberOfUnknownWords));
                mAppDatabase.testDao().insertTest(testEntry);

                numberOfUnknownWords = 3;
                testEntry = new TestEntry(studentId, new Date(new Date().getTime() -  2* ONE_DAY_IN_MILLISECONDS),
                        WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords,
                        getSampleTestUnknownWords(testType, numberOfUnknownWords));
                mAppDatabase.testDao().insertTest(testEntry);

                numberOfUnknownWords = 1;
                testEntry = new TestEntry(studentId, new Date(new Date().getTime() - ONE_HOUR_IN_MILLISECONDS),
                        WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords,
                        getSampleTestUnknownWords(testType, numberOfUnknownWords));
                mAppDatabase.testDao().insertTest(testEntry);

                numberOfUnknownWords = 0;
                unknownWords = getSampleTestUnknownWords(testType, numberOfUnknownWords);
                testResult = WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords;
                date = new Date();
                testEntry = new TestEntry(studentId, date, testResult, unknownWords);
                mAppDatabase.testDao().insertTest(testEntry);

                studentEntry = new StudentEntry(firstName, lastName, testResult, testType, date, unknownWords);
                studentEntry.setId(studentId);
                mAppDatabase.studentDao().updateStudent(studentEntry);


                // Inserts student 4 with no tests.
                mAppDatabase.studentDao().insertStudent(new StudentEntry("Sarah", "Jones",
                        STUDENT_WITH_NO_TESTS_GRADE, defaultTestType, new Date(), EMPTY_STRING));


                // Inserts student 5 with 1 tests.
                testType = differentTestType;
                firstName = "Hector";
                lastName = "Rodriguez";

                mAppDatabase.studentDao().insertStudent(new StudentEntry(firstName, lastName,
                        STUDENT_WITH_NO_TESTS_GRADE, testType, new Date(), EMPTY_STRING));

                studentEntry = mAppDatabase.studentDao().findStudentsWithName(firstName, lastName).get(0);
                studentId = studentEntry.getId();

                numberOfUnknownWords = 7;
                unknownWords = getSampleTestUnknownWords(testType, numberOfUnknownWords);
                testResult = WordUtils.getNumberOfWordsOnList(testType) - numberOfUnknownWords;
                date = new Date();
                testEntry = new TestEntry(studentId, date, testResult, unknownWords);
                mAppDatabase.testDao().insertTest(testEntry);

                studentEntry = new StudentEntry(firstName, lastName, testResult, testType, date, unknownWords);
                studentEntry.setId(studentId);
                mAppDatabase.studentDao().updateStudent(studentEntry);

            }
        });
    }


    /**
     * Helper method that gets a random unknown words for a sample test
     * @param testType The test type used to get the list of words
     * @param numberOfUnknownWords The number of unknown words that will be returned
     * @return A String that contains unknown words separated by commas.
     */
    private static String getSampleTestUnknownWords(int testType, int numberOfUnknownWords) {
        StringBuilder unknownWordsStringBuilder = new StringBuilder();
        List<Word> words = WordUtils.getListOfWords(testType);
        List<Integer> unknownWordsPositions = new ArrayList<>();
        List<Integer> wordsPositions = new ArrayList<>();

        // Creates a list of all the positions in order and shuffles it
        for(int i = 0; i < WordUtils.getNumberOfWordsOnList(testType); i++)
        {
            wordsPositions.add(i);
        }
        Collections.shuffle(wordsPositions);

        if (words != null) {

            // Gets the positions for the unknown words and sorts them
            for (int i = 0; i < numberOfUnknownWords; i++) {
                unknownWordsPositions.add(wordsPositions.get(i));
            }
            Collections.sort(unknownWordsPositions);

            // Gets the unknown words from the words list
            for (int i = 0; i < numberOfUnknownWords; i++) {
                unknownWordsStringBuilder.append(words.get(unknownWordsPositions.get(i)).getWord());
                if (i < numberOfUnknownWords - 1) {
                    unknownWordsStringBuilder.append(", ");
                }
            }

            return unknownWordsStringBuilder.toString();
        } else {
            return EMPTY_STRING;
        }
    }


    public static void vibrate(Context context, int time){
        Vibrator vibrator =(Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(time);
        }
    }

}
