package app.institute.mysql.receive;

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
import app.institute.helper.OnCountCompleted;
import app.institute.model.Branch;

import static app.institute.configuration.Configuration.API_URL;


public class CountUnit
{

	private OnCountCompleted listener;

	private String URL = "";

	private Context context;
	private Branch _branch;

	private static final int MAX_ATTEMPTS = 10;
	private int ATTEMPTS_COUNT;


	public CountUnit(Context context , OnCountCompleted listener)
	{

		this.listener = listener;
		this.context = context;

		this.URL = API_URL + "count-unit.php";
	}


	public void count(Branch branch)
	{

		this._branch = branch;
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

					JSONObject jsonObj = new JSONObject(response);

					int status_code = jsonObj.getInt("status_code");
					int total = jsonObj.getInt("total");


					if(status_code == 200)
					{
						listener.onCountCompleted(200, total, "unit");
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

						listener.onCountCompleted(500, 0, "Internet Connection Failure"); // Unsuccessful
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

				Log.v("Product List: ", "" + error);

				if(ATTEMPTS_COUNT != MAX_ATTEMPTS)
				{

					execute();

					ATTEMPTS_COUNT ++;

					Log.v("#Attempt No: ", "" + ATTEMPTS_COUNT);
					return;
				}

				listener.onCountCompleted(500, 0, "Internet Connection Failure"); // Invalid User

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