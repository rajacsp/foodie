package com.byteshaft.foodie.utils;

import android.app.Application;
import android.content.Context;


public class AppGlobals extends Application {

    private static Context sContext;
    public static final String USER_LOGIN_KEY = "user_login_key";
    public static final String KEY_USERNAME = "username ";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER_ID = "user_id";
    public static final String BASE_URL = "http://tode.ca/tode_rest_php/";
    public static final String SEND_IMAGES_URL = String.format("%sr_sa_add_food_entry.php", BASE_URL);
    public static final String LOGIN_URL = String.format("%sr_sa_user_select.php?", BASE_URL);
    public static final String REGISTER_URL = String.format("%sr_sa_registeruser.php", BASE_URL);
    public static final String NO_INTERNET_TITLE = "Info";
    public static final String NO_INTERNET_MESSAGE = "Internet not available, Cross check your " +
            "internet connectivity and try again";
    public static final String SUCCESS_TITLE = "Success";
    public static final String SUCCESS_MESSAGE = "Please login with your credentials";
    public static final String USER_EXIST = "Info";
    public static final String USER_EXIST_MESSAGE = "User already exist \nPlease select another username";
    public static boolean sRegisterStatus = false;
    public static final String GET_IMAGES_URL = "http://tode.ca/tode_rest_php" +
            "/r_sa_show_food_entries.php?userid=";
    public static final String IMAGES_LOCATION = "http://tode.ca/tode_rest_php/food_uploads/";


    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
}
