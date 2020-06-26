package com.example.beaker;

import java.util.regex.Pattern;

public class checkerClass {

    public static boolean passwordCheck(String password) {
        boolean check1 = false;
        char[] pass = password.toCharArray();
        check1 = password.matches(".*[0-9]{1,}.*") && password.matches(".*[@#$]{1,}.*") && password.length() >= 6 && password.length() <= 20;
        return check1;
    }

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
