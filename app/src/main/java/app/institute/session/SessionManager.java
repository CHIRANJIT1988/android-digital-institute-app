package app.institute.session;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

import app.institute.RegisterActivity;
import app.institute.model.User;


public class SessionManager 
{
	
	SharedPreferences pref; // Shared Preferences
	
	Editor editor; // Editor for Shared preferences
	
	Context _context; // Context
	
	int PRIVATE_MODE = 0; // Shared pref mode
	
	
	private static final String PREF_NAME = "InstitutePref"; // Sharedpref file name
	
	private static final String IS_LOGIN = "IsLoggedIn"; // All Shared Preferences Keys

	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_USER_NAME = "user_name";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_MOBILE_NO = "mobile_no";
	
	
	@SuppressLint("CommitPrefEdits") 
	public SessionManager(Context context) // Constructor
	{
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	

	public void createLoginSession(User user)
	{
		
		editor.putBoolean(IS_LOGIN, true); // Storing login value as TRUE

		editor.putString(KEY_USER_ID, user.getUserId());
		editor.putString(KEY_USER_NAME, user.getName());
		editor.putString(KEY_PASSWORD, user.getPhoneNumber());
		editor.putString(KEY_MOBILE_NO, user.getPhoneNumber());
		
		editor.commit(); // commit changes
	}


	public boolean checkLogin()
	{
		
		if(!this.isLoggedIn()) // Check login status
		{
			
			/*Intent i = new Intent(_context, MainActivity.class); // user is not logged in redirect him to Login Activity
			
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Closing all the Activities
			
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add new Flag to start new Activity
			
			_context.startActivity(i); // Staring Login Activity*/
			
			return false;
			
		}
		
		return true;
	}
	
	
	
	/**
	 * Get stored session data
	 * */
	
	public HashMap<String, String> getUserDetails()
	{
		
		HashMap<String, String> store = new HashMap<>();

		store.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null)); // store id
		store.put(KEY_USER_NAME, pref.getString(KEY_USER_NAME, null)); // store name
		store.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null)); // password
		store.put(KEY_MOBILE_NO, pref.getString(KEY_MOBILE_NO, null)); // mobile no

		return store; // return user
	}


	public String getUserId() // Get Login State
	{
		return pref.getString(KEY_USER_ID, null);
	}


	/**
	 * Clear session details
	 * */
	
	public void logoutUser()
	{
		
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();
		
		
		// After logout redirect user to Register Activity
		Intent i = new Intent(_context, RegisterActivity.class);

		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Staring Login Activity
		_context.startActivity(i);
	
	}
	
	/**
	 * Quick check for login
	 * ***/
	
	public boolean isLoggedIn() // Get Login State
	{
		return pref.getBoolean(IS_LOGIN, false);
	}
}