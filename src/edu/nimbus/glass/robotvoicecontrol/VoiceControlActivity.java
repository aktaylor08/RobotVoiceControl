package edu.nimbus.glass.robotvoicecontrol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;



import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketHandler;
 

import android.os.Bundle;
import android.app.Activity;
import android.speech.RecognizerIntent;
import android.util.Log;


public class VoiceControlActivity extends Activity {
	/** This line must be updated to ensure that the glass connects to the correct webserver that is running ROS */
	public final static String HOST_ADDRESS = "ws://10.214.33.96:9090";
    @Override
    
    
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume(){
    	super.onResume();
    	String command = "";
    	ArrayList<String> voiceResults = getIntent().getExtras()
    	        .getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
    	
    	for(int i=0;i< voiceResults.size(); i++){
    		Log.d("Voice Results", voiceResults.get(i));
    		command = voiceResults.get(i);
    	}
    	sendMessage(command);
    }
   
    /**
     * Send the command to the ROS Server on the specilized topic and wait for the reply back.
     * @param command
     */
    private void sendMessage(String command) {

		final String url = HOST_ADDRESS;
		Log.d("SENDING REQUEST", command);
		final WebSocketConnection mConnection = new WebSocketConnection();
		final String to_send = command;

		try{
			Log.d("Socket", "Atempting connection");
			mConnection.connect(url, new WebSocketHandler(){

				@Override
				public void onOpen() {
					//send the request
					JSONObject jsonRequest = new JSONObject();


					try {
						
						//Create the object and call it correctly using the API for the rosbridge server.
						jsonRequest.put("op", "call_service");
						jsonRequest.put("service", "/glass_voice_command");
						
						JSONObject args = new JSONObject();
						args.put("command", to_send);
						
						jsonRequest.put("args", args);
						Log.d("SENDING COMMAND", jsonRequest.toString());

						mConnection.sendTextMessage(jsonRequest.toString());


					} catch(Exception e) {

					}
					Log.d("Main Connection", "Status: Connected to " + url);

				}

				@Override
				public void onTextMessage(String payload) {
					Log.d("Main Payload", payload);
					//We got a message back from the server so lets create the cards for selection.
					
							
					mConnection.disconnect();
					finish();
					
				}

				@Override
				public void onClose(int code, String reason) {
						//DOn't need to do anything.
				}

			});
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}
    
}
