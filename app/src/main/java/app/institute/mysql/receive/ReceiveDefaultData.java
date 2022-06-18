package app.institute.mysql.receive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import app.institute.app.MyApplication;
import app.institute.helper.OnTaskCompleted;
import app.institute.model.Branch;
import app.institute.model.Class;
import app.institute.model.Subject;
import app.institute.sqlite.SQLiteDatabaseHelper;

import static app.institute.configuration.Configuration.API_URL;


public class ReceiveDefaultData
{

	private OnTaskCompleted listener;
	
	private String URL = "";

	private Context context;

	private static final int MAX_ATTEMPTS = 10;
	private int ATTEMPTS_COUNT;
	private SQLiteDatabaseHelper helper;
	


	public ReceiveDefaultData(Context context , OnTaskCompleted listener)
	{

		this.listener = listener;
		this.context = context;
		this.helper = new SQLiteDatabaseHelper(context);

		this.URL = API_URL + "receive-default-data.php";
	}


	public void execute()
	{

		StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

			@Override
			public void onResponse(String response)
			{

				try
				{

					Log.v("response: ", response);

					JSONArray arr = new JSONArray(response);


					if(arr.length() > 0)
					{


						for (int i = 0; i < arr.length(); i++)
						{

							JSONObject jsonObj = (JSONObject) arr.get(i);

							String branch_code = jsonObj.getString("branch_code");
							String branch_name = jsonObj.getString("branch_name");
							String subject_code = jsonObj.getString("subject_code");
							String subject_name = jsonObj.getString("subject_name");
							String class_code = jsonObj.getString("class_code");
							String class_name = jsonObj.getString("class_name");

							Class _class = new Class(class_code, class_name);
							Subject _subject = new Subject(subject_code, subject_name);

							Branch _branch = new Branch(_subject, _class, branch_code, branch_name);

							helper.insertBranch(_branch);
						}

						listener.onTaskCompleted(true, 200, "Synchronization Successful");
						return;
					}

					listener.onTaskCompleted(false, 200, "No Data Found");
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

				Log.v("Product List: ", "" + error);

				if(ATTEMPTS_COUNT != MAX_ATTEMPTS)
				{

					execute();

					ATTEMPTS_COUNT ++;

					Log.v("#Attempt No: ", "" + ATTEMPTS_COUNT);
					return;
				}

				listener.onTaskCompleted(false, 500, "Internet Connection Failure"); // Invalid User

			}
		});

		// Adding request to request queue
		MyApplication.getInstance().addToRequestQueue(postRequest);
	}
}