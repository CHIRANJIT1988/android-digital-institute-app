package app.institute.mysql.receive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.institute.app.MyApplication;
import app.institute.configuration.Configuration;
import app.institute.helper.OnTaskCompleted;
import app.institute.helper.Security;
import app.institute.model.MockTest;
import app.institute.model.Option;
import app.institute.model.Question;
import app.institute.session.SessionManager;

import static app.institute.configuration.Configuration.API_URL;
import static app.institute.configuration.Configuration.SECRET_KEY;


public class ReceiveTest
{

	private OnTaskCompleted listener;

	private String URL = "";

	private Context context;
	private int test_id;
	private SharedPreferences prefs = null;

	private static final int MAX_ATTEMPTS = 10;
	private int ATTEMPTS_COUNT;


	public ReceiveTest(Context context , OnTaskCompleted listener)
	{

		this.listener = listener;
		this.context = context;
		this.prefs = context.getSharedPreferences(Configuration.SHARED_PREF, Context.MODE_PRIVATE);
		this.URL = API_URL + "receive-test.php";
	}


	public void receive(int test_id)
	{

		this.test_id = test_id;

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

					String json_data = Security.decrypt(response, prefs.getString("key", ""));

					JSONArray arr = new JSONArray(json_data);

					if(arr.length() > 0)
					{

						for (int i = 0; i < arr.length(); i++)
						{

							JSONObject jsonObj = (JSONObject) arr.get(i);

							int mock_test_id = jsonObj.getInt("mock_test_id");
							int question_id = jsonObj.getInt("question_id");
							String question = jsonObj.getString("question");
							String diagram = jsonObj.getString("diagram");
							int total_marks = jsonObj.getInt("total_marks");
							int positive_marks = jsonObj.getInt("positive_marks");
							int negative_marks = jsonObj.getInt("negative_marks");
							int duration = jsonObj.getInt("duration");


							JSONArray option_array = new JSONArray(jsonObj.getString("options"));

							List<Option> optionList = new ArrayList<>();

							for (int j = 0; j < option_array.length(); j++)
							{

								jsonObj = (JSONObject) option_array.get(j);

								int option_id = jsonObj.getInt("option_id");
								String option = jsonObj.getString("option_details");
								int is_correct = jsonObj.getInt("is_correct");

								optionList.add(new Option(option_id, option, is_correct));

								Log.v("my_option_details", option);
							}

							Question questionObj = new Question(question_id, question, diagram, positive_marks, negative_marks, optionList);
							MockTest.testList.add(new MockTest(mock_test_id, total_marks, duration, questionObj));
						}

						listener.onTaskCompleted(true, 200, "success");
						return;
					}

					listener.onTaskCompleted(false, 200, "Questions Not Available");
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
					params.put("test_id", Security.encrypt(String.valueOf(test_id), SECRET_KEY));
					params.put("user_id", Security.encrypt(new SessionManager(context).getUserId(), SECRET_KEY));
				}

				catch (Exception e)
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