package app.institute.mysql.send;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import app.institute.app.MyApplication;
import app.institute.configuration.Configuration;
import app.institute.helper.OnTaskCompleted;
import app.institute.helper.Security;


import static app.institute.configuration.Configuration.API_URL;
import static app.institute.configuration.Configuration.SECRET_KEY;


public class UpdateProfileDetails
{

	private OnTaskCompleted listener;
	
	private String URL = "";

	private Context context;
	private String json_data, user_id;

	private SharedPreferences preferences;

	private static final int MAX_ATTEMPTS = 5;
	private int ATTEMPTS_COUNT;
	
	
	public UpdateProfileDetails(Context context , OnTaskCompleted listener)
	{

		this.listener = listener;
		this.context = context;
		this.preferences = context.getSharedPreferences(Configuration.SHARED_PREF, Context.MODE_PRIVATE);
		this.URL = API_URL + "sync-profile-details.php";
	}
	
	
	public void update(String json_data, String user_id)
	{

		this.json_data = json_data;
		this.user_id = user_id;
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

					JSONObject jsonObj = new JSONObject(response);

					int status_code = jsonObj.getInt("status_code");
					String message = jsonObj.getString("message");

					Log.v("Response: " , "" + response);


					if (status_code == 200) // checking for error node in json
					{
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
					params.put("responseJSON", Security.encrypt(json_data, preferences.getString("key", "")));
					params.put("user", Security.encrypt(user_id, SECRET_KEY));
				}

				catch (Exception e)
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