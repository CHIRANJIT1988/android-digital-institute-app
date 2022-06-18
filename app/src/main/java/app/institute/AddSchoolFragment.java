package app.institute;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import app.institute.model.School;


public class AddSchoolFragment extends DialogFragment implements AdapterView.OnItemSelectedListener
{

    private Context context;

    private ViewHolder viewHolder;

    private ViewHolder getViewHolder()
    {
        return viewHolder;
    }

    AddSchoolFragmentListener listener;

    public void setListener(AddSchoolFragmentListener listener)
    {
        this.listener = listener;
    }


    public AddSchoolFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public AddSchoolFragment(Context context)
    {
        this.context = context;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_school, null);

        viewHolder = new ViewHolder();
        viewHolder.populate(view);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton(R.string.Save, null)
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id)
                    {
                        AddSchoolFragment.this.getDialog().cancel();

                        if (listener != null)
                        {
                            listener.onDialogNegativeClick(AddSchoolFragment.this);
                        }
                    }
                });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface)
            {

                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view)
                {

                    if (dataIsValid())
                    {

                        School school = new School();
                        school.school_name = getViewHolder().schoolNameEditText.getText().toString();
                        school.state = getViewHolder().getSchoolStateEditText.getText().toString();

                        if (listener != null)
                        {
                            listener.onDialogPositiveClick(AddSchoolFragment.this, school);
                            dialog.dismiss();
                        }
                    }

                    else
                    {
                        showValidationErrorToast();
                    }
                }
            });
          }
        });

        return dialog;
    }


    // endregion
    // region Private

    private boolean dataIsValid()
    {

        boolean validData = true;

        String school_name = getViewHolder().schoolNameEditText.getText().toString();
        String school_state = getViewHolder().getSchoolStateEditText.getText().toString();

        if(TextUtils.isEmpty(school_state))
        {
            validData = false;
        }

        if(TextUtils.isEmpty(school_name))
        {
            validData = false;
        }

        return validData;
    }


    private void showValidationErrorToast()
    {
        Toast.makeText(getActivity(), "Invalid Data", Toast.LENGTH_SHORT).show();
    }


    // endregion
    // region Interfaces

    public interface AddSchoolFragmentListener
    {
        void onDialogPositiveClick(DialogFragment dialog, School school);
        void onDialogNegativeClick(DialogFragment dialog);
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

    // endregion
    // region Inner classes

    class ViewHolder
    {

        EditText schoolNameEditText, getSchoolStateEditText;

        public void populate(View v)
        {

            schoolNameEditText = (EditText) v.findViewById(R.id.fragment_add_school_name);
            getSchoolStateEditText = (EditText) v.findViewById(R.id.fragment_add_school_state);
        }
    }
}