package com.illusivemen.patients;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import android.content.Context;

public class CurrentPatient {
	
	String FILENAME = "current_patient";
	String currentPatient;
	
	/**
	 * Store the currently selected patient id
	 */
	public void selectPatient(Context context, String currentPatient) {		
		
		FileOutputStream fos;
		try {
			fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(currentPatient.getBytes());
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	// retrieve the currently selected patient id from internal storage
	public String GetId(Context context) {
		try
			{					   
				  FileInputStream fis = context.openFileInput(FILENAME);  
			      InputStreamReader isr = new InputStreamReader(fis);
			      BufferedReader bufferedReader = new BufferedReader(isr);
			      currentPatient = bufferedReader.readLine();
			      fis.close();
			    }
			  catch (Exception ex)
			    {
				  ex.printStackTrace();
			    }
		return currentPatient;
		}

}
