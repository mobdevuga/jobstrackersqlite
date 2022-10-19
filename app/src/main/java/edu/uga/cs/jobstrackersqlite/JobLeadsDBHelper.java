package edu.uga.cs.jobstrackersqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * This is a SQLiteOpenHelper class, which Android uses to create, upgrade, delete an SQLite database
 * in an app.
 *
 * This class is a singleton, following the Singleton Design Pattern.
 * Only one instance of this class will exist.  To make sure, the
 * only constructor is private.
 * Access to the only instance is via the getInstance method.
 */
public class JobLeadsDBHelper extends SQLiteOpenHelper {

    private static final String DEBUG_TAG = "JobLeadsDBHelper";

    private static final String DB_NAME = "jobleads.db";
    private static final int DB_VERSION = 1;

    // Define all names (strings) for table and column names.
    // This will be useful if we want to change these names later.
    public static final String TABLE_JOBLEADS = "jobleads";
    public static final String JOBLEADS_COLUMN_ID = "_id";
    public static final String JOBLEADS_COLUMN_NAME = "name";
    public static final String JOBLEADS_COLUMN_PHONE = "phone";
    public static final String JOBLEADS_COLUMN_URL = "url";
    public static final String JOBLEADS_COLUMN_COMMENTS = "comments";

    // This is a reference to the only instance for the helper.
    private static JobLeadsDBHelper helperInstance;

    // A Create table SQL statement to create a table for job leads.
    // Note that _id is an auto increment primary key, i.e. the database will
    // automatically generate unique id values as keys.
    private static final String CREATE_JOBLEADS =
            "create table " + TABLE_JOBLEADS + " ("
                + JOBLEADS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + JOBLEADS_COLUMN_NAME + " TEXT, "
                + JOBLEADS_COLUMN_PHONE + " TEXT, "
                + JOBLEADS_COLUMN_URL + " TEXT, "
                + JOBLEADS_COLUMN_COMMENTS + " TEXT"
            + ")";

    // Note that the constructor is private!
    // So, it can be called only from
    // this class, in the getInstance method.
    private JobLeadsDBHelper( Context context ) {
        super( context, DB_NAME, null, DB_VERSION );
    }

    // Access method to the single instance of the class.
    // It is synchronized, so that only one thread can executes this method, at a time.
    public static synchronized JobLeadsDBHelper getInstance( Context context ) {
        // check if the instance already exists and if not, create the instance
        if( helperInstance == null ) {
            helperInstance = new JobLeadsDBHelper( context.getApplicationContext() );
        }
        return helperInstance;
    }

    // We must override onCreate method, which will be used to create the database if
    // it does not exist yet.
    @Override
    public void onCreate( SQLiteDatabase db ) {
        db.execSQL( CREATE_JOBLEADS );
        Log.d( DEBUG_TAG, "Table " + TABLE_JOBLEADS + " created" );
    }

    // We should override onUpgrade method, which will be used to upgrade the database if
    // its version (DB_VERSION) has changed.  This will be done automatically by Android
    // if the version will be bumped up, as we modify the database schema.
    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        db.execSQL( "drop table if exists " + TABLE_JOBLEADS );
        onCreate( db );
        Log.d( DEBUG_TAG, "Table " + TABLE_JOBLEADS + " upgraded" );
    }
}
