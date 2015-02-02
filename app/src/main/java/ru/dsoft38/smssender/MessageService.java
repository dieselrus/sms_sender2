package ru.dsoft38.smssender;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by user on 28.01.2015.
 */
public class MessageService {

    String SENT      = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

    public MessageService(Context paramContext)
    {
        //this.mContext = paramContext;
        //this.mAccounts = new ArrayList();
        //this.mCurrentAccount = null;
    }


    //Метод отправки SMS сообщения
    public void SendSMS(String phone, String message)
    {
        SmsManager sms = SmsManager.getDefault();

        ArrayList<String> al_message = new ArrayList<String>();
        al_message = sms.divideMessage(message);

        ArrayList<PendingIntent> al_piSent = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> al_piDelivered = new ArrayList<PendingIntent>();

        for (int i = 0; i < al_message.size(); i++)
        {
            Intent sentIntent = new Intent(SENT);
            sentIntent.putExtra("PARTS", "Часть: "+i);
            sentIntent.putExtra("MSG", "Сообщение: "+al_message.get(i));
            //PendingIntent pi_sent = PendingIntent.getBroadcast(MessageService.this, i, sentIntent,
            //        PendingIntent.FLAG_UPDATE_CURRENT);
            //al_piSent.add(pi_sent);

            Intent deliveredIntent = new Intent(DELIVERED);
            deliveredIntent.putExtra("PARTS", "Часть: "+i);
            deliveredIntent.putExtra("MSG", "Сообщение: "+al_message.get(i));
            //PendingIntent pi_delivered = PendingIntent.getBroadcast(this, i, deliveredIntent,
            //        PendingIntent.FLAG_UPDATE_CURRENT);
            //al_piDelivered.add(pi_delivered);
        }
        sms.sendMultipartTextMessage(phone, null, al_message, al_piSent, al_piDelivered);
    }
}
