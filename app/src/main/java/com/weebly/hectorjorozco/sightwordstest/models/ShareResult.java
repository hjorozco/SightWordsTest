package com.weebly.hectorjorozco.sightwordstest.models;

import java.io.File;

public class ShareResult {

    private final File mFile;
    private final byte mCode;

    public ShareResult(File file, byte code) {
        mFile = file;
        mCode = code;
    }

    public File getFile() {
        return mFile;
    }

    public byte getCode() {
        return mCode;
    }
}
