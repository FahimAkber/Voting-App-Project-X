package com.aversoft.votingapp.Model;

public class User {
    String id, name, nId, pass;

    public User() {
    }

    public User(String id, String name, String nId, String pass) {
        this.id = id;
        this.name = name;
        this.nId = nId;
        this.pass = pass;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getnId() {
        return nId;
    }

    public String getPass() {
        return pass;
    }
}
