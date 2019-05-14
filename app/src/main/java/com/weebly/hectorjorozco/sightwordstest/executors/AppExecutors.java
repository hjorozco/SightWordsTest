package com.weebly.hectorjorozco.sightwordstest.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Global executor pools for the whole application.
 * <p>
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */

public class AppExecutors {

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static AppExecutors sExecutorsInstance;
    private final ExecutorService diskIO;

    private AppExecutors(ExecutorService diskIO) {
        this.diskIO = diskIO;
    }

    public static AppExecutors getInstance() {
        if (sExecutorsInstance == null) {
            synchronized (LOCK) {
                sExecutorsInstance = new AppExecutors(Executors.newSingleThreadExecutor());
            }
        }
        return sExecutorsInstance;
    }

    public ExecutorService diskIO() {
        return diskIO;
    }

}
