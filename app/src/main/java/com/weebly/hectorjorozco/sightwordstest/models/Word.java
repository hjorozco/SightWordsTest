package com.weebly.hectorjorozco.sightwordstest.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a word in one of the tests
 */

public class Word implements Parcelable {

    // The word that the student will read
    private final String word;
    // The state of the word in the Grid presented to the user
    private boolean pressed;

    // Constructor used by TestActivity
    public Word(String word, boolean pressed) {
        this.word = word;
        this.pressed = pressed;
    }

    // Getter and setter methods
    public String getWord() {
        return word;
    }

    public boolean getPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.word);
        dest.writeByte(this.pressed ? (byte) 1 : (byte) 0);
    }

    private Word(Parcel in) {
        this.word = in.readString();
        this.pressed = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel source) {
            return new Word(source);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };
}
