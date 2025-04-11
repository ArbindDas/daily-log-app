package com.JSR.DailyLog.Exception;

public class JournalEntriesNotFoundException extends RuntimeException{
    public JournalEntriesNotFoundException ( String message ) {
        super ( message );
    }
}
