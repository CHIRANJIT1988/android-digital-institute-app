package app.institute.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.institute.model.Branch;
import app.institute.model.Class;
import app.institute.model.Message;
import app.institute.model.Subject;

/**
 * Created by CHIRANJIT on 7/24/2016.
 */
public class SQLiteDatabaseHelper extends SQLiteOpenHelper
{

    private Context context;
    // Database version
    private static final int DATABASE_VERSION = 2;
    // Database name
    private static final String DATABASE_NAME = "institute.db";


    public static final String TABLE_BRANCH = "branch";
    public static final String TABLE_INBOX = "inbox";

    private static final String KEY_ID = "id";

    private static final String KEY_BRANCH_CODE = "branch_code";
    private static final String KEY_BRANCH_NAME = "branch_name";
    private static final String KEY_SUBJECT_CODE = "subject_code";
    private static final String KEY_SUBJECT_NAME = "subject_name";
    private static final String KEY_CLASS_CODE = "class_code";
    private static final String KEY_CLASS_NAME = "class_name";

    private static final String KEY_MESSAGE_ID = "message_id";
    private static final String KEY_READ_STATUS = "read_status";
    private static final String KEY_MESSAGE_TITLE = "message_title";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_MESSAGE = "message";


    private static final String CREATE_TABLE_BRANCH = "CREATE TABLE "
            + TABLE_BRANCH + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_BRANCH_CODE + " INTEGER," + KEY_BRANCH_NAME + " TEXT,"
            + KEY_SUBJECT_CODE + " INTEGER," + KEY_SUBJECT_NAME + " TEXT," + KEY_CLASS_CODE + " INTEGER," + KEY_CLASS_NAME + " TEXT)";

    // Complain table create statements
    private static final String CREATE_TABLE_INBOX = "CREATE TABLE "
            + TABLE_INBOX + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_MESSAGE_ID + " TEXT," + KEY_MESSAGE_TITLE + " TEXT,"
            + KEY_MESSAGE + " TEXT, " + KEY_TIMESTAMP + " TEXT," + KEY_READ_STATUS + " INTEGER DEFAULT 0)" ;


    public SQLiteDatabaseHelper(Context context)
    {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase database)
    {

        database.execSQL(CREATE_TABLE_BRANCH);
        database.execSQL(CREATE_TABLE_INBOX);
        Log.v("CREATE TABLE: ", "Inside onCreate()");
    }


    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version)
    {

        database.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANCH);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_INBOX);
        onCreate(database);
        Log.v("UPGRADE TABLE: ", "Inside onUpgrade()");
    }


    public boolean insertMessage(Message message)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_MESSAGE_ID, message.message_id);
        values.put(KEY_MESSAGE_TITLE, message.message_title);
        values.put(KEY_MESSAGE, message.message_body);
        values.put(KEY_TIMESTAMP, message.timestamp);

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_INBOX, null, values) > 0;

        Log.v("createSuccessful ", String.valueOf(createSuccessful));

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public boolean insertBranch(Branch branch)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_BRANCH_CODE, branch.branch_code);
        values.put(KEY_BRANCH_NAME, branch.branch_name);
        values.put(KEY_SUBJECT_CODE, branch._subject.subject_code);
        values.put(KEY_SUBJECT_NAME, branch._subject.subject_name);
        values.put(KEY_CLASS_CODE, branch._class.class_code);
        values.put(KEY_CLASS_NAME, branch._class.class_name);

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_BRANCH, null, values) > 0;

        Log.v("createSuccessful", " " + createSuccessful );

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public void getAllBranch()
    {

        String selectQuery = "SELECT DISTINCT " + KEY_BRANCH_CODE + "," + KEY_BRANCH_NAME + " FROM " + TABLE_BRANCH
                + " ORDER BY " + KEY_BRANCH_NAME + " ASC";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {

            Branch.list.clear();

            do
            {

                Branch branch = new Branch();

                branch.branch_code = cursor.getString(0);
                branch.branch_name = cursor.getString(1);

                Branch.list.add(branch);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();
    }


    public List<Class> getAllClass(String branch_code, String subject_code)
    {

        List<Class> list = new ArrayList<>();

        String selectQuery = "SELECT DISTINCT " + KEY_CLASS_CODE + "," + KEY_CLASS_NAME + " FROM " + TABLE_BRANCH
                + " WHERE " + KEY_BRANCH_CODE + "='" + branch_code + "' AND " + KEY_SUBJECT_CODE + "='" + subject_code
                + "' ORDER BY " + KEY_SUBJECT_NAME + " DESC";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                Class _class = new Class();

                _class.class_code = cursor.getString(0);
                _class.class_name = cursor.getString(1);

                list.add(_class);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return list;
    }


    public List<Subject> getAllSubject(String branch_code)
    {

        List<Subject> list = new ArrayList<>();

        String selectQuery = "SELECT DISTINCT " + KEY_SUBJECT_CODE + "," + KEY_SUBJECT_NAME + " FROM " + TABLE_BRANCH
                + " WHERE " + KEY_BRANCH_CODE + "='" + branch_code + "'" + " ORDER BY " + KEY_SUBJECT_NAME + " ASC";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                Subject subject = new Subject();

                subject.subject_code = cursor.getString(0);
                subject.subject_name = cursor.getString(1);

                list.add(subject);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return list;
    }


    public ArrayList<Message> getAllMessage()
    {

        ArrayList<Message> messagesList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_INBOX + " ORDER BY " + KEY_ID + " DESC";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                Message message = new Message();

                message.message_id = cursor.getString(0);
                message.message_title = cursor.getString(2);
                message.message_body = cursor.getString(3);
                message.timestamp = cursor.getString(4);
                message.read_status = cursor.getInt(5);

                messagesList.add(message);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return messagesList;
    }


    public int dbRowCount(String TABLE_NAME)
    {

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }


    public int setAsRead()
    {

        String selectQuery = "UPDATE " + TABLE_INBOX + " SET " + KEY_READ_STATUS + "='1' WHERE " + KEY_READ_STATUS + "='0'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }


    public void deleteMessage(String message_id)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_INBOX + " WHERE " + KEY_MESSAGE_ID + " ='" + message_id + "'";
        Log.d("query", deleteQuery);
        database.execSQL("PRAGMA foreign_keys=ON");
        database.execSQL(deleteQuery);
        database.close();
    }


    public void deleteAllRow(String TABLE_NAME)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_NAME;
        Log.d("query", deleteQuery);
        database.execSQL("PRAGMA foreign_keys=ON");
        database.execSQL(deleteQuery);
        database.close();
    }

    public int unreadMessageCount()
    {

        String selectQuery = "SELECT * FROM " + TABLE_INBOX + " WHERE " + KEY_READ_STATUS + "='0'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }
}