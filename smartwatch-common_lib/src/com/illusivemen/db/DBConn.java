package com.illusivemen.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class DBConn {
	
	private final static String SERVER = "http://agile.azarel-howard.me";
	// input variables
	private String script;
	private String[] parameters;
	
	// output variables
	private String result;
	
	/**
	 * Constructor. An instance is a script connector,
	 * main constant is which script to connect to.
	 */
	public DBConn(String script) {
		super();
		
		// save instance variables
		this.script = SERVER + script;
		this.parameters = null;
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
			
			if (parameters == null) {
				// plain retrieve HTTP
				connection.connect();
			} else {
				// write POST data
				writePostData(connection);
			}
			
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
	
	/**
	 * If post parameters need to be written, this method will post them.
	 * @param connection the connection to the HTTP (PHP) server
	 */
	public void writePostData(HttpURLConnection connection) {
		// writer object
		OutputStreamWriter outputStreamWriter = null;
		
		try {
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			
			// open connection
			outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
			outputStreamWriter.write(stringJoin("&", parameters));
		
		} catch (ProtocolException e) {
			// setRequestMethod throws
			e.printStackTrace();
		
		} catch (IOException e) {
			// general network error
			e.printStackTrace();
		
		} finally {
			// close writer object
			
			try {
				outputStreamWriter.flush();
				outputStreamWriter.close();
			} catch (IOException e) {
				// already closed/network error
			} catch (NullPointerException e) {
				// never initialized
			}
			
		}
	}
	
	/**
	 * Internal implementation of StringUtils.join() method.
	 * Joins a String[] with a separator string in between.
	 * @param separator the string placed between the array items
	 * @param stringArray array of string to join
	 * @return joined string with separator in between
	 */
	private String stringJoin(String separator, String[] stringArray) {
		StringBuilder builder = new StringBuilder();
		
		// loop through array, join strings, add separator before but not the first time
		for (int string = 0; string < stringArray.length; string++) {
			if (string > 0) {
				// only add separator if not first item
				builder.append(separator);
			}
			builder.append(stringArray[string]);
		}
		
		return builder.toString();
	}
	
	// ----------- BEGIN PUBLIC METHODS ----------
	
	/**
	 * Make the connection to the script to retrieve only.
	 */
	public void execute() {
		
		// clear any POST items before connecting
		this.parameters = null;
		makeRequest();
	}
	
	/**
	 * Make the connection to the script and POST/retrieve.
	 * @param parameters array of post variables such as "key1=value1" and "key2=value2"
	 */
	public void execute(String[] parameters) {
		
		// set the parameters for this connection
		this.parameters = parameters;
		makeRequest();
	}
	
	/**
	 * Execute with the same setup (parameters) as before.
	 * @return
	 */
	public void reExecute() {
		
		// parameters remain the same
		makeRequest();
	}
	
	/**
	 * Returns the output of the request.
	 * @return HTTP output
	 */
	public String getResult() {
		
		// result is saved post-execution
		return result;
	}
}
