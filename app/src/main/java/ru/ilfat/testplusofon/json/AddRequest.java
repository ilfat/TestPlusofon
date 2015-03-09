package ru.ilfat.testplusofon.json;

/**
 * Created by userocker on 09.03.2015.
 */
public class AddRequest {
    long phone;
    String name, surname;

    public AddRequest(long phone, String name, String surname) {
        this.phone = phone;
        this.name = name;
        this.surname = surname;
    }
}
