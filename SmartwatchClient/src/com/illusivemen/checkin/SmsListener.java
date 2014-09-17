package com.illusivemen.checkin;

import com.illusivemen.checkin.PatientCheckIn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsListener extends BroadcastReceiver {

    String msgBody;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
			
			Intent activityIntent = new Intent(context, PatientCheckIn.class);
		    activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    context.startActivity(activityIntent);		    
		    
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
	           
	                }
	            }catch(Exception e){
//	                        Log.d("Exception caught",e.getMessage());
	            }
	        }
	    };		
	}
}

