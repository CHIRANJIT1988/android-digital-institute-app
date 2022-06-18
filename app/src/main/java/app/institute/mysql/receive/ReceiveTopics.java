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
import app.institute.model.Topic;
import app.institute.model.Unit;

import static app.institute.configuration.Configuration.API_URL;


public class ReceiveTopics
{

	private OnTaskCompleted listener;
	
	private String URL = "";

	private Context context;
	private String unit_id;

	private static final int MAX_ATTEMPTS = 10;
	private int ATTEMPTS_COUNT;


	public ReceiveTopics(Context context , OnTaskCompleted listener)
	{

		this.listener = listener;
		this.context = context;

		this.URL = API_URL + "receive-topics.php";
	}


	public void receive(String unit_id)
	{

		this.unit_id = unit_id;
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

							String topic_id = jsonObj.getString("topic_id");
							String unit_id = jsonObj.getString("unit_id");
							String topic_name = jsonObj.getString("topic_name");

							Topic topic = new Topic(new Unit(unit_id), topic_id, topic_name);

							Topic.topicList.add(topic);
						}

						listener.onTaskCompleted(true, 200, "success");
						return;
					}

					listener.onTaskCompleted(false, 200, "No Topic Found");
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
					params.put("unit_id", unit_id);
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