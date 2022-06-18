package app.institute.alert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import app.institute.helper.OnAlertButtonClick;


public class CustomAlertDialog
{

	private Context context;
	private OnAlertButtonClick listener;
	
	
	public CustomAlertDialog(Context context, OnAlertButtonClick listener)
	{
		this.listener = listener;
		this.context = context;
	}


	public void showConfirmationDialog(String title, String message, String positive_button, String negative_button)
	{

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		alertDialogBuilder.setMessage(message).setTitle(title).setCancelable(false) // set dialog message

				// Yes button click action
				.setPositiveButton(positive_button, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						listener.onAlertButtonClick(true, 200);
					}
				})

				// No button click action
				.setNegativeButton(negative_button, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});


		AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog

		alertDialog.show(); // show it
	}


	public void showOKDialog(String title, String message, String positive_button)
	{

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// Setting Dialog Title
		alertDialogBuilder.setTitle(title);

		alertDialogBuilder.setMessage(message).setCancelable(false) // set dialog message

				// Yes button click action
				.setPositiveButton(positive_button, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						listener.onAlertButtonClick(true, 200);
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog

		alertDialog.show(); // show it
	}
}