package com.device.onsuritycontact.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.device.onsuritycontact.R;
import com.device.onsuritycontact.adapter.ContactsListAdapter;
import com.device.onsuritycontact.asynctask.RequestTask;
import com.device.onsuritycontact.database.ContactsContentProvider;
import com.device.onsuritycontact.database.ContactsDB;
import com.device.onsuritycontact.model.ContactsModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private String TAG = "com.device.onsuritycontact.activity.MainActivity";

    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static Context mContext;
    private ProgressBar mProgressbar;
    private ListView mList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mProgressbar = findViewById(R.id.progressBar);
        EditText search = findViewById(R.id.search_text);
        mList = findViewById(R.id.contact_list);

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                if(s.length() != 0) {

                    List<ContactsModel> contacts = new ContactsDB(mContext).getContact(s.toString());
                    mList.setAdapter(new ContactsListAdapter(mContext, R.layout.contacts_list_item, contacts));
                } else {
                    List<ContactsModel> contacts = new ContactsDB(mContext).getAllContacts();
                    mList.setAdapter(new ContactsListAdapter(mContext, R.layout.contacts_list_item, contacts));
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<ContactsModel> contacts = new ContactsDB(mContext).getAllContacts();
                mList.setAdapter(new ContactsListAdapter(mContext, R.layout.contacts_list_item, contacts));
                if (contacts.isEmpty()) {
                    requestContactPermission();
                }
            }
        }, 300);

    }

    @SuppressLint("StaticFieldLeak")
    private void getContactList() {


        final ArrayList<ContentValues> valuesList = new ArrayList<>();


        new RequestTask().execute("https://7yd7u01nw9.execute-api.ap-south-1.amazonaws.com/prod/contact-list");

        final ContentResolver cr = getContentResolver();
        final Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            mProgressbar.setVisibility(View.VISIBLE);

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {

                    String[] projection = new String[] {
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,

                            //plus any other properties you wish to query
                    };

                    Cursor cursor = null;
                    try {
                        cursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
                    } catch (SecurityException e) {
                        //SecurityException can be thrown if we don't have the right permissions
                    }


                    if (cursor != null) {
                        try {
                            HashSet<String> normalizedNumbersAlreadyFound = new HashSet<>();
                            int indexOfNormalizedNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
                            int indexOfDisplayName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                            int indexOfDisplayNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            int indexOfContactId = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);

                            while (cursor.moveToNext()) {

                                String normalizedNumber = cursor.getString(indexOfNormalizedNumber);
                                if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {

                                    String displayName = cursor.getString(indexOfDisplayName);
                                    String displayNumber = cursor.getString(indexOfDisplayNumber);
                                    String ContactID = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

                                    Cursor emailCur = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{ContactID},null);
                                    String emailAddress = null;
                                    while (emailCur.moveToNext()) {
                                        emailAddress = emailCur.getString( emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                                    }
                                    emailCur.close();

                                    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                                            .parseLong(ContactID));

                                    String photo = String.valueOf(Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY));

                                    ContentValues contentValues = new ContentValues();

                                    contentValues.put(ContactsDB.FIELD_FIRST_NAME, displayName);
                                    contentValues.put(ContactsDB.FIELD_SECOND_NAME, "");
                                    contentValues.put(ContactsDB.FIELD_EMAIL, emailAddress );
                                    contentValues.put(ContactsDB.FIELD_USER_PHOTO_URL, photo);
                                    contentValues.put(ContactsDB.FIELD_PHONE, displayNumber );

                                    Log.i(TAG, "Name: " + displayName );
                                    Log.i(TAG, "second: " + "");
                                    Log.i(TAG, "email: " + emailAddress );
                                    Log.i(TAG, "photo: " + photo );
                                    Log.i(TAG, "Phone Number: " + displayNumber);

                                    valuesList.add(contentValues);
                                    //haven't seen this number yet: do something with this contact!
                                } else {
                                    //don't do anything with this contact because we've already found this number
                                }
                            }
                        } finally {
                            cursor.close();
                        }
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    Set<ContentValues> set = new HashSet<>(valuesList);
                    valuesList.clear();
                    valuesList.addAll(set);

                    new ContactsDB(mContext).insertContactlist(valuesList);
                    if(cur!=null){
                        cur.close();
                    }
                    mProgressbar.setVisibility(View.GONE);

                    List<ContactsModel> contacts = new ContactsDB(mContext).getAllContacts();
                    mList.setAdapter(new ContactsListAdapter(mContext, R.layout.contacts_list_item, contacts));
                }
            }.execute();
        }
    }

    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read Contacts permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                getContactList();
            }
        } else {
            getContactList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContactList();
                } else {
                    Toast.makeText(this, "You have disabled the contacts permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public static class ContactsInsertTask extends AsyncTask<ContentValues, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(ContentValues... contentValues) {

            /** Setting up values to insert the clicked location into SQLite database */
            mContext.getContentResolver().insert(ContactsContentProvider.CONTENT_URI, contentValues[0]);
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_sync:
                deleteContacts();
                getContactList();
                return true;
            case R.id.action_logout:
                deleteContacts();
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_CLEAR_CREDENTIALS, true);

        startActivity(intent);
        finish();
    }

    @SuppressLint("StaticFieldLeak")
    private void deleteContacts(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                new ContactsDB(mContext).del();
                return null;
            }
        }.execute();
    }

}
