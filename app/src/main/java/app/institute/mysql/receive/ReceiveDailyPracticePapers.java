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
import app.institute.model.DailyPracticePaper;
import app.institute.model.Subject;
import app.institute.model.Unit;

import static app.institute.configuration.Configuration.API_URL;


public class ReceiveDailyPracticePapers
{

	private OnTaskCompleted listener;
	
	private String URL = "";

	private Context context;
	private Unit unit;

	private static final int MAX_ATTEMPTS = 10;
	private int ATTEMPTS_COUNT;


	public ReceiveDailyPracticePapers(Context context , OnTaskCompleted listener)
	{

		this.listener = listener;
		this.context = context;

		this.URL = API_URL + "receive-daily-practice-papers.php";
	}


	public void receive(Unit unit)
	{

		this.unit = unit;
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

							String paper_code = jsonObj.getString("paper_code");
							String unit_id = jsonObj.getString("unit_id");
							String branch_code = jsonObj.getString("branch_code");
							String class_code = jsonObj.getString("class_code");
							String subject_code = jsonObj.getString("subject_code");
							String paper_name = jsonObj.getString("paper_name");
							String paper_date = jsonObj.getString("paper_date");
							String daily_practice_paper = jsonObj.getString("daily_practice_paper");

							Subject _subject = new Subject(subject_code);
							Class _class = new Class(class_code);
							Branch _branch = new Branch(_subject, _class, branch_code);
							Unit _unit = new Unit(_branch, unit_id);

							DailyPracticePaper dpp = new DailyPracticePaper(_unit, paper_code, paper_name, paper_date, daily_practice_paper);

							DailyPracticePaper.dppList.add(dpp);
						}

						listener.onTaskCompleted(true, 200, "success");
						return;
					}

					listener.onTaskCompleted(false, 200, "DPP Not Found");
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

					jsonObject.put("branch_code", unit.branch.branch_code);
					jsonObject.put("class_code", unit.branch._class.class_code);
					jsonObject.put("subject_code", unit.branch._subject.subject_code);
					jsonObject.put("unit_id", unit.unit_id);

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