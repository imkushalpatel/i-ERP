package helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dexteronweb.i_erp.Login;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SessionManager {
    // Sharedpref file name
    private static final String PREF_NAME = "i-ERP";
    // User name (make variable public to access from outside)
   // public static final String KEY_NAME = "name";
    public static final String KEY_ENTITY_ID = "entityid";
    // Email address (make variable public to access from outside)

    public static final String KEY_ID = "id";
    public static final String KEY_USER_TYPE="type";



    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String userid, String entityid,String usertype) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        //editor.putString(KEY_NAME, name);
        editor.putString(KEY_ID,userid);
        // Storing email in pref
        editor.putString(KEY_ENTITY_ID,entityid);
        editor.putString(KEY_USER_TYPE,usertype);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, Login.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        //user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_ENTITY_ID, pref.getString(KEY_ENTITY_ID, null));
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_USER_TYPE, pref.getString(KEY_USER_TYPE, null));

        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, Login.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * *
     */
    // Get Login State
    public boolean isLoggedIn() {

        return pref.getBoolean(IS_LOGIN, false);
    }
}