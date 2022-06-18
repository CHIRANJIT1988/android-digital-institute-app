package app.institute.mysql.send;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


import app.institute.RegisterActivity;
import app.institute.app.MyApplication;
import app.institute.configuration.Configuration;
import app.institute.helper.OnTaskCompleted;
import app.institute.helper.Security;
import app.institute.model.User;
import app.institute.session.SessionManager;

import static app.institute.configuration.Configuration.API_URL;
import static app.institute.configuration.Configuration.SECRET_KEY;


public class RegisterUser
{

	private OnTaskCompleted listener;
	
	private String URL = "";

	private Context context;
	SharedPreferences prefs = null;
	User user;

	private static final int MAX_ATTEMPTS = 5;
	private int ATTEMPTS_COUNT;

	
	public RegisterUser(Context _context , OnTaskCompleted listener)
	{
		this.listener = listener;
		this.context = _context;
		prefs = context.getSharedPreferences(Configuration.SHARED_PREF, Context.MODE_PRIVATE);
		this.URL = API_URL + "register-user.php";
	}
	
	
	public void register(User user)
	{

		this.user = user;
		execute();
	}


	public void execute()
	{

		StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

			@Override
			public void onResponse(String response)
			{

				try
				{

					Log.v("Response: ", response);

					JSONObject jsonObj = new JSONObject(response);

					int status_code = jsonObj.getInt("status_code");
					String message = jsonObj.getString("message");

					if (status_code == 200) // checking for error node in json
					{

						SharedPreferences preferences = context.getSharedPreferences(Configuration.SHARED_PREF, Context.MODE_PRIVATE);
						preferences.edit().putString("key", Security.decrypt(jsonObj.getString("key"), Configuration.SECRET_KEY)).apply();

						SessionManager session = new SessionManager(context);

						String user_id = Security.decrypt(jsonObj.getString("user_id"), Configuration.SECRET_KEY);
						String name = Security.decrypt(jsonObj.getString("user_name"), Configuration.SECRET_KEY);

						jsonObj = new JSONObject();
						jsonObj.put("name", name);
						jsonObj.put("email", "");
						jsonObj.put("location", "");
						jsonObj.put("gender", "");
						jsonObj.put("dob", "");
						jsonObj.put("mobile_number", RegisterActivity.user.phone_number);

						preferences.edit().putString("profile_details", jsonObj.toString()).apply();

						jsonObj = new JSONObject();
						jsonObj.put("school_name", "");
						jsonObj.put("state", "");

						preferences.edit().putString("school_details", jsonObj.toString()).apply();

						RegisterActivity.user.setUserId(user_id);
						RegisterActivity.user.setName(name);
						session.createLoginSession(RegisterActivity.user);

						listener.onTaskCompleted(true, status_code, message); // Successful
					}

					else
					{

						if(ATTEMPTS_COUNT != MAX_ATTEMPTS)
						{

							execute();

							ATTEMPTS_COUNT ++;

							Log.v("#Attempt No: ", "" + ATTEMPTS_COUNT);
							return;
						}

						listener.onTaskCompleted(false, status_code, message); // Unsuccessful
					}
				}

				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error)
			{

				if(ATTEMPTS_COUNT != MAX_ATTEMPTS)
				{

					execute();

					ATTEMPTS_COUNT ++;

					Log.v("#Attempt No: ", "" + ATTEMPTS_COUNT);
					return;
				}

				listener.onTaskCompleted(false, 500, "Internet connection fail. Try Again");
			}
		})

		{

			@Override
			protected Map<String, String> getParams()
			{

				Map<String, String> params = new HashMap<>();

				try
				{

					JSONObject jsonObject = new JSONObject();

					jsonObject.put("reg_id", user.fcm_reg_id);
					jsonObject.put("name", user.name);
					jsonObject.put("mobile_no", user.phone_number);
					jsonObject.put("password", user.password);
					jsonObject.put("device_id", user.device_id);

					params.put("responseJSON", Security.encrypt(jsonObject.toString(), SECRET_KEY));
				}

				catch (JSONException e)
				{

				}

				Log.v("Data: ", "" + params);

				return params;
			}
		};

		// Adding request to request queue
		MyApplication.getInstance().addToRequestQueue(postRequest);
	}
}