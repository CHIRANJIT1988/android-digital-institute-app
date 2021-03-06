package app.institute;

import android.content.Context;
import android.content.Intent;


public final class CommonUtilities
{

    /**
     * Tag used on log messages.
     */
    static final String TAG = "Specturm Eduventures Application";

    public static final String DISPLAY_MESSAGE_ACTION =
            "app.institute.DISPLAY_MESSAGE";

    public static final String EXTRA_MESSAGE = "data";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */


    public static void displayMessage(Context context, String message)
    {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}