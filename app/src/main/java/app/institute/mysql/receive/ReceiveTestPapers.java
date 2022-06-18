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
import app.institute.helper.Security;
import app.institute.model.Branch;
import app.institute.model.MockTest;

import static app.institute.configuration.Configuration.API_URL;
import static app.institute.configuration.Configuration.SECRET_KEY;


public class ReceiveTestPapers
{

	private OnTaskCompleted listener;

	private String URL = "";

	private Context context;
	private Branch _branch;
	private String user_id;

	private static final int MAX_ATTEMPTS = 10;
	private int ATTEMPTS_COUNT;


	public ReceiveTestPapers(Context context , OnTaskCompleted listener)
	{

		this.listener = listener;
		this.context = context;

		this.URL = API_URL + "receive-test-papers.php";
	}


	public void receive(Branch branch, String user_id)
	{

		this._branch = branch;
		this.user_id = user_id;
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

					MockTest.testPaperList.clear();

					if(arr.length() > 0)
					{

						for (int i = 0; i < arr.length(); i++)
						{

							JSONObject jsonObj = (JSONObject) arr.get(i);

							int test_id = jsonObj.getInt("test_id");
							String test_name = jsonObj.getString("test_name");
							int total_marks = jsonObj.getInt("total_marks");
							int duration = jsonObj.getInt("duration");
							int positive_score = jsonObj.getInt("positive_score");
							int negative_store = jsonObj.getInt("negative_score");
							String attempted_on = jsonObj.getString("attempted_on");

							int percentage = ((positive_score - negative_store) *100)/total_marks;

							MockTest test = new MockTest(test_id, test_name, total_marks, duration, attempted_on, percentage);

							MockTest.testPaperList.add(test);
						}

						listener.onTaskCompleted(true, 200, "success");
						return;
					}

					listener.onTaskCompleted(false, 200, "No Test Available");
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
					jsonObject.put("user_id", user_id);

					params.put("responseJSON", Security.encrypt(jsonObject.toString(), SECRET_KEY));
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