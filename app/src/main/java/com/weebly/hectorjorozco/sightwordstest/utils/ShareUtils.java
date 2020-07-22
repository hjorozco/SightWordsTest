package com.weebly.hectorjorozco.sightwordstest.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.database.StudentEntry;
import com.weebly.hectorjorozco.sightwordstest.database.TestEntry;
import com.weebly.hectorjorozco.sightwordstest.models.ShareResult;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.SHARE_RESULT_DATA_SHARED;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.SHARE_RESULT_NO_APP;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.SHARE_RESULT_NO_FILE_CREATED;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.STUDENT_WITH_NO_TESTS_GRADE;

public class ShareUtils {

    private static final String DATE_FORMAT = "MM/dd/yyy hh:mm aaa";

    private static final String LOG_TAG = ShareUtils.class.getSimpleName();

    private static final String CSV_FILE_EXTENSION = ".csv";
    private static final String PDF_FILE_EXTENSION = ".pdf";
    private static final int LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH = 612;
    private static final int LETTER_SIZE_PAGE_POSTSCRIPT_HEIGHT = 792;
    private static final int MAX_NUMBER_OF_STUDENTS_ON_PDF_PAGE = 19;
    private static final int MAX_NUMBER_OF_TESTS_ON_PDF_PAGE = 27;

    private static final String EMPTY_STRING = "";

    /**
     * Create a CSV file with the class or student results information, saves it in the documents
     * directory of the app private external storage and shares it.
     *
     * @param context                    Context used to get access to resources
     * @param studentEntries             a list of Student Entries to be shared. It is null when Test Entries
     *                                   will be shared.
     * @param testEntries                a list of Test Entries to be shared. It is null when Student Entries
     *                                   will be shared.
     * @param detailed                   boolean flag. It is true when the shared csv file will include words not
     *                                   read correctly, false otherwise.
     * @param csvFileNamePart1           The first part of the csv file name. For class results shared it is
     *                                   "Class", for student results shared it is
     *                                   "FirstNameLastName_TestTypeNumberOfWords"
     * @param numberOfWordsOnStudentTest Only used when Test Entries for a particular student are being
     *                                   shared.
     * @return the CSV file created and shared.
     */
    public static ShareResult shareCsvFile(Context context, List<StudentEntry> studentEntries,
                                           List<TestEntry> testEntries, boolean detailed,
                                           String csvFileNamePart1, int numberOfWordsOnStudentTest) {

        // Sets a flag that indicates if the file shared is "class results" or "student results"
        boolean sharingClassResults = studentEntries != null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

        String csvFileNamePart2;

        if (sharingClassResults) {
            if (detailed) {
                csvFileNamePart2 = context.getString(R.string.share_class_results_detailed_text);
            } else {
                csvFileNamePart2 = context.getString(R.string.share_class_results_simple_text);
            }
        } else {
            if (detailed) {
                csvFileNamePart2 = context.getString(R.string.share_student_results_detailed_text);
            } else {
                csvFileNamePart2 = context.getString(R.string.share_student_results_simple_text);
            }
        }

        String csvFileName = csvFileNamePart1 + csvFileNamePart2 + CSV_FILE_EXTENSION;

        File csvFile = new File(context.getExternalFilesDirs(
                Environment.DIRECTORY_DOCUMENTS)[0], csvFileName);

        String[] csvHeader;
        Object[] record;

        try {
            // Gets the Buffered Writer to write the csv file on the user's
            // public documents directory.
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
                    csvFile));

            // Defines the csv file header
            int csvHeaderStringArrayId;
            if (sharingClassResults) {
                if (detailed) {
                    csvHeaderStringArrayId = R.array.share_class_results_detailed_csv_file_header_array;
                } else {
                    csvHeaderStringArrayId = R.array.share_class_results_simple_csv_file_header_array;
                }
            } else {
                if (detailed) {
                    csvHeaderStringArrayId = R.array.share_student_results_detailed_csv_file_header_array;
                } else {
                    csvHeaderStringArrayId = R.array.share_student_results_simple_csv_file_header_array;
                }
            }
            csvHeader = context.getResources().getStringArray(csvHeaderStringArrayId);

