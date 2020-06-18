package com.device.onsuritycontact.asynctask;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.widget.Toast;

import com.device.onsuritycontact.activity.MainActivity;
import com.device.onsuritycontact.database.ContactsDB;
import com.device.onsuritycontact.model.ContactsModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RequestTask extends AsyncTask<String, Integer, JSONObject> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected JSONObject doInBackground(String... urls) {
        URL url = null;
        try {
            url = new URL(urls[0]);

            HttpURLConnection urlConnection = null;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */ );
            urlConnection.setConnectTimeout(15000 /* milliseconds */ );
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            String jsonString = sb.toString();
            System.out.println("JSON: " + jsonString);

            return new JSONObject(jsonString);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(JSONObject result) {
        // this is executed on the main thread after the process is over
        // update your UI here
        final List<ContentValues> valuesList = new ArrayList<>();
        try {
            ContactsModel[] contactsModels = new Gson().fromJson(String.valueOf(result.getJSONArray("data")), ContactsModel[].class);
            for (ContactsModel contactsModel:
                    contactsModels) {
                ContentValues contentValues = new ContentValues();


                contentValues.put(ContactsDB.FIELD_FIRST_NAME, contactsModel.getFirstName() + " " + contactsModel.getSecondName());
                contentValues.put(ContactsDB.FIELD_SECOND_NAME, "");
                contentValues.put(ContactsDB.FIELD_EMAIL, contactsModel.getEmail() );
                contentValues.put(ContactsDB.FIELD_USER_PHOTO_URL, contactsModel.getPhotourl());
                contentValues.put(ContactsDB.FIELD_PHONE, contactsModel.getPhoneNumber() );
                valuesList.add(contentValues);
            }

            new ContactsDB(MainActivity.mContext).insertContactlist(valuesList);
            Toast.makeText(MainActivity.mContext, "Data fetched from server", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){}

    }
}