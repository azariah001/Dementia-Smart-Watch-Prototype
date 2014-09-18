package com.illusivemen.checkin;

import com.illusivemen.checkin.PatientCheckIn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsListener extends BroadcastReceiver {

    String msgBody;
    String confirmText = "Check-in Request";

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){					    
		    
	        Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
	        SmsMessage[] msgs = null;
	        String msg_from;
	        if (bundle != null){
	            //---retrieve the SMS message received---
	            try{
	                Object[] pdus = (Object[]) bundle.get("pdus");
	                msgs = new SmsMessage[pdus.length];
	                for(int i=0; i<msgs.length; i++){
	                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
	                    msg_from = msgs[i].getOriginatingAddress();
	                    msgBody = msgs[i].getMessageBody();
	                    
	                    CharSequence text = msgBody;
	                    int duration = Toast.LENGTH_LONG;

	                    Toast toast = Toast.makeText(context, text, duration);
	                    toast.show();
	                    
	                    //---add extra conditions below based on the contents of a text
	                    
	                    if (msgBody.equals(confirmText)) {
	                    	Intent activityIntent = new Intent(context, PatientCheckIn.class);
	    	    		    activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	    		    context.startActivity(activityIntent);
	                    }
	               }                
	    		    
	            }catch(Exception e){
//	                        Log.d("Exception caught",e.getMessage());
	            }
	        }
	    };		
	}
}