            // Defines the csv file printer where the record will be printed
            CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT
                    .withHeader(csvHeader));

            String dateString;
            String gradeString;
            String status;
            String testTypeString;
            int grade;
            int testType;
            int numberOfWordsOnList;

            // Defines the records and print them on the csv file
            if (sharingClassResults) {
                // For each student entry print a record on the CSV file
                for (StudentEntry studentEntry : studentEntries) {

                    grade = studentEntry.getGrade();

                    if (grade == STUDENT_WITH_NO_TESTS_GRADE) {
                        dateString = context.getString(R.string.student_with_no_tests_text);
                        gradeString = EMPTY_STRING;
                    } else {
                        dateString = simpleDateFormat.format(studentEntry.getLastTestDate());
                        gradeString = String.valueOf(grade);
                    }

                    testType = studentEntry.getTestType();
                    numberOfWordsOnList = WordUtils.getNumberOfWordsOnList(testType);
                    testTypeString = TestTypeUtils.getTestTypeString(context, testType);

                    if (grade == numberOfWordsOnList) {
                        status = context.getString(R.string.yes_uppercase_text);
                    } else {
                        status = context.getString(R.string.no_lowercase_text);
                    }

                    if (detailed) {
                        record = new Object[]{studentEntry.getFirstName(), studentEntry.getLastName(),
                                context.getString(R.string.activity_main_csv_file_test_type_text,
                                        testTypeString, numberOfWordsOnList), dateString, gradeString,
                                status, studentEntry.getUnknownWords()};
                    } else {
                        record = new Object[]{studentEntry.getFirstName(), studentEntry.getLastName(),
                                context.getString(R.string.activity_main_csv_file_test_type_text,
                                        testTypeString, numberOfWordsOnList), dateString, gradeString,
                                status};
                    }

                    csvPrinter.printRecord(record);
                }
            } else {
                // For each test entry print a record on the CSV file
                for (TestEntry testEntry : testEntries) {

                    grade = testEntry.getGrade();
                    dateString = simpleDateFormat.format(testEntry.getDate());
                    gradeString = String.valueOf(grade);

                    if (grade == numberOfWordsOnStudentTest) {
                        status = context.getString(R.string.yes_uppercase_text);
                    } else {
                        status = context.getString(R.string.no_lowercase_text);
                    }

                    if (detailed) {
                        record = new Object[]{dateString, gradeString,
                                status, testEntry.getUnknownWords()};
                    } else {
                        record = new Object[]{dateString, gradeString,
                                status};
                    }

                    csvPrinter.printRecord(record);
                }
            }

            csvPrinter.flush();

        } catch (IOException e) {
            return null;
        }

        if (csvFile.exists()) {
            String chooserTitle;
            if (detailed) {
                chooserTitle = context.getString(R.string.menu_main_action_share_csv_detailed_chooser_title);
            } else {
                chooserTitle = context.getString(R.string.menu_main_action_share_csv_simple_chooser_title);
            }
            // Create and Intent to share the CSV file
            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            intentShareFile.setType("text/csv");
            //  intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + csvFile.getPath()));
            intentShareFile.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                    context, context.getApplicationContext().getPackageName() + ".provider", csvFile));
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, csvFileName);
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Check if there is an app that can share the data
            if (intentShareFile.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(Intent.createChooser(intentShareFile, chooserTitle));
                return new ShareResult(csvFile, SHARE_RESULT_DATA_SHARED);
            } else {
                return new ShareResult(null, SHARE_RESULT_NO_APP);
            }
        } else {
            return new ShareResult(null, SHARE_RESULT_NO_FILE_CREATED);
        }
    }


    /**
     * Create a PDF file with the class or student test results information, saves it in the documents directory
     * * of the app private external storage and shares it. It DOES NOT show the words not read correctly.
     *
     * @param context                       Context used to get access to resources.
     * @param studentEntries                List of a class student entries to be shared. Null if only one student tests will be shared.
     * @param testEntries                   List of one student test results. Null if a whole class test results will be shared.
     * @param fileNamePart1                 The first part of the PDF file name. For class results shared it is
     *                                      "Class", for student results shared it is "FirstNameLastName_TestTypeNumberOfWords"
     * @param studentNameForStudentResults  The complete name of the student to be displayed at the top of
     *                                      the PDF file when only one student test results are being shared.
     * @param testTitleForStudentResults    The test title to be displayed below the student name in the PDF
     *                                      file when only one student test results are being shared.
     * @param testTypeForStudentTestResults The test type of the student when only one student test results
     *                                      are being shared.
     * @return a PDF file that was shared, null if the file was not created or shared.
     */
    public static ShareResult shareSimplePdfFile(Context context, @Nullable List<StudentEntry> studentEntries,
                                          List<TestEntry> testEntries, String fileNamePart1,
                                          String studentNameForStudentResults,
                                          String testTitleForStudentResults, int testTypeForStudentTestResults) {

        // Sets a flag that indicates if the file shared is "class results" or "student results"
        boolean sharingClassResults = studentEntries != null;

        // This flag is only true when the results to show are the class results and there is only
        // one student.
        boolean oneStudent = false;
        if (studentEntries != null) {
            oneStudent = studentEntries.size() == 1;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

        String fileNamePart2;
        int testsResultsColor = 0;
        int testPassedBackgroundColor = 0;
        int testsResultsPageColor = 0;
        if (sharingClassResults) {
            fileNamePart2 = context.getString(R.string.share_class_results_simple_text);
        } else {
            fileNamePart2 = context.getString(R.string.share_student_results_simple_text);
            if (testTypeForStudentTestResults == TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(context)) {
                testsResultsColor = context.getResources().getColor(R.color.colorPrimaryDark);
                testsResultsPageColor = context.getResources().getColor(R.color.colorPrimaryLight);
                testPassedBackgroundColor = context.getResources().getColor(R.color.colorPrimaryUltraLight);
            } else {
                testsResultsColor = context.getResources().getColor(R.color.colorSecondaryDark);
                testsResultsPageColor = context.getResources().getColor(R.color.colorSecondaryLight);
                testPassedBackgroundColor = context.getResources().getColor(R.color.colorSecondaryUltraLight);
            }
        }

        String pdfFileName = fileNamePart1 + fileNamePart2 + PDF_FILE_EXTENSION;

        File pdfFile = new File(context.getExternalFilesDirs(
                Environment.DIRECTORY_DOCUMENTS)[0], pdfFileName);

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.Page page;
        Canvas canvas;
        Paint paint = new Paint();
        int xLeftPagePosition = 30;

        int spaceBetweenLines = 15;
        int spaceToFillWithBackground = 14;
        int spaceBetweenItems;

        if (sharingClassResults) {
            spaceBetweenItems = 20;
        } else {
            spaceBetweenItems = 10;
        }

        int spaceForTitle = 75;
        int titleYPosition = 45;
        int subtitleYPosition = 60;

        // Create first page and sets its title
        int pageNumber = 1;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH, LETTER_SIZE_PAGE_POSTSCRIPT_HEIGHT, pageNumber).create();
        page = pdfDocument.startPage(pageInfo);
        canvas = page.getCanvas();

        // Print the page number
        int pageTextColor;
        if (sharingClassResults) {
            pageTextColor = context.getResources().getColor(R.color.colorPrimaryLight);
        } else {
            pageTextColor = testsResultsPageColor;
        }
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setColor(pageTextColor);
        paint.setTypeface(Typeface.create("sans-serif", Typeface.ITALIC));
        canvas.drawText(context.getString(R.string.shared_file_page_number_text, pageNumber),
                LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition, titleYPosition, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        //If sharing class results show the test type. If sharing student test results show the student name.
        if (sharingClassResults) {
            paint.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
            paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD_ITALIC));
            canvas.drawText(String.valueOf(Html.fromHtml(
                    TestTypeUtils.getDefaultTestTypeTitleFromSharedPreferences(context, false))),
                    (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2, titleYPosition, paint);
        } else {
            paint.setColor(testsResultsColor);
            paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            canvas.drawText(String.valueOf(Html.fromHtml(studentNameForStudentResults)),
                    (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2, titleYPosition, paint);
        }

        if (sharingClassResults) {
            paint.setColor(context.getResources().getColor(R.color.colorPrimaryLight));
            paint.setTypeface(null);
            if (oneStudent) {
                canvas.drawText(context.getString(R.string.activity_main_sorted_by_message,
                        1, EMPTY_STRING, EMPTY_STRING),
                        (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2, subtitleYPosition, paint);
            } else {
                canvas.drawText(context.getString(R.string.activity_main_sorted_by_message,
                        studentEntries.size(), context.getString(R.string.lowercase_letter_s),
                        StudentsOrderUtils.getStudentsOrderString(context)),
                        (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2, subtitleYPosition, paint);
            }
        } else {
            paint.setColor(testsResultsColor);
            paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD_ITALIC));
            canvas.drawText(String.valueOf(Html.fromHtml(testTitleForStudentResults)),
                    (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2,
                    subtitleYPosition, paint);
        }


        int itemCounter = 0;
        int itemCounterOnPage = 0;
        int nameAndGradeColor;
        int testInfoColor;
        int studentPassedColor;
        int grade;
        int testType;
        int itemSeparator;
        String testDateString;
        String testDateFullString;
        String gradeString;

        if (sharingClassResults) {

            // For each student entry print a record on the PDF file
            for (StudentEntry studentEntry : studentEntries) {

                // If more than 19 on this page students create new page and set its title
                if ((itemCounter > MAX_NUMBER_OF_STUDENTS_ON_PDF_PAGE - 1) &&
                        (itemCounter % MAX_NUMBER_OF_STUDENTS_ON_PDF_PAGE == 0)) {
                    itemCounterOnPage = 0;
                    pageNumber++;
                    // Create a new page and print a title
                    pageInfo = new PdfDocument.PageInfo.Builder(
                            LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH, LETTER_SIZE_PAGE_POSTSCRIPT_HEIGHT, pageNumber).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();

                    // Print the page number
                    paint.setTextAlign(Paint.Align.RIGHT);
                    paint.setColor(context.getResources().getColor(R.color.colorPrimaryLight));
                    paint.setTypeface(Typeface.create("sans-serif", Typeface.ITALIC));
                    canvas.drawText(context.getString(R.string.shared_file_page_number_text, pageNumber),
                            LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition, titleYPosition, paint);

                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
                    paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD_ITALIC));
                    canvas.drawText(String.valueOf(Html.fromHtml(
                            TestTypeUtils.getDefaultTestTypeTitleFromSharedPreferences(context, false))),
                            (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2, titleYPosition, paint);

                    paint.setTypeface(null);
                    paint.setColor(context.getResources().getColor(R.color.colorPrimaryLight));
                    canvas.drawText(context.getString(R.string.activity_main_sorted_by_message,
                            studentEntries.size(), context.getString(R.string.lowercase_letter_s),
                            StudentsOrderUtils.getStudentsOrderString(context)),
                            (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2, subtitleYPosition, paint);
                }

                grade = studentEntry.getGrade();
                testType = studentEntry.getTestType();

                if (grade == STUDENT_WITH_NO_TESTS_GRADE) {
                    testDateString = context.getString(R.string.student_with_no_tests_text);
                    gradeString = EMPTY_STRING;

                } else {
                    testDateString = simpleDateFormat.format(studentEntry.getLastTestDate());
                    gradeString = String.valueOf(grade);
                }

                if (testType == TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(context)) {
                    nameAndGradeColor = context.getResources().getColor(R.color.colorPrimaryDark);
                    testInfoColor = context.getResources().getColor(R.color.colorPrimaryLight);
                    testDateFullString = testDateString;
                    studentPassedColor = context.getResources().getColor(R.color.colorPrimaryUltraLight);
                } else {
                    nameAndGradeColor = context.getResources().getColor(R.color.colorSecondaryDark);
                    testInfoColor = context.getResources().getColor(R.color.colorSecondaryLight);
                    testDateFullString = context.getString(R.string.test_type_and_last_date_for_no_default_test,
                            TestTypeUtils.getTestTypeString(context, testType),
                            WordUtils.getNumberOfWordsOnList(testType), testDateString);
                    studentPassedColor = context.getResources().getColor(R.color.colorSecondaryUltraLight);
                }

                itemSeparator = ((itemCounterOnPage) * spaceBetweenItems) + spaceForTitle;

                // If the students knows all words change the background of that student
                if (grade == WordUtils.getNumberOfWordsOnList(testType)) {
                    paint.setColor(studentPassedColor);
                    canvas.drawRect(
                            xLeftPagePosition - 5,
                            ((itemCounterOnPage + 1) * spaceBetweenLines) + itemSeparator - spaceToFillWithBackground - 0.6f,
                            LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition + 5,
                            ((itemCounterOnPage + 3) * spaceBetweenLines) + itemSeparator - (float) spaceBetweenItems / 2, paint);
                }

                // Prints the student complete name
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(nameAndGradeColor);
                paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
                canvas.drawText(String.valueOf(Html.fromHtml(context.getString(R.string.student_complete_name_with_html_format,
                        studentEntry.getFirstName(), studentEntry.getLastName()))), xLeftPagePosition,
                        ((itemCounterOnPage + 1) * spaceBetweenLines) + itemSeparator, paint);

                // Prints the student grade
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(gradeString, LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition,
                        ((itemCounterOnPage + 2) * spaceBetweenLines) + itemSeparator - (float) spaceBetweenLines / 2, paint);

                // Prints the test information
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(testInfoColor);
                paint.setTypeface(Typeface.create("sans-serif", Typeface.ITALIC));
                canvas.drawText(testDateFullString, xLeftPagePosition,
                        ((itemCounterOnPage + 2) * spaceBetweenLines) + itemSeparator, paint);

                // Print a line separator
                paint.setColor(Color.GRAY);
                paint.setStrokeWidth(0.8f);
                canvas.drawLine(
                        xLeftPagePosition - 5,
                        ((itemCounterOnPage + 3) * spaceBetweenLines) + itemSeparator - (float) spaceBetweenItems / 2,
                        LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition + 5,
                        ((itemCounterOnPage + 3) * spaceBetweenLines) + itemSeparator - (float) spaceBetweenItems / 2, paint);

                // If this is the last student on the page or the last student on the list of students
                // finish the PDF page
                if (itemCounter % MAX_NUMBER_OF_STUDENTS_ON_PDF_PAGE == (MAX_NUMBER_OF_STUDENTS_ON_PDF_PAGE - 1)
                        || itemCounter == studentEntries.size() - 1) {
                    pdfDocument.finishPage(page);
                }

                itemCounter++;
                itemCounterOnPage++;
            }
        } else {

            // For each test entry print a record on the PDF file
            for (TestEntry testEntry : testEntries) {

                // If more than 27 test results create new page and set its title
                if ((itemCounter > MAX_NUMBER_OF_TESTS_ON_PDF_PAGE - 1)
                        && (itemCounter % MAX_NUMBER_OF_TESTS_ON_PDF_PAGE == 0)) {
                    itemCounterOnPage = 0;
                    pageNumber++;
                    // Create a new page and print a title
                    pageInfo = new PdfDocument.PageInfo.Builder(
                            LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH, LETTER_SIZE_PAGE_POSTSCRIPT_HEIGHT, pageNumber).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();

                    // Print the page number
                    paint.setTextAlign(Paint.Align.RIGHT);
                    paint.setColor(testsResultsPageColor);
                    paint.setTypeface(Typeface.create("sans-serif", Typeface.ITALIC));
                    canvas.drawText(context.getString(R.string.shared_file_page_number_text, pageNumber),
                            LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition, titleYPosition, paint);

                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setColor(testsResultsColor);
                    paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
                    canvas.drawText(String.valueOf(Html.fromHtml(studentNameForStudentResults)),
                            (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2,
                            titleYPosition, paint);

                    paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD_ITALIC));
                    canvas.drawText(String.valueOf(Html.fromHtml(testTitleForStudentResults)),
                            (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2, subtitleYPosition, paint);
                }

                grade = testEntry.getGrade();
                testDateString = simpleDateFormat.format(testEntry.getDate());
                gradeString = String.valueOf(grade);

                itemSeparator = ((itemCounterOnPage) * spaceBetweenItems) + spaceForTitle;

                // If the student read all the words on this test change the background of it.
                if (grade == WordUtils.getNumberOfWordsOnList(testTypeForStudentTestResults)) {
                    paint.setColor(testPassedBackgroundColor);
                    canvas.drawRect(
                            xLeftPagePosition - 5,
                            ((itemCounterOnPage + 1) * spaceBetweenLines) + itemSeparator - spaceToFillWithBackground - 2.6f,
                            LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition + 5,
                            ((itemCounterOnPage + 2) * spaceBetweenLines) + itemSeparator - (float) spaceBetweenItems / 2 - 2, paint);
                }

                // Prints the date of the test
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(testsResultsColor);
                paint.setTypeface(null);
                canvas.drawText(testDateString, xLeftPagePosition,
                        ((itemCounterOnPage + 1) * spaceBetweenLines) + itemSeparator, paint);

                // Prints the grade of the test
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(gradeString, LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition,
                        ((itemCounterOnPage + 1) * spaceBetweenLines) + itemSeparator, paint);

                // Print a line separator
                paint.setColor(Color.GRAY);
                paint.setStrokeWidth(0.8f);
                canvas.drawLine(
                        xLeftPagePosition - 5,
                        ((itemCounterOnPage + 2) * spaceBetweenLines) + itemSeparator - (float) spaceBetweenItems / 2 - 2,
                        LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition + 5,
                        ((itemCounterOnPage + 2) * spaceBetweenLines) + itemSeparator - (float) spaceBetweenItems / 2 - 2, paint);

                // If this is the last test on the page or the last test on the list of tests
                // finish the PDF page
                if (itemCounter % MAX_NUMBER_OF_TESTS_ON_PDF_PAGE == (MAX_NUMBER_OF_TESTS_ON_PDF_PAGE - 1)
                        || itemCounter == testEntries.size() - 1) {
                    pdfDocument.finishPage(page);
                }

                itemCounter++;
                itemCounterOnPage++;
            }
        }

        try {
            pdfDocument.writeTo(new FileOutputStream(pdfFile));
        } catch (IOException e) {
            return null;
        }

        // close the document
        pdfDocument.close();

        // If the PDF file was created successfully
        if (pdfFile.exists()) {
            // Create and Intent to share the PDF file
            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            intentShareFile.setType("application/pdf");
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, pdfFileName);
            intentShareFile.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                    context, context.getApplicationContext().getPackageName() + ".provider", pdfFile));
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Check if there is an app that can share the data
            if (intentShareFile.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(Intent.createChooser(intentShareFile,
                        context.getString(R.string.menu_main_action_share_pdf_simple_chooser_title)));
                return new ShareResult(pdfFile, SHARE_RESULT_DATA_SHARED);
            } else {
                return new ShareResult(null, SHARE_RESULT_NO_APP);
            }
        } else {
            return new ShareResult(null, SHARE_RESULT_NO_FILE_CREATED);
        }

    }


    /**
     * Create a PDF file with the class or student test results information, saves it in the documents directory
     * * of the app private external storage and shares it. It DOES show the words not read correctly.
     *
     * @param context                       Context used to get access to resources.
     * @param studentEntries                List of a class student entries to be shared. Null if only one student tests will be shared.
     * @param testEntries                   List of one student test results. Null if a whole class test results will be shared.
     * @param fileNamePart1                 The first part of the PDF file name. For class results shared it is
     *                                      "Class", for student results shared it is "FirstNameLastName_TestTypeNumberOfWords"
     * @param studentNameForStudentResults  The complete name of the student to be displayed at the top of
     *                                      the PDF file when only one student test results are being shared.
     * @param testTitleForStudentResults    The test title to be displayed below the student name in the PDF
     *                                      file when only one student test results are being shared.
     * @param testTypeForStudentTestResults The test type of the student when only one student test results
     *                                      are being shared.
     * @return a PDF file that was shared, null if the file was not created or shared.
     */
    public static ShareResult shareDetailedPdfFile(Context context, @Nullable List<StudentEntry> studentEntries,
                                            List<TestEntry> testEntries, String fileNamePart1,
                                            String studentNameForStudentResults,
                                            String testTitleForStudentResults, int testTypeForStudentTestResults) {


        // Sets a flag that indicates if the file shared is "class results" or "student results"
        boolean sharingClassResults = studentEntries != null;

        // This flag is only true when the results to show are the class results and there is only
        // one student.
        boolean oneStudent = false;
        if (studentEntries != null) {
            oneStudent = studentEntries.size() == 1;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

        String fileNamePart2;
        int testsResultsColor = 0;
        int testsResultsUnknownWordsAndPageColor = 0;
        int testPassedBackgroundColor = 0;

        if (sharingClassResults) {
            fileNamePart2 = context.getString(R.string.share_class_results_detailed_text);
        } else {
            fileNamePart2 = context.getString(R.string.share_student_results_detailed_text);
            if (testTypeForStudentTestResults == TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(context)) {
                testsResultsColor = context.getResources().getColor(R.color.colorPrimaryDark);
                testsResultsUnknownWordsAndPageColor = context.getResources().getColor(R.color.colorPrimaryLight);
                testPassedBackgroundColor = context.getResources().getColor(R.color.colorPrimaryUltraLight);
            } else {
                testsResultsColor = context.getResources().getColor(R.color.colorSecondaryDark);
                testsResultsUnknownWordsAndPageColor = context.getResources().getColor(R.color.colorSecondaryLight);
                testPassedBackgroundColor = context.getResources().getColor(R.color.colorSecondaryUltraLight);
            }
        }

        String pdfFileName = fileNamePart1 + fileNamePart2 + PDF_FILE_EXTENSION;

        File pdfFile = new File(context.getExternalFilesDirs(
                Environment.DIRECTORY_DOCUMENTS)[0], pdfFileName);

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.Page page;
        Canvas canvas;
        StaticLayout staticLayout;
        Rect rect;
        Paint paint = new Paint();
        TextPaint textPaint = new TextPaint();
        int xLeftPagePosition = 30;
        int spaceBetweenLines = 15;
        int spaceToFillWithBackground = 14;
        int spaceBetweenItems;

        if (sharingClassResults) {
            spaceBetweenItems = 20;
        } else {
            spaceBetweenItems = 10;
        }

        int spaceForTitle = 75;

        int titleYPosition = 45;
        int subtitleYPosition = 60;

        int spaceForUnknownWords = 0;
        int spaceForUnknownWordsForThisItem;


        // Create first page and sets its title
        int pageNumber = 1;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH, LETTER_SIZE_PAGE_POSTSCRIPT_HEIGHT, pageNumber).create();
        page = pdfDocument.startPage(pageInfo);
        canvas = page.getCanvas();

        // Print the page number
        int pageTextColor;
        if (sharingClassResults) {
            pageTextColor = context.getResources().getColor(R.color.colorPrimaryLight);
        } else {
            pageTextColor = testsResultsUnknownWordsAndPageColor;
        }
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setColor(pageTextColor);
        paint.setTypeface(Typeface.create("sans-serif", Typeface.ITALIC));
        canvas.drawText(context.getString(R.string.shared_file_page_number_text, pageNumber),
                LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition, titleYPosition, paint);

        paint.setTextAlign(Paint.Align.CENTER);

        //If sharing class results show the test type. If sharing student test results show the student name.
        if (sharingClassResults) {
            paint.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
            paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD_ITALIC));
            canvas.drawText(String.valueOf(Html.fromHtml(
                    TestTypeUtils.getDefaultTestTypeTitleFromSharedPreferences(context, false))),
                    (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2, titleYPosition, paint);
        } else {
            paint.setColor(testsResultsColor);
            paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            canvas.drawText(String.valueOf(Html.fromHtml(studentNameForStudentResults)),
                    (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2, titleYPosition, paint);
        }

        // If sharing class results with more than one student show subtitle with the number of students
        // and order type. If sharing student test results always show the test type.

        if (sharingClassResults) {
            paint.setColor(context.getResources().getColor(R.color.colorPrimaryLight));
            paint.setTypeface(null);
            if (oneStudent) {
                canvas.drawText(context.getString(R.string.activity_main_sorted_by_message,
                        1, EMPTY_STRING, EMPTY_STRING),
                        (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2, subtitleYPosition, paint);
            } else {
                canvas.drawText(context.getString(R.string.activity_main_sorted_by_message,
                        studentEntries.size(), context.getString(R.string.lowercase_letter_s),
                        StudentsOrderUtils.getStudentsOrderString(context)),
                        (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2, subtitleYPosition, paint);
            }
        } else {
            paint.setColor(testsResultsColor);
            paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD_ITALIC));
            canvas.drawText(String.valueOf(Html.fromHtml(testTitleForStudentResults)),
                    (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2,
                    subtitleYPosition, paint);
        }


        int itemCounter = 0;
        int itemsOnPageCounter = 0;
        int nameAndGradeColor;
        int testInfoColor;
        int studentPassedBackgroundColor;
        int grade;
        int testType;
        int staticLayoutLineHeight;
        int numberOfWordsInTest;
        int itemLineSeparatorYPosition;
        int totalSpace;
        String testDateString;
        String testDateFullString;
        String gradeString;

        // For each student entry print a record on the PDF file
        if (sharingClassResults) {

            // For each student entry print a record on the PDF file
            for (StudentEntry studentEntry : studentEntries) {

                // Gets student information and set information to be displayed
                grade = studentEntry.getGrade();
                testType = studentEntry.getTestType();
                String unknownWords = studentEntry.getUnknownWords();
                numberOfWordsInTest = WordUtils.getNumberOfWordsOnList(testType);

                if (grade == STUDENT_WITH_NO_TESTS_GRADE) {
                    testDateString = context.getString(R.string.student_with_no_tests_text);
                    gradeString = EMPTY_STRING;

                } else {
                    testDateString = simpleDateFormat.format(studentEntry.getLastTestDate());
                    gradeString = String.valueOf(grade);
                }

                if (testType == TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(context)) {
                    nameAndGradeColor = context.getResources().getColor(R.color.colorPrimaryDark);
                    testInfoColor = context.getResources().getColor(R.color.colorPrimaryLight);
                    testDateFullString = testDateString;
                    studentPassedBackgroundColor = context.getResources().getColor(R.color.colorPrimaryUltraLight);
                } else {
                    nameAndGradeColor = context.getResources().getColor(R.color.colorSecondaryDark);
                    testInfoColor = context.getResources().getColor(R.color.colorSecondaryLight);
                    testDateFullString = context.getString(R.string.test_type_and_last_date_for_no_default_test,
                            TestTypeUtils.getTestTypeString(context, testType),
                            WordUtils.getNumberOfWordsOnList(testType), testDateString);
                    studentPassedBackgroundColor = context.getResources().getColor(R.color.colorSecondaryUltraLight);
                }


                totalSpace = ((itemsOnPageCounter) * spaceBetweenItems) + spaceForTitle + spaceForUnknownWords;

                spaceForUnknownWordsForThisItem = 0;
                staticLayout = null;
                staticLayoutLineHeight = 0;

                // If there are unknown words for the student calculates the space to display them
                if (!((grade == numberOfWordsInTest) || (grade == STUDENT_WITH_NO_TESTS_GRADE))) {

                    textPaint.setColor(testInfoColor);
                    textPaint.setTypeface(Typeface.create("sans-serif", Typeface.ITALIC));

                    staticLayout = new StaticLayout(unknownWords, textPaint, LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - 60,
                            Layout.Alignment.ALIGN_NORMAL, 1, 1, true);
                    rect = new Rect();
                    textPaint.getTextBounds(unknownWords, 0, unknownWords.length(), rect);
                    staticLayoutLineHeight = rect.height() + 1;
                    spaceForUnknownWordsForThisItem = staticLayoutLineHeight * (staticLayout.getLineCount()) + (spaceBetweenLines / 3);
                    spaceForUnknownWords += spaceForUnknownWordsForThisItem;
                }

                itemLineSeparatorYPosition = ((itemsOnPageCounter + 3) * spaceBetweenLines) + totalSpace - (spaceBetweenItems / 2) +
                        spaceForUnknownWordsForThisItem;

                // If printing this student will go outside of the page bottom margin create a new page
                if (itemLineSeparatorYPosition > LETTER_SIZE_PAGE_POSTSCRIPT_HEIGHT - 30) {

                    // Finish the previous page
                    pdfDocument.finishPage(page);

                    itemsOnPageCounter = 0;
                    spaceForUnknownWords = spaceForUnknownWordsForThisItem;
                    totalSpace = spaceForTitle;
                    itemLineSeparatorYPosition = ((itemsOnPageCounter + 3) * spaceBetweenLines) + totalSpace - (spaceBetweenItems / 2) +
                            spaceForUnknownWordsForThisItem;
                    pageNumber++;
                    // Create a new page and print a title
                    pageInfo = new PdfDocument.PageInfo.Builder(
                            LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH, LETTER_SIZE_PAGE_POSTSCRIPT_HEIGHT, pageNumber).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();

                    // Print the page number
                    paint.setTextAlign(Paint.Align.RIGHT);
                    paint.setColor(context.getResources().getColor(R.color.colorPrimaryLight));
                    paint.setTypeface(Typeface.create("sans-serif", Typeface.ITALIC));
                    canvas.drawText(context.getString(R.string.shared_file_page_number_text, pageNumber),
                            LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition, titleYPosition, paint);

                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
                    paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD_ITALIC));
                    canvas.drawText(String.valueOf(Html.fromHtml(
                            TestTypeUtils.getDefaultTestTypeTitleFromSharedPreferences(context, false))),
                            (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2, titleYPosition, paint);

                    paint.setTypeface(null);
                    paint.setColor(context.getResources().getColor(R.color.colorPrimaryLight));
                    canvas.drawText(context.getString(R.string.activity_main_sorted_by_message,
                            studentEntries.size(), context.getString(R.string.lowercase_letter_s),
                            StudentsOrderUtils.getStudentsOrderString(context)),
                            (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2, subtitleYPosition, paint);
                }

                // If the students knows all words change the background of that student
                if (grade == WordUtils.getNumberOfWordsOnList(testType)) {
                    paint.setColor(studentPassedBackgroundColor);
                    canvas.drawRect(
                            xLeftPagePosition - 5,
                            ((itemsOnPageCounter + 1) * spaceBetweenLines) + totalSpace - spaceToFillWithBackground - 0.6f,
                            LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition + 5,
                            itemLineSeparatorYPosition, paint);
                }

                // Prints the student complete name
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(nameAndGradeColor);
                paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
                canvas.drawText(context.getString(R.string.student_complete_name, studentEntry.getFirstName(),
                        studentEntry.getLastName()), xLeftPagePosition,
                        ((itemsOnPageCounter + 1) * spaceBetweenLines) + totalSpace, paint);

                // Prints the student grade
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(gradeString, LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition,
                        ((itemsOnPageCounter + 1) * spaceBetweenLines) + totalSpace, paint);

                // Prints the test information
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setTypeface(Typeface.create("sans-serif", Typeface.ITALIC));
                paint.setColor(testInfoColor);
                canvas.drawText(testDateFullString, xLeftPagePosition,
                        ((itemsOnPageCounter + 2) * spaceBetweenLines) + totalSpace, paint);

                // If there are unknown words print them
                if (!((grade == numberOfWordsInTest) || (grade == STUDENT_WITH_NO_TESTS_GRADE))) {
                    canvas.save();
                    canvas.translate((float) xLeftPagePosition,
                            (float) (((itemsOnPageCounter + 3) * spaceBetweenLines) + totalSpace - staticLayoutLineHeight));
                    staticLayout.draw(canvas);
                    canvas.restore();
                }

                // Print a line separator
                paint.setColor(Color.GRAY);
                paint.setStrokeWidth(0.8f);
                canvas.drawLine(
                        xLeftPagePosition - 5, itemLineSeparatorYPosition,
                        LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition + 5, itemLineSeparatorYPosition, paint);


                // If this is the last student to print finish the page
                if (itemCounter == studentEntries.size() - 1) {
                    pdfDocument.finishPage(page);
                }

                itemCounter++;
                itemsOnPageCounter++;
            }
        } else {

            // For each test entry print a record on the PDF file
            for (TestEntry testEntry : testEntries) {

                // Gets test information and set information to be displayed
                grade = testEntry.getGrade();
                String unknownWords = testEntry.getUnknownWords();
                numberOfWordsInTest = WordUtils.getNumberOfWordsOnList(testTypeForStudentTestResults);

                testDateString = simpleDateFormat.format(testEntry.getDate());
                gradeString = String.valueOf(grade);

                totalSpace = ((itemsOnPageCounter) * spaceBetweenItems) + spaceForTitle + spaceForUnknownWords;

                spaceForUnknownWordsForThisItem = 0;
                staticLayout = null;
                staticLayoutLineHeight = 0;

                // If there are unknown words for this test calculates the space to display them
                if (!(grade == numberOfWordsInTest)) {

                    textPaint.setColor(testsResultsUnknownWordsAndPageColor);
                    textPaint.setTypeface(Typeface.create("sans-serif", Typeface.ITALIC));

                    staticLayout = new StaticLayout(unknownWords, textPaint, LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - 60,
                            Layout.Alignment.ALIGN_NORMAL, 1, 1, true);
                    rect = new Rect();
                    textPaint.getTextBounds(unknownWords, 0, unknownWords.length(), rect);
                    staticLayoutLineHeight = rect.height() + 1;
                    spaceForUnknownWordsForThisItem = staticLayoutLineHeight * (staticLayout.getLineCount()) + (spaceBetweenLines / 3);
                    spaceForUnknownWords += spaceForUnknownWordsForThisItem;
                }

                itemLineSeparatorYPosition = ((itemsOnPageCounter + 2) * spaceBetweenLines) + totalSpace - (spaceBetweenItems / 2) +
                        spaceForUnknownWordsForThisItem;

                // If printing this test will go outside of the page bottom margin create a new page
                if (itemLineSeparatorYPosition > LETTER_SIZE_PAGE_POSTSCRIPT_HEIGHT - 30) {

                    // Finish the previous page
                    pdfDocument.finishPage(page);

                    itemsOnPageCounter = 0;
                    spaceForUnknownWords = spaceForUnknownWordsForThisItem;
                    totalSpace = spaceForTitle;
                    itemLineSeparatorYPosition = ((itemsOnPageCounter + 2) * spaceBetweenLines) + totalSpace - (spaceBetweenItems / 2) +
                            spaceForUnknownWordsForThisItem;
                    pageNumber++;

                    // Create a new page
                    pageInfo = new PdfDocument.PageInfo.Builder(
                            LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH, LETTER_SIZE_PAGE_POSTSCRIPT_HEIGHT, pageNumber).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();

                    // Print the page number
                    paint.setTextAlign(Paint.Align.RIGHT);
                    paint.setColor(testsResultsUnknownWordsAndPageColor);
                    paint.setTypeface(Typeface.create("sans-serif", Typeface.ITALIC));
                    canvas.drawText(context.getString(R.string.shared_file_page_number_text, pageNumber),
                            LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition, titleYPosition, paint);

                    // Print a title
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setColor(testsResultsColor);
                    paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
                    canvas.drawText(String.valueOf(Html.fromHtml(studentNameForStudentResults)),
                            (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2, titleYPosition, paint);

                    // Print a subtitle
                    paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD_ITALIC));
                    canvas.drawText(String.valueOf(Html.fromHtml(testTitleForStudentResults)),
                            (float) LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH / 2, subtitleYPosition, paint);
                }

                // If the students knows all words on that particular test change the background of that test
                if (grade == WordUtils.getNumberOfWordsOnList(testTypeForStudentTestResults)) {
                    paint.setColor(testPassedBackgroundColor);
                    canvas.drawRect(
                            xLeftPagePosition - 5,
                            ((itemsOnPageCounter + 1) * spaceBetweenLines) + totalSpace - spaceToFillWithBackground - 2.6f,
                            LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition + 5,
                            itemLineSeparatorYPosition - 2, paint);
                }

                // Prints the date of the test
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(testsResultsColor);
                paint.setTypeface(null);
                canvas.drawText(testDateString, xLeftPagePosition,
                        ((itemsOnPageCounter + 1) * spaceBetweenLines) + totalSpace, paint);

                // Prints the grade of the test
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(gradeString, LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition,
                        ((itemsOnPageCounter + 1) * spaceBetweenLines) + totalSpace, paint);

                // If there are unknown words print them
                if (!(grade == numberOfWordsInTest)) {
                    canvas.save();
                    canvas.translate((float) xLeftPagePosition,
                            (float) (((itemsOnPageCounter + 2) * spaceBetweenLines) + totalSpace - staticLayoutLineHeight));
                    staticLayout.draw(canvas);
                    canvas.restore();
                }

                // Print a line separator
                paint.setColor(Color.GRAY);
                paint.setStrokeWidth(0.8f);
                canvas.drawLine(
                        xLeftPagePosition - 5, itemLineSeparatorYPosition - 2,
                        LETTER_SIZE_PAGE_POSTSCRIPT_WIDTH - xLeftPagePosition + 5, itemLineSeparatorYPosition - 2, paint);


                // If this is the last student to print finish the page
                if (itemCounter == testEntries.size() - 1) {
                    pdfDocument.finishPage(page);
                }

                itemCounter++;
                itemsOnPageCounter++;
            }
        }

        try {
            pdfDocument.writeTo(new FileOutputStream(pdfFile));
        } catch (IOException e) {
            return null;
        }

        // close the document
        pdfDocument.close();

        // If the PDF file was created successfully
        if (pdfFile.exists()) {
            // Create and Intent to share the PDF file
            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            intentShareFile.setType("application/pdf");
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, pdfFileName);
            intentShareFile.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                    context, context.getApplicationContext().getPackageName() + ".provider", pdfFile));
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Check if there is an app that can share the data
            if (intentShareFile.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(Intent.createChooser(intentShareFile,
                        context.getString(R.string.menu_main_action_share_pdf_detailed_chooser_title)));
                return new ShareResult(pdfFile, SHARE_RESULT_DATA_SHARED);
            } else {
                return new ShareResult(null, SHARE_RESULT_NO_APP);
            }
        } else {
            return new ShareResult(null, SHARE_RESULT_NO_FILE_CREATED);
        }

    }


    public static void deleteFilesFromExternalStoragePrivateDocumentsDirectory(Context context) {
        File directory = new File(context.getExternalFilesDirs(
                Environment.DIRECTORY_DOCUMENTS)[0], EMPTY_STRING);
        if (directory.isDirectory()) {
            String[] children = directory.list();
            if (children != null) {
                for (String aChildren : children) {
                    if (aChildren.contains(context.getString(R.string.share_class_results_simple_text)) ||
                            aChildren.contains(context.getString(R.string.share_class_results_detailed_text)) ||
                            aChildren.contains(context.getString(R.string.share_student_results_simple_text)) ||
                            aChildren.contains(context.getString(R.string.share_student_results_detailed_text))) {
                        boolean deleted = new File(directory, aChildren).delete();
                        Log.d(LOG_TAG, "File deletion result: " + deleted);
                    }
                }
            }
        }
    }

}
