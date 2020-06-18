package com.device.onsuritycontact.model;

import android.os.Parcel;
import android.os.Parcelable;


public class ContactsModel implements Parcelable {

    private int id;
    private String first_name;
    private String last_name;
    private String email_id;
    private String profile_image;
    private String mobile_number;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String firstName) {
        this.first_name = firstName;
    }

    public String getPhoneNumber() {
        return mobile_number;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.mobile_number = phoneNumber;
    }


    public String getSecondName() {
        return last_name;
    }

    public void setSecondName(String secondName) {
        this.last_name = secondName;
    }

    public String getEmail() {
        return email_id;
    }

    public void setEmail(String email) {
        this.email_id = email;
    }

    public String getPhotourl() {
        return profile_image;
    }

    public void setPhotourl(String photourl) {
        this.profile_image = photourl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.first_name);
        dest.writeString(this.last_name);
        dest.writeString(this.email_id);
        dest.writeString(this.profile_image);
        dest.writeString(this.mobile_number);
    }

    public ContactsModel() {
    }

    protected ContactsModel(Parcel in) {
        this.id = in.readInt();
        this.first_name = in.readString();
        this.last_name = in.readString();
        this.email_id = in.readString();
        this.profile_image = in.readString();
        this.mobile_number = in.readString();
    }

    public static final Parcelable.Creator<ContactsModel> CREATOR = new Parcelable.Creator<ContactsModel>() {
        @Override
        public ContactsModel createFromParcel(Parcel source) {
            return new ContactsModel(source);
        }

        @Override
        public ContactsModel[] newArray(int size) {
            return new ContactsModel[size];
        }
    };
}
