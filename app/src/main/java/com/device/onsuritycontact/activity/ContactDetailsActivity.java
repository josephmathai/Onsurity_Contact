package com.device.onsuritycontact.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.device.onsuritycontact.R;
import com.device.onsuritycontact.model.ContactsModel;

public class ContactDetailsActivity extends AppCompatActivity {

    private static final int REQUEST_PHONE_CALL = 1;

    private ContactsModel contactsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        // getting extras from listview
        contactsModel = (ContactsModel) getIntent().getExtras().getParcelable("contact");

        ImageView image = findViewById(R.id.imageView);
        TextView name = findViewById(R.id.name);
        TextView email = findViewById(R.id.email);
        TextView phone = findViewById(R.id.phone_number);

        AppCompatButton emailButton = findViewById(R.id.email_button);

        if (contactsModel.getPhotourl() != null) {
            Uri uri = Uri.parse(contactsModel.getPhotourl());
            Glide.with(this).load(uri).placeholder(getResources().getDrawable(R.drawable.ic_phone)).circleCrop().into(image);
        }

        if (contactsModel.getFirstName() != null) {
            name.setText(contactsModel.getFirstName());
        }

        if (contactsModel.getEmail() != null && !contactsModel.getEmail().equals("null")) {
            email.setText(contactsModel.getEmail());
        } else {
            email.setText("Not given");
            emailButton.setClickable(false);
        }

        if (contactsModel.getPhoneNumber() != null) {
            phone.setText(contactsModel.getPhoneNumber());
        }
    }

    public void OnEmailClicked(View view) {
        // email intent :- only enable button with valid email id
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, contactsModel.getEmail());
        intent.putExtra(Intent.EXTRA_SUBJECT, "From Onsurity Contact app");
        intent.putExtra(Intent.EXTRA_TEXT, "This is email body.");

        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    public void OnCallClicked(View view) {
        // call intent
        if (ContextCompat.checkSelfPermission(ContactDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ContactDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactsModel.getPhoneNumber()));
            startActivity(intent);
        }

    }
}
