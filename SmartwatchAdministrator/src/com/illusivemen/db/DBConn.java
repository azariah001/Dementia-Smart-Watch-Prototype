package com.illusivemen.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DBConn {
	
	// input variables
	private String script;
	private String[] parameters;
	
	// output variables
	private String result;
	
	/**
	 * Constructor for a connection only receiving data.
	 */
	public DBConn(String script) {
		super();
		
		// save instance variables
		this.script = script;
		this.parameters = null;
	}
	
	/**
	 * Constructor for a connection expecting posted data.
	 */
	public DBConn(String script, String[] parameters) {
		super();
		
		// save instance variables
		this.script = script;
		this.parameters = parameters;
	}
	
	/**
	 * Main processing method.
	 * Internal method which executes the connection process.
	 */
	private void makeRequest() {
		// processing variables
		URL url;
		HttpURLConnection connection = null;
		InputStream iStream = null;
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		
		try {
			// setup and make connection
			url = new URL(script);
			connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            
            // setup output reader
            iStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(iStream));
            
            // read in output
            String output = "";
            while((output = reader.readLine()) != null) {
                sb.append(output);
            }

        } catch (MalformedURLException e) {
        	// thrown by URL constructor
        	System.out.println("DB Internal Error");
        	
        } catch (IOException e) {
        	// general network error
        	System.out.println("DB Network Error");
        	
        } finally {
        	// close order opposite to open order
        	// must have separate try blocks to ensure everything gets closed
        	
        	try {
				reader.close();
			} catch (IOException e) {
				// general network error
			} catch (NullPointerException e) {
				// never initialized
			}
        	
        	try {
				iStream.close();
			} catch (IOException e) {
				// general network error
			} catch (NullPointerException e) {
				// never initialized
			}
        	
        	try {
        		connection.disconnect();
        	} catch (NullPointerException e) {
        		// never initialized
        	}
        }
		
		// store resulting output
        this.result = sb.toString();
	}
	
	// ----------- BEGIN PUBLIC METHODS ----------
	
	public void execute() {
		makeRequest();
	}
	
	public String getResult() {
		return result;
	}
}
