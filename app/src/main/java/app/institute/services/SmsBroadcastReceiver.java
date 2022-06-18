package app.institute.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;


public class SmsBroadcastReceiver extends BroadcastReceiver
{


    public static final String SMS_BUNDLE = "pdus";


    public void onReceive(Context context, Intent intent)
    {

        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null)
        {

            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);

            //String smsMessageStr = "";

            for (int i = 0; i < sms.length; ++i)
            {

                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress().toString();

                //smsMessageStr += "SMS From: " + address + "\n";
                //smsMessageStr += smsBody + "\n";


                if(address.contains("SPCTRM") && smsBody.contains("OTP"))
                {

                    // A custom Intent that will used as another Broadcast
                    Intent in = new Intent("SmsMessage.intent.MAIN");
                    in.putExtra("get_msg", smsBody);

                    //Log.v("REGISTRATION: ", "" + address);

                    //You can place your check conditions here(on the SMS or the sender)
                    //and then send another broadcast
                    context.sendBroadcast(in);
                }
            }


            //Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();

            //this will update the UI with message
            //SmsActivity inst = SmsActivity.instance();
            //inst.updateList(smsMessageStr);

            // This is used to abort the broadcast and can be used to silently
            // process incoming message and prevent it from further being
            // broadcasted. Avoid this, as this is not the way to program an app.
            // this.abortBroadcast();
        }
    }
}