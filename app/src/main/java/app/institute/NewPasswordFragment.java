package app.institute;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class NewPasswordFragment extends DialogFragment
{

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


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_password, null);

        viewHolder = new ViewHolder();
        viewHolder.populate(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
              .setPositiveButton(R.string.Save, null)
              .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

                  public void onClick(DialogInterface dialog, int id)
                  {

                      NewPasswordFragment.this.getDialog().cancel();

                      if (listener != null)
                      {
                          listener.onDialogNegativeClick(NewPasswordFragment.this);
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

                        String new_password = getViewHolder().passwordEditText.getText().toString();

                        if (listener != null)
                        {
                          listener.onDialogPositiveClick(NewPasswordFragment.this, new_password);
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

        String new_password = getViewHolder().passwordEditText.getText().toString();

        if(TextUtils.isEmpty(new_password))
        {
            validData = false;
        }

        return validData;
    }


    private void showValidationErrorToast()
    {
      Toast.makeText(getActivity(), "Please enter new password", Toast.LENGTH_SHORT).show();
    }

    // endregion
    // region Interfaces

    public interface AddSchoolFragmentListener
    {
        void onDialogPositiveClick(DialogFragment dialog, String new_password);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    // endregion
    // region Inner classes

    static class ViewHolder
    {

        EditText passwordEditText;

        public void populate(View v)
        {
            passwordEditText = (EditText) v.findViewById(R.id.editPassword);
        }
    }
}