package org.ghotkar.helper;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cghotkar on 1/31/17.
 * This class will handle all database CRUD(Create, Read, Update and Delete) operations.
 *
 * _id | Title | Description
 *
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;

    // Database name
    public static final String DATABASE_NAME = "TodoList.db";

    // Contacts Table Columns names
    public static final String ID = "_id";
    public static final String TABLE_NAME = "list";
    public static final String COLUMN_NAME_TITLE = "title";
    public  static final String COLUMN_NAME_DESC = "description";

    // SQL string command for creating a table
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_TITLE + " TEXT," +
                    COLUMN_NAME_DESC + " TEXT)";

    // SQL string command for deleting a table
    private static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // Adds a todo list item into the the database
    public void addItem(TodoListItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_TITLE, item.getTodo_title()); // add the title of the todo item
        values.put(COLUMN_NAME_DESC, item.getTodo_description()); // add the description of the todo item

        db.insert(TABLE_NAME, null,values);
        db.close();
    }

    // This looks for a single row in the table
    public TodoListItem getSingleItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] { ID,
                        COLUMN_NAME_TITLE, COLUMN_NAME_DESC }, ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        TodoListItem item = new TodoListItem(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return contact
        return item;
    }

    // get all rows that match the query
    public List<TodoListItem> getAllContacts() {
        List<TodoListItem> itemList = new ArrayList<TodoListItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TodoListItem item = new TodoListItem();
                item.set_id(Integer.parseInt(cursor.getString(0)));
                item.setTodo_title(cursor.getString(1));
                item.setTodo_description(cursor.getString(2));
                // Adding contact to list
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        // return contact list
        return itemList;
    }

    // get total count of entries in table
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // update the rows data
    public int updateContact(TodoListItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_TITLE, item.getTodo_title());
        values.put(COLUMN_NAME_DESC, item.getTodo_description());

        // updating row
        return db.update(TABLE_NAME, values, ID + " = ?",
                new String[] { String.valueOf(item.get_id()) });
    }

    // Deleting single item
    public void deleteContact(TodoListItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ID + " = ?",
                new String[] { String.valueOf(item.get_id()) });
        db.close();
    }
}
