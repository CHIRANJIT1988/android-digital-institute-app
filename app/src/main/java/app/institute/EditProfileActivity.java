package app.institute;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import app.institute.configuration.Configuration;
import app.institute.helper.DatePickerFragment;
import app.institute.helper.Helper;
import app.institute.helper.OnTaskCompleted;
import app.institute.model.User;
import app.institute.mysql.send.UpdateProfileDetails;
import app.institute.session.SessionManager;


public class EditProfileActivity extends AppCompatActivity implements OnTaskCompleted, AdapterView.OnItemSelectedListener, View.OnClickListener
{

    private AppCompatSpinner spinner_gender;
    private EditText edit_name, edit_email, edit_location, edit_dob;
    private TextInputLayout layout_name, layout_email, layout_location, layout_dob;
    private String[] gender = { "Select Gender", "Male", "Female", "Other" };

    private SharedPreferences preferences;
    private SessionManager session;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Edit Profile");

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById();

        spinner_gender.setOnItemSelectedListener(this);
        edit_dob.setOnClickListener(this);


        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, gender);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner_gender.setAdapter(aa);

        this.preferences = getSharedPreferences(Configuration.SHARED_PREF, Context.MODE_PRIVATE);
        this.session = new SessionManager(this);
        this.pDialog = new ProgressDialog(this);

        setProfileData(preferences.getString("profile_details", ""));
    }


    private void initProgressDialog(String message)
    {

        pDialog.setMessage(message);
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(false);
        pDialog.show();
    }


    private void findViewById()
    {

        edit_name = (EditText) findViewById(R.id.editName);
        edit_email = (EditText) findViewById(R.id.editEmail);
        edit_location = (EditText) findViewById(R.id.editLocation);
        edit_dob = (EditText) findViewById(R.id.editDOB);

        spinner_gender = (AppCompatSpinner) findViewById(R.id.spinner_gender);

        layout_name = (TextInputLayout) findViewById(R.id.input_layout_name);
        layout_email = (TextInputLayout) findViewById(R.id.input_layout_email);
        layout_location = (TextInputLayout) findViewById(R.id.input_layout_location);
        layout_dob = (TextInputLayout) findViewById(R.id.input_layout_dob);
    }


    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {

            case R.id.editDOB:

                showDatePicker();
                break;
        }
    }


    private void setProfileData(String profile_data)
    {

        try
        {

            JSONObject jsonObj = new JSONObject(profile_data);

            String name = jsonObj.getString("name");
            String email = jsonObj.getString("email");
            String location = jsonObj.getString("location");
            String dob = jsonObj.getString("dob");
            String gender = jsonObj.getString("gender");

            edit_name.setText(name);
            edit_email.setText(email);
            edit_location.setText(location);
            edit_dob.setText(dob);

            if(gender.toLowerCase().equals("male"))
            {
                spinner_gender.setSelection(1);
            }

            else if(gender.toLowerCase().equals("female"))
            {
                spinner_gender.setSelection(2);
            }

            else if(gender.toLowerCase().equals("other"))
            {
                spinner_gender.setSelection(3);
            }

            else
            {
                spinner_gender.setSelection(0);
            }
        }

        catch (JSONException e)
        {

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case android.R.id.home:

                {
                    finish();
                    break;
                }

            case R.id.action_save:

                if(validateName() && validateEmail() && validateGender() && validateLocation() && validateDOB())
                {

                    initProgressDialog("Updating Profile ...");
                    String json_data = buildJSONData(initUserObject());
                    new UpdateProfileDetails(getApplicationContext(), this).update(json_data, session.getUserId());
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private User initUserObject()
    {

        User user = new User();

        user.setName(edit_name.getText().toString());
        user.setEmail(edit_email.getText().toString());
        user.setLocation(edit_location.getText().toString());
        user.setDateOfBirth(edit_dob.getText().toString());
        user.setGender(gender[spinner_gender.getSelectedItemPosition()]);

        return user;
    }


    private String buildJSONData(User user)
    {

        JSONObject jsonObject = new JSONObject();

        try
        {

            jsonObject.put("name", user.getName());
            jsonObject.put("email", user.getEmail());
            jsonObject.put("location", user.getLocation());
            jsonObject.put("gender", user.getGender());
            jsonObject.put("dob", user.getDateOfBirth());
            jsonObject.put("mobile_number", getIntent().getStringExtra("MOBILE"));
        }

        catch (JSONException e)
        {

        }

        return jsonObject.toString();
    }


    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)
    {

        /*if(position != 0)
        {
            Toast.makeText(getApplicationContext(), gender[position], Toast.LENGTH_LONG).show();
        }*/
    }


    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
        // TODO Auto-generated method stub
    }


    private void showDatePicker()
    {

        DatePickerFragment date = new DatePickerFragment();

        Calendar calender = Calendar.getInstance();

        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);

        date.setCallBack(ondate);
        date.show(getSupportFragmentManager(), "Select Date of Birth");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {

            Calendar now = Calendar.getInstance();

            Calendar choosen = Calendar.getInstance();
            choosen.set(year, monthOfYear, dayOfMonth);

            if (choosen.compareTo(now) > 0)
            {
                Toast.makeText(getApplicationContext(), "Invalid Selection", Toast.LENGTH_LONG).show();
                return;
            }


            // Initialize date variable to current date
            String dob = new StringBuilder().append(Helper.format_date(year)).append("-").append(Helper.format_date(monthOfYear + 1)).append("-").append(dayOfMonth).toString();
            edit_dob.setText(dob);
        }
    };


    private boolean validateName()
    {

        if(edit_name.getText().toString().trim().isEmpty())
        {
            edit_name.setError("Enter Your Name");
            edit_name.requestFocus();
            return false;
        }

        else
        {
            layout_name.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateEmail()
    {

        boolean flag = android.util.Patterns.EMAIL_ADDRESS.matcher(edit_email.getText().toString()).matches();

        if(!flag)
        {
            edit_email.setError("Invalid Email");
            edit_email.requestFocus();
            return false;
        }

        else
        {
            layout_email.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateLocation()
    {

        if(edit_location.getText().toString().trim().isEmpty())
        {
            edit_location.setError("Enter Location");
            edit_location.requestFocus();
            return false;
        }

        else
        {
            layout_location.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateDOB()
    {

        if(edit_dob.getText().toString().trim().isEmpty())
        {
            edit_dob.setError("Select DOB");
            edit_dob.requestFocus();
            return false;
        }

        else
        {
            layout_dob.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateGender()
    {

        if(spinner_gender.getSelectedItemPosition() == 0)
        {
            Toast.makeText(getApplicationContext(), "Select Gender", Toast.LENGTH_SHORT).show();
            spinner_gender.requestFocus();
            return false;
        }

        return true;
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        try
        {
            if(code == 200)
            {
                this.preferences.edit().putString("profile_details", buildJSONData(initUserObject())).apply();
                finish();
            }
        }

        catch (Exception e)
        {

        }

        finally
        {

            if(pDialog.isShowing())
            {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}