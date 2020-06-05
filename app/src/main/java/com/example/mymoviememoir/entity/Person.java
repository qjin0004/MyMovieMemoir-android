package com.example.mymoviememoir.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {
    private int personId;
    private String firstname;
    private String surname;
    private String gender;
    private String dob;
    private String address;
    private String state;
    private int postcode;

    public Person(int personId){
        this.personId = personId;
    }

    public Person(int personId,String firstname, String surname,String gender,String dob,
                  String address,String state,int postcode){
        this.personId = personId;
        this.firstname = firstname;
        this.surname = surname;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.state = state;
        this.postcode = postcode;
    }

    public Person(String firstname, String surname,String gender,String dob,
                  String address,String state,int postcode){
        this.firstname = firstname;
        this.surname = surname;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.state = state;
        this.postcode = postcode;
    }

    //step2 for Parcelable
    public void writeToParcel(Parcel parcel, int flags){
        parcel.writeInt(personId);
        parcel.writeString(firstname);
        parcel.writeString(surname);
        parcel.writeString(gender);
        parcel.writeString(dob);
        parcel.writeString(address);
        parcel.writeString(state);
        parcel.writeInt(postcode);
    }

    //step3
    public Person(Parcel in){
        this.personId = in.readInt();
        this.firstname = in.readString();
        this.surname = in.readString();
        this.gender = in.readString();
        this.dob = in.readString();
        this.address = in.readString();
        this.state = in.readString();
        this.postcode = in.readInt();
    }

    //step4
    public int describeContents(){
        return 0;
    }

    //step5
    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>(){
        @Override
        public Person createFromParcel(Parcel in){
            return new Person(in);
        }
        @Override
        public Person[] newArray(int size){
            return new Person[size];
        }
    };


    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDOB(String dob) {
        this.dob = dob;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPostcode(int postcode) {
        this.postcode = postcode;
    }


    public String getFirstName() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getAddress() {
        return address;
    }

    public String getState() {
        return state;
    }

    public int getPostcode() {
        return postcode;
    }

    public int getPersonId() {
        return personId;
    }

}
