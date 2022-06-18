package app.institute;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import app.institute.helper.GenerateUniqueId;
import app.institute.helper.OnTaskCompleted;
import app.institute.model.User;
import app.institute.mysql.send.OTPVerification;
import app.institute.mysql.send.RegisterUser;
import app.institute.network.InternetConnectionDetector;

import static app.institute.RegisterActivity.user;
import static app.institute.configuration.Configuration.SHARED_PREF;


public class RegisterFragment extends Fragment implements View.OnClickListener, OnTaskCompleted, OTPFragment.AddSchoolFragmentListener
{

    private BroadcastReceiver mIntentReceiver;

    private TextInputLayout layout_name, layout_mobile_number, layout_password;

    private Button btnRegister, btnConfirmationCode;
    private EditText editName, editMobileNo, editPassword;
    private TextView tvStatus;
    private ProgressBar pBar;
    private LinearLayout relativeLayout;

    private static Context context = null;
    private SharedPreferences prefs = null;


    public RegisterFragment()
    {

    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        context = this.getActivity();

        this.prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        RegisterActivity.is_registration = true;
    }


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        findViewById(rootView);
        btnRegister.setOnClickListener(this);
        btnConfirmationCode.setOnClickListener(this);

        hideKeyboard(rootView);

        return rootView;
    }



    private void findViewById(View rootView)
    {

        layout_name = (TextInputLayout) rootView.findViewById(R.id.input_layout_name);
        layout_mobile_number = (TextInputLayout) rootView.findViewById(R.id.input_layout_phone_number);
        layout_password = (TextInputLayout) rootView.findViewById(R.id.input_layout_password);

        btnRegister = (Button) rootView.findViewById(R.id.btnRegister);
        btnConfirmationCode = (Button) rootView.findViewById(R.id.btnConfirmationCode);

        editName = (EditText) rootView.findViewById(R.id.editName);
        editPassword = (EditText) rootView.findViewById(R.id.editPassword);
        editMobileNo = (EditText) rootView.findViewById(R.id.editPhoneNumber);

        pBar = (ProgressBar) rootView.findViewById(R.id.pbLoading);
        tvStatus = (TextView) rootView.findViewById(R.id.status);
        relativeLayout = (LinearLayout) rootView.findViewById(R.id.linear_main);
    }



    /** Called when the activity is about to become visible. */
    @Override
    public void onStart() {

        super.onStart();
        Log.d("Inside : ", "onStart() event");
    }



    /** Called when another activity is taking focus. */
    @Override
    public void onPause() {
        super.onPause();
        Log.d("Inside : ", "onPause() event");
    }


    /** Called when the activity is no longer visible. */
    @Override
    public void onStop()
    {
        super.onStop();
        Log.d("Inside : ", "onStop() event");
    }


    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d("Inside : ", "onDestroy() event");
    }


    public void onClick(View v)
    {

        switch (v.getId())
        {

            case R.id.btnRegister:

                if(validateName() && validateMobileNumber() && validatePassword())
                {

                    if (!new InternetConnectionDetector(getActivity()).isConnected())
                    {
                        makeSnackbar("Internet connection fail");
                        return;
                    }

                    if(prefs.getString("reg_id", "").isEmpty())
                    {
                        Toast.makeText(context, "FCM ID Not Found. Please Wait ...", Toast.LENGTH_LONG).show();
                        return;
                    }

                    pBar.setVisibility(View.VISIBLE);
                    tvStatus.setText(String.valueOf("Waiting for OTP"));

                    user = initUserObject();

                    btnConfirmationCode.setVisibility(View.GONE);
                    new OTPVerification(context, this).otpVerify(user);
                }

                break;

            case R.id.btnConfirmationCode:

                FragmentManager fm = getActivity().getSupportFragmentManager();

                OTPFragment dialogFragment = new OTPFragment();
                dialogFragment.setListener(this);
                dialogFragment.setRetainInstance(true);
                dialogFragment.show(fm, "OTPFragment");
                break;
        }
    }


    @Override
    public void onDialogPositiveClick(android.support.v4.app.DialogFragment dialog, String new_password)
    {

        if (user.getConfirmationCode().equals(new_password))
        {

            btnConfirmationCode.setVisibility(View.VISIBLE);
            new RegisterUser(context, this).register(user);
            tvStatus.setText(String.valueOf("Registering ..."));
        }

        else
        {
            makeSnackbar("Verification Fail. Try Again");
        }
    }


    @Override
    public void onDialogNegativeClick(android.support.v4.app.DialogFragment dialog)
    {
        // Do nothing
    }


    private User initUserObject()
    {

        WifiManager m_wm = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        User user = new User();

        user.setName(editName.getText().toString());
        user.setPhoneNumber(editMobileNo.getText().toString());
        user.setPassword(editPassword.getText().toString());
        user.setConfirmationCode(String.valueOf(GenerateUniqueId.getRandomNo(999999, 100000)));
        user.setDeviceID(String.valueOf(m_wm.getConnectionInfo().getMacAddress()));
        user.fcm_reg_id = prefs.getString("reg_id", "");

        return user;
    }


    private void makeSnackbar(String msg)
    {

        Snackbar snackbar = Snackbar.make(relativeLayout, msg, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.myPrimaryColor));
        snackbar.show();
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }


    private void hideKeyboard(final View rootView) {

        editMobileNo.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editMobileNo.getText().toString().trim().length() == 10) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                }
            }
        });
    }



    @Override
    public void onResume()
    {

        super.onResume();

        IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.MAIN");

        mIntentReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent)
            {

                try
                {

                    String msg = intent.getStringExtra("get_msg");

                    if(msg.contains("registration"))
                    {

                        String otp = msg.substring(Math.max(0, msg.length() - 6));

                        if(otp.equals(user.confirmation_code))
                        {

                            btnConfirmationCode.setVisibility(View.VISIBLE);
                            new RegisterUser(context, RegisterFragment.this).register(user);
                            tvStatus.setText(String.valueOf("Registering ..."));
                        }
                    }
                }

                catch (Exception e)
                {
                    Toast.makeText(context, "Registration Error", Toast.LENGTH_SHORT).show();
                }
            }
        };

        getActivity().registerReceiver(mIntentReceiver, intentFilter);
    }


    private boolean validateName()
    {

        if(editName.getText().toString().trim().length() < 3)
        {
            editName.setError("Name must be at least 3 characters");
            editName.requestFocus();
            return false;
        }

        else
        {
            layout_name.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateMobileNumber()
    {

        if(editMobileNo.getText().toString().trim().length() != 10)
        {
            editMobileNo.setError("Invalid Mobile Number");
            editMobileNo.requestFocus();
            return false;
        }

        else
        {
            layout_mobile_number.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validatePassword()
    {

        if(editPassword.getText().toString().trim().length() < 6)
        {
            editPassword.setError("Password must be minimum 6 characters");
            editPassword.requestFocus();
            return false;
        }

        else
        {
            layout_password.setErrorEnabled(false);
        }

        return true;
    }


    /*private boolean validateEmail()
    {

        boolean flag = android.util.Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString()).matches();

        if(!flag)
        {
            editEmail.setError("Invalid Email");
            editEmail.requestFocus();
            return false;
        }

        else
        {
            layout_email.setErrorEnabled(false);
        }

        return true;
    }*/


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        try
        {

            if (flag && code == 100)
            {

                btnConfirmationCode.setVisibility(View.VISIBLE);
                new RegisterUser(context, RegisterFragment.this).register(user);
                tvStatus.setText(String.valueOf("Registering ..."));
            }

            else if (flag && code == 200)
            {
                Intent callIntent = new Intent(context, DashboardActivity.class);
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(callIntent);
                getActivity().finish();
            }

            else
            {
                tvStatus.setText("");
                btnConfirmationCode.setVisibility(View.GONE);
                pBar.setVisibility(View.GONE);
                makeSnackbar(message);
            }
        }

        catch (Exception e)
        {

        }
    }
}