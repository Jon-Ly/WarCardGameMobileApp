package jonly.warcardgame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by wintow on 4/14/2018.
 */

public final class GameContract {

    public GameContract(){}

    public static abstract class DBEntry implements BaseColumns{
        public static final String TABLE_NAME = "registeredUser";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
    }

    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = " ,";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE" + DBEntry.TABLE_NAME + " (" +
                    DBEntry._ID + " INTEGER PRIMARY KEY," + COMMA_SEP +
                    DBEntry.COLUMN_USERNAME + TEXT_TYPE + COMMA_SEP +
                    DBEntry.COLUMN_PASSWORD + TEXT_TYPE + COMMA_SEP + ")";
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBEntry.TABLE_NAME;

}