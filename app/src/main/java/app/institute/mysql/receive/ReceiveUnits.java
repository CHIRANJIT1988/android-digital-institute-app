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

import java.util.HashMap;
import java.util.Map;

import app.institute.app.MyApplication;
import app.institute.helper.OnTaskCompleted;
import app.institute.model.Branch;
import app.institute.model.Class;
import app.institute.model.Subject;
import app.institute.model.Unit;

import static app.institute.configuration.Configuration.API_URL;


public class ReceiveUnits
{

	private OnTaskCompleted listener;
	
	private String URL = "";

	private Context context;
	private Branch _branch;

	private static final int MAX_ATTEMPTS = 10;
	private int ATTEMPTS_COUNT;


	public ReceiveUnits(Context context , OnTaskCompleted listener)
	{

		this.listener = listener;
		this.context = context;

		this.URL = API_URL + "receive-units.php";
	}


	public void receive(Branch _branch)
	{

		this._branch = _branch;
		execute();
	}


	private void execute()
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

							String unit_id = jsonObj.getString("unit_id");
							String branch_code = jsonObj.getString("branch_code");
							String class_code = jsonObj.getString("class_code");
							String subject_code = jsonObj.getString("subject_code");
							String unit_name = jsonObj.getString("unit_name");

							Subject _subject = new Subject(subject_code);
							Class _class = new Class(class_code);
							Unit unit = new Unit(new Branch(_subject, _class, branch_code), unit_id, unit_name);

							Unit.unitList.add(unit);
						}

						listener.onTaskCompleted(true, 200, "success");
						return;
					}

					listener.onTaskCompleted(false, 200, "No Unit Found");
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
		})

		{

			@Override
			protected Map<String, String> getParams()
			{

				Map<String, String> params = new HashMap<>();

				try
				{

					JSONObject jsonObject = new JSONObject();

					jsonObject.put("branch_code", _branch.branch_code);
					jsonObject.put("class_code", _branch._class.class_code);
					jsonObject.put("subject_code", _branch._subject.subject_code);

					params.put("responseJSON", jsonObject.toString());
				}

				catch (JSONException e)
				{

				}

				Log.v("params", "" + params);

				return params;
			}
		};

		// Adding request to request queue
		MyApplication.getInstance().addToRequestQueue(postRequest);
	}
}