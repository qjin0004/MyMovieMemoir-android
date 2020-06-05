package com.example.mymoviememoir.entity;
public class Credentials {
    private String username;
    private String password;
    private String signUpDate;
    private Person personId;

    public Credentials(String userName,String password,String signUpDate){
        this.username = userName;
        this.password = password;
        this.signUpDate = signUpDate;
    }

    public Person getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = new Person(personId);
    }


    public String getUserName() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSignUpDate() {
        return signUpDate;
    }


    public void setUserName(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSignUpDate(String signUpDate) {
        this.signUpDate = signUpDate;
    }



}
