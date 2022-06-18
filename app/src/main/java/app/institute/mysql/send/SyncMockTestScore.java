package app.institute.mysql.send;

import java.util.HashMap;
import java.util.Map;

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
import app.institute.model.MockTest;


import static app.institute.configuration.Configuration.API_URL;


public class SyncMockTestScore
{

	private OnTaskCompleted listener;
	
	private String URL = "";

	private Context context;
	private MockTest mock_test;
	private String user_id;

	private static final int MAX_ATTEMPTS = 5;
	private int ATTEMPTS_COUNT;
	
	
	public SyncMockTestScore(Context context , OnTaskCompleted listener)
	{

		this.listener = listener;
		this.context = context;
		this.URL = API_URL + "sync-mock-test-score.php";
	}
	
	
	public void sync(MockTest mock_test, String user_id)
	{

		this.mock_test = mock_test;
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

					JSONObject jsonObject = new JSONObject();

					jsonObject.put("user_id", user_id);
					jsonObject.put("mock_test_id", mock_test.test_id);
					jsonObject.put("count_correct", mock_test.count_correct);
					jsonObject.put("count_wrong", mock_test.count_wrong);
					jsonObject.put("count_not_attempt", mock_test.count_not_attempt);
					jsonObject.put("positive_score", mock_test.question.positive_marks);
					jsonObject.put("negative_store", mock_test.question.negative_marks);

					params.put("responseJSON", jsonObject.toString());

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