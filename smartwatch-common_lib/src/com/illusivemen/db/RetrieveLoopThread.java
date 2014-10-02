package com.illusivemen.db;


public class RetrieveLoopThread extends Thread {
	
	// database retrieval server connection
	private DBConn conn;
	
	boolean stopFlag = false;
	int interval;
	String[] params = null;
	OnLoopRetrievedListener listener;
	
	/**
	 * Create a new looping/retrieving thread.
	 * @param url source to retrieve
	 * @param interval how long in milliseconds to wait before starting again
	 */
	public RetrieveLoopThread(String url, int interval) {
		super();
		
		conn = new DBConn(url);
		this.interval = interval;
	}
	
	/**
	 * Constructor specifying parameters for the connection.
	 * @param url as above
	 * @param params POST parameters for the URL
	 * @param interval as above
	 */
	public RetrieveLoopThread(String url, String[] params, int interval) {
		this(url, interval);
		this.params = params;
	}
	
	/**
	 * Change the listener of events for this thread.
	 * @param listener the sole listener for this thread
	 */
	public void setListener(OnLoopRetrievedListener listener) {
		this.listener = listener;
	}
	
	/**
	 * Send the result to the listener.
	 * @param result URL retrieved contents 
	 */
	private void notifyListener(String result) {
		listener.onLocationRetrieved(result);
	}
	
	/**
	 * Main loop.
	 */
	@Override
	public void run() {
		while (!stopFlag) {
			
			if (params == null) {
				conn.execute();
			} else {
				conn.execute(params);
			}
			notifyListener(conn.getResult());
			
			// wait before running again
			nop();
		}
		
	}
	
	/**
	 * No operation for specified time between runs.
	 */
	private void nop() {
		try {
			Thread.sleep(interval);
		} catch (InterruptedException e) {
			System.out.println("Unexpected Interrupt");
			System.exit(0);
		}
	}
	
	/**
	 * Access to stop the thread safely.
	 */
	public void safeStop() {
		stopFlag = true;
	}
	
	/**
	 * Enables the ability to change parameters on an existing thread.
	 * @param params new POST parameters for retrieval
	 */
	public void setParams(String[] params) {
		this.params = params;
	}
}
