package com.device.onsuritycontact.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.device.onsuritycontact.model.ContactsModel;

import java.util.ArrayList;
import java.util.List;

/** created by Joseph */
public class ContactsDB extends SQLiteOpenHelper{

    private static final String DATABASE_TABLE = "contacts";
    /** Database name */
    private static String DBNAME = "contactssqlite";

    /** Version number of the database */
    private static int VERSION = 2;

    public static final String FIELD_ROW_ID = "_id";
    public static final String FIELD_FIRST_NAME = "firstName";
    public static final String FIELD_SECOND_NAME = "secondName";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_USER_PHOTO_URL = "photourl";
    public static final String FIELD_PHONE = "phone";
 
    /** An instance variable for SQLiteDatabase */
    private SQLiteDatabase mDB;
 
    /** Constructor */
    public ContactsDB(Context context) {
        super(context, DBNAME, null, VERSION);
        this.mDB = getWritableDatabase();
    }
 
    /** This is a callback method, invoked when the method getReadableDatabase() / getWritableDatabase() is called
    * provided the database does not exists
    * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =     " CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE + " ( " +
                         FIELD_ROW_ID + " integer primary key autoincrement , " +
                         FIELD_FIRST_NAME + " text ," +
                         FIELD_SECOND_NAME + " text ," +
                         FIELD_EMAIL + " text ," +
                         FIELD_PHONE + " text NOT NULL UNIQUE ," +
                         FIELD_USER_PHOTO_URL + " text " +
                         " ) ";
 
        db.execSQL(sql);
    }
 
    /** Inserts a new contacts to the table contacts */
    public long insert(ContentValues contentValues){
        long rowID =mDB.insert(DATABASE_TABLE, null, contentValues);
        return rowID;
    }

    public boolean insertContactlist(List<ContentValues> values) {

        SQLiteDatabase contactsDB = this.getWritableDatabase();
        contactsDB.beginTransaction();
        try {
            for(int i = 0; i < values.size(); i++) {
                contactsDB.insert(DATABASE_TABLE, null, values.get(i));
            }
            contactsDB.setTransactionSuccessful();
        } finally {
            contactsDB.endTransaction();
            return true;

        }
    }

    public List<ContactsModel> getContact(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<ContactsModel> list = new ArrayList<ContactsModel>();
        Cursor cursor = db.query(true, DATABASE_TABLE, new String[]{FIELD_ROW_ID, FIELD_FIRST_NAME, FIELD_SECOND_NAME, FIELD_PHONE, FIELD_EMAIL, FIELD_USER_PHOTO_URL}, FIELD_FIRST_NAME + " LIKE ?",
                new String[]{"%"+ name+ "%" }, null, null, FIELD_FIRST_NAME+" ASC", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String firstName = cursor.getString(cursor.getColumnIndex("firstName"));
            String secondName = cursor.getString(cursor.getColumnIndex("secondName"));
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String email = cursor.getString(cursor.getColumnIndex("email"));
            String photo = cursor.getString(cursor.getColumnIndex("photourl"));

            ContactsModel contactsModel = new ContactsModel();

            contactsModel.setId(id);
            contactsModel.setFirstName(firstName);
            contactsModel.setSecondName(secondName);
            contactsModel.setPhoneNumber(phone);
            contactsModel.setEmail(email);
            contactsModel.setPhotourl(photo);
            list.add(contactsModel);

            cursor.moveToNext();
        }
// make sure to close the cursor
        cursor.close();
        return list;
    }

    public long update(ContentValues contentValues, int id) {
        return mDB.update(DATABASE_TABLE, contentValues, "_id="+id, null);
    }
 
    /** Deletes all contacts from the table */
    public int del(){
        int cnt = mDB.delete(DATABASE_TABLE, null , null);
        return cnt;
    }
 
    /** Returns all the contacts from the table */
    public List<ContactsModel> getAllContacts(){

        List<ContactsModel> list = new ArrayList<ContactsModel>();
        Cursor cursor = mDB.query(DATABASE_TABLE, new String[] { FIELD_ROW_ID,  FIELD_FIRST_NAME , FIELD_SECOND_NAME, FIELD_PHONE, FIELD_EMAIL , FIELD_USER_PHOTO_URL} , null, null, null, null, FIELD_FIRST_NAME+" ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String firstName = cursor.getString(cursor.getColumnIndex("firstName"));
            String secondName = cursor.getString(cursor.getColumnIndex("secondName"));
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String email = cursor.getString(cursor.getColumnIndex("email"));
            String photo = cursor.getString(cursor.getColumnIndex("photourl"));

            ContactsModel contactsModel = new ContactsModel();

            contactsModel.setId(id);
            contactsModel.setFirstName(firstName);
            contactsModel.setSecondName(secondName);
            contactsModel.setPhoneNumber(phone);
            contactsModel.setEmail(email);
            contactsModel.setPhotourl(photo);
            list.add(contactsModel);

            cursor.moveToNext();
        }
// make sure to close the cursor
        cursor.close();


        return list;

    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}