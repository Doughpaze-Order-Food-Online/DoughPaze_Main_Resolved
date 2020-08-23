package com.example.doughpaze.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class validation {

    public static boolean validateFields(String name){

        return !TextUtils.isEmpty(name);
    }

    public static boolean validateEmail(String string) {

        return !TextUtils.isEmpty(string) && Patterns.EMAIL_ADDRESS.matcher(string).matches();
    }

    public  static boolean validatePassword(String p1, String p2)
    {
        return !p1.equals(p2);
    }

    public static boolean validatePhone(String string)
    {
        return !TextUtils.isEmpty(string) && Patterns.PHONE.matcher(string).matches();
    }



}