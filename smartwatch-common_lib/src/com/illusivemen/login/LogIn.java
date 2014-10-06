package com.illusivemen.login;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import android.content.Context;


/**
 * login to the app so that we can track multiple patients
 * TODO - Password validation
 * @author Daniel Luckhurst
 *
 */
public class LogIn {
	
	public final static String SMARTWATCH_CLIENT = "Smartwatch Client";
	public final static String SMARTWATCH_ADMIN = "Smartwatch Administrator";
	String FILENAME;
	String login;	
	
	/**
	 * save the carer id to the device's internal storage
	 * can be accessed by any class
	 * private to this application
	 */
	
	public void LogInToApp(Context context, String id) {
		
		if (getApplicationName(context).equals(SMARTWATCH_CLIENT)) {
			   FILENAME = "patient_login_file";
		   } 
		   
		if (getApplicationName(context).equals(SMARTWATCH_ADMIN)) {
			   FILENAME = "admin_login_file";
		   } 
		
		try {
		FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);		
		fos.write(id.getBytes());
		fos.close();
		}
		   catch (Exception ex)
		{
			   ex.printStackTrace();
		 	}
	}
			
	// retrieve the id from internal storage
	public String GetId(Context context) {
		try
			{
			   if (getApplicationName(context).equals(SMARTWATCH_CLIENT)) {
				   FILENAME = "patient_login_file";
			   } 
			   
			   if (getApplicationName(context).equals(SMARTWATCH_ADMIN)) {
				   FILENAME = "admin_login_file";
			   } 
				   
			   FileInputStream fis = context.openFileInput(FILENAME);  
		       InputStreamReader isr = new InputStreamReader(fis);
		       BufferedReader bufferedReader = new BufferedReader(isr);
		       login = bufferedReader.readLine();
		       fis.close();
		     }
		   catch (Exception ex)
		     {
			   ex.printStackTrace();
		     }
		return login;
		}		

	
	public static String getApplicationName(Context context) {
	    int stringId = context.getApplicationInfo().labelRes;
	    return context.getString(stringId);
	}
}
