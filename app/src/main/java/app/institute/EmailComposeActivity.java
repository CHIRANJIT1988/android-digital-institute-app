package app.institute;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import app.institute.helper.OnTaskCompleted;
import app.institute.model.Branch;
import app.institute.network.InternetConnectionDetector;


public class EmailComposeActivity extends AppCompatActivity implements View.OnClickListener, OnTaskCompleted
{

    private EditText edit_subject, edit_message;
    private TextInputLayout layout_subject, layout_message;
    private Button button_send;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_mail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Compose Message");

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById();

        button_send.setOnClickListener(this);
    }


    private void findViewById()
    {

        edit_subject = (EditText) findViewById(R.id.editSubject);
        edit_message = (EditText) findViewById(R.id.editMessage);

        layout_subject = (TextInputLayout) findViewById(R.id.input_layout_subject);
        layout_message = (TextInputLayout) findViewById(R.id.input_layout_message);

        button_send = (Button) findViewById(R.id.btnSend);
    }


    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {

            case R.id.btnSend:

                if(!new InternetConnectionDetector(this).isConnected())
                {
                    Toast.makeText(getApplicationContext(), "Internet Connection Failure", Toast.LENGTH_LONG).show();
                }

                if(validateSubject() && validateMessage())
                {

                    Branch branch = (Branch) getIntent().getSerializableExtra("BRANCH");

                    String physics_email = "bastavsaikia@gmail.com";
                    String chemistry_email = "avishek84030@gmail.com";
                    String biology_email = "lakshmi88borah@gmail.com";
                    String math_email = "basubhattacharya@gmail.com";


                    if(branch._subject.subject_code.equals("1"))
                    {
                        String [] TO = { "bardhan.jit@gmail.com", physics_email };
                        sendEmail(TO, edit_subject.getText().toString(), edit_message.getText().toString());
                    }

                    else if(branch._subject.subject_code.equals("2"))
                    {
                        String [] TO = { "bardhan.jit@gmail.com", chemistry_email };
                        sendEmail(TO, edit_subject.getText().toString(), edit_message.getText().toString());
                    }

                    else if(branch._subject.subject_code.equals("3"))
                    {
                        String [] TO = { "bardhan.jit@gmail.com", math_email };
                        sendEmail(TO, edit_subject.getText().toString(), edit_message.getText().toString());
                    }

                    else if(branch._subject.subject_code.equals("4"))
                    {
                        String [] TO = { "bardhan.jit@gmail.com", biology_email };
                        sendEmail(TO, edit_subject.getText().toString(), edit_message.getText().toString());
                    }
                }

                break;
        }
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
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean validateSubject()
    {

        if(edit_subject.getText().toString().trim().length() < 3)
        {
            edit_subject.setError("Subject must be at least 3 characters");
            edit_subject.requestFocus();
            return false;
        }

        else
        {
            layout_subject.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateMessage()
    {

        if(edit_message.getText().toString().trim().isEmpty())
        {
            edit_message.setError("Write Message");
            edit_message.requestFocus();
            return false;
        }

        else
        {
            layout_message.setErrorEnabled(false);
        }

        return true;
    }


    protected void sendEmail(String[] TO, String subject, String message)
    {

        Log.i("Send email", "");
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try
        {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //finish();
            Log.i("Finished sending email...", "");
        }

        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(EmailComposeActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}