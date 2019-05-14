package com.weebly.hectorjorozco.sightwordstest.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;
import java.util.Date;

/**
 * Represents an entry in the students table. Used by Room Persistence Library.
 */
@Entity(
        tableName = "students",
        indices = {@Index(value = {"first_name", "last_name"}, unique = true)}
        )
public class StudentEntry implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "first_name")
    private String firstName;
    @ColumnInfo(name = "last_name")
    private String lastName;
    private int grade;
    @ColumnInfo(name = "test_type")
    private int testType;
    @ColumnInfo(name = "last_test_date")
    private Date lastTestDate;
    @ColumnInfo(name = "unknown_words")
    private String unknownWords;

    // Constructor used by AddStudentFragment
    @Ignore
    public StudentEntry(String firstName, String lastName, int grade, int testType, Date lastTestDate, String unknownWords) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.grade = grade;
        this.testType = testType;
        this.lastTestDate = lastTestDate;
        this.unknownWords = unknownWords;
    }

    // Constructor used by Room library
    StudentEntry(int id, String firstName, String lastName, int grade, int testType, Date lastTestDate, String unknownWords) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.grade = grade;
        this.testType = testType;
        this.lastTestDate = lastTestDate;
        this.unknownWords = unknownWords;
    }

    // Getter and setter methods
    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getGrade() {
        return grade;
    }

    public int getTestType() {return testType;}

    public Date getLastTestDate() {
        return lastTestDate;
    }

    public String getUnknownWords() {
        return unknownWords;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {this.firstName = firstName;}

    public void setLastName(String lastName) {this.lastName = lastName;}

    public void setGrade(int grade) {this.grade = grade;}

    public void setTestType(int testType) {this.testType = testType;}

    public void setLastTestDate (Date lastTestDate) {this.lastTestDate = lastTestDate;}

    public void setUnknownWords(String unknownWords) {this.unknownWords = unknownWords;}

    public static final Comparator<StudentEntry> FirstNameComparator = new Comparator<StudentEntry>() {
        @Override
        public int compare(StudentEntry studentEntry1, StudentEntry studentEntry2) {
            String FirstName1 = studentEntry1.getFirstName().toUpperCase();
            String FirstName2 = studentEntry2.getFirstName().toUpperCase();

            return FirstName1.compareTo(FirstName2);
        }
    };

    public static final Comparator<StudentEntry> LastNameComparator = new Comparator<StudentEntry>() {
        @Override
        public int compare(StudentEntry studentEntry1, StudentEntry studentEntry2) {
            String LastName1 = studentEntry1.getLastName().toUpperCase();
            String LastName2 = studentEntry2.getLastName().toUpperCase();

            return LastName1.compareTo(LastName2);
        }
    };

    public static final Comparator<StudentEntry> TestResultComparator = new Comparator<StudentEntry>() {
        @Override
        public int compare(StudentEntry studentEntry1, StudentEntry studentEntry2) {
            int testResult1 = studentEntry1.getGrade();
            int testResult2 = studentEntry2.getGrade();

            return testResult1 - testResult2;
        }
    };

    public static final Comparator<StudentEntry> TestTypeComparator = new Comparator<StudentEntry>() {
        @Override
        public int compare(StudentEntry studentEntry1, StudentEntry studentEntry2) {
            int testType1 = studentEntry1.getTestType();
            int testType2 = studentEntry2.getTestType();

            return testType1 - testType2;
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeInt(this.grade);
        dest.writeInt(this.testType);
        dest.writeLong(this.lastTestDate != null ? this.lastTestDate.getTime() : -1);
        dest.writeString(this.unknownWords);
    }

    private StudentEntry(Parcel in) {
        this.id = in.readInt();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.grade = in.readInt();
        this.testType = in.readInt();
        long tmpLastTestDate = in.readLong();
        this.lastTestDate = tmpLastTestDate == -1 ? null : new Date(tmpLastTestDate);
        this.unknownWords = in.readString();
    }

    public static final Parcelable.Creator<StudentEntry> CREATOR = new Parcelable.Creator<StudentEntry>() {
        @Override
        public StudentEntry createFromParcel(Parcel source) {
            return new StudentEntry(source);
        }

        @Override
        public StudentEntry[] newArray(int size) {
            return new StudentEntry[size];
        }
    };
}
