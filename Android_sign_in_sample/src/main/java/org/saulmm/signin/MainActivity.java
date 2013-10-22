package org.saulmm.signin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;

import static android.util.Log.d;
import static android.util.Log.e;

public class MainActivity extends Activity implements
		View.OnClickListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {



	private static final int REQUEST_CODE_RESOLVE_ERR = 101;
	private static final int ERROR = 102;

	private ProgressDialog connectionProgressDialog;
	private PlusClient googlePlusClient;
	private ConnectionResult connectionResult;
	private SignInButton googleSignInButton;
	private MenuItem logOutItem;
	private MenuItem revokeItem;
	private TextView jsonOutput;


	/**
	 * When the activity creates, load all gui elements and
	 * init the configuration of the google+ client.
	 */
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initGUI();
		initGPlusClient();
	}


	/**
	 * Inflates the action bar menu with the selected menu layout
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		logOutItem  = menu.findItem(R.id.menu_log_out);
		revokeItem = menu.findItem(R.id.menu_reboke);

		logOutItem.setEnabled(false);
		revokeItem.setEnabled(false);

		return true;
	}


	@Override
	public boolean onOptionsItemSelected (MenuItem item) {

			switch (item.getItemId()) {
				case R.id.menu_log_out:
					logOut();
					break;

				case R.id.menu_reboke:
					revokePermissions();
					break;
			}

		return super.onOptionsItemSelected(item);
	}


	private void logOut () {
		d("[DEBUG] org.saulmm.signin.MainActivity.logOut ", "Is connected: "+googlePlusClient.isConnected());
		if(googlePlusClient.isConnected()) {
			googlePlusClient.clearDefaultAccount();
			googlePlusClient.disconnect();
			googlePlusClient.connect();

			Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show();

			if(getActionBar() != null) {
				logOutItem.setEnabled(false);
				revokeItem.setEnabled(false);
			}

			jsonOutput.setText("");
			googleSignInButton.setEnabled(true);
		}
	}


	private void revokePermissions () {
		if(googlePlusClient.isConnected())
			googlePlusClient.clearDefaultAccount();
			googlePlusClient.revokeAccessAndDisconnect(new PlusClient.OnAccessRevokedListener() {
				@Override
				public void onAccessRevoked (ConnectionResult connectionResult) {
					Toast.makeText(MainActivity.this ,"Revoked access", Toast.LENGTH_LONG).show();
			}
		});

		logOut();
	}


	/**
	 *  When the user ends withe the google+ configuration options this method
	 *  will be automatically fired. If everything goes good, this last connection try
	 *  should ends with success and the user would be connected with google+, and the
	 *  onConnected method will be fired.
	 */
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
			connectionResult = null;
			googlePlusClient.connect();
		}

	}


	/**
	 * When the activity starts, makes a try to connect with google+
	 */
	@Override
	protected void onStart () {
		super.onStart();
		googlePlusClient.connect();
	}


	/**
	 * Init all gui components
	 */
	private void initGUI () {
		setContentView(R.layout.activity_main);
		jsonOutput = (TextView) findViewById(R.id.json_output);
		connectionProgressDialog = new ProgressDialog(this);
		connectionProgressDialog.setCancelable(false);
		googleSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
		googleSignInButton.setOnClickListener(this);

		if(getActionBar() != null) {
			getActionBar().setDisplayShowHomeEnabled(true);
			getActionBar().setDisplayShowTitleEnabled(true);
		}
	}


	/**
	 * Configure the google plus client object
	 */
	private void initGPlusClient () {
		googleSignInButton = (SignInButton)findViewById(R.id.sign_in_button);

		PlusClient.Builder gPlusBuilder = new PlusClient.Builder(
				this, // Connection callbacks
				this, // Connection failed listener
				this); // connection failed listener

		// All activity types in https://developers.google.com/+/api/moment-types/
		gPlusBuilder.setVisibleActivities(
						"http://schemas.google.com/AddActivity", // Is the generic fallback type. Use it when no other app activity type is appropriate.
						"http://schemas.google.com/ListenActivity"); // Use this type when your user listens to a song, audio book, or other type of audio recording.


		googlePlusClient = gPlusBuilder.build();
	}


	/**
	 * Disconnect the user of google+ when the activity stops
	 */
	@Override
	protected void onStop () {
		super.onStop();
		googlePlusClient.disconnect();
	}


	@Override
	public void onClick (View v) {

		switch (v.getId()) {
			case R.id.sign_in_button:
				sigInWithGoogle();
				break;
		}

	}


	/**
	 *  Check if the user is not connected, if not may the connection result
	 *  can be null, if that happens, we will show a progress dialog until it comes.
	 *
	 *  If the connection result is not null we will call to the startResolutionForResult()
	 *  method, this method will show to the user the necessary options to resolve possible
	 *  errors detected in the connection attemp with Google+.
	 */
	private void sigInWithGoogle () {

		if(!googlePlusClient.isConnected()) {
			if(connectionResult == null) {
				connectionProgressDialog.show();

			} else {
				try {
					connectionResult.startResolutionForResult(MainActivity.this, REQUEST_CODE_RESOLVE_ERR);

				} catch(IntentSender.SendIntentException e) {
					e("[ERROR] org.saulmm.signin.MainActivity.sigInWithGoogle ", "" + e.getMessage());
					connectionResult = null;
					googlePlusClient.connect();
				}

			}
		}
	}


	@Override
	public void onConnected (Bundle bundle) {
		connectionProgressDialog.dismiss();

		fillOutputTextView(googlePlusClient.getCurrentPerson());

		d("[DEBUG] org.saulmm.signin.MainActivity.onConnected ", ":"+googlePlusClient.getCurrentPerson().toString());
		d("[DEBUG] org.saulmm.signin.MainActivity.onConnected ", "Connected");

		if(getActionBar() != null) {
			logOutItem.setEnabled(true);
			revokeItem.setEnabled(true);
		}

		googleSignInButton.setEnabled(false);
	}



	@Override
	public void onDisconnected () {
		Toast.makeText(this, getString(R.string.now_disconnected), Toast.LENGTH_LONG).show();
		d("[DEBUG] org.saulmm.signin.MainActivity.onDisconnected ", "Disconnected");
		if(getActionBar() != null) {
			logOutItem.setEnabled(false);
			revokeItem.setEnabled(false);
		}

		recreate();
	}


	private void fillOutputTextView (Person currentPerson) {
		String parsedJson = beautifyJSon(currentPerson.toString());

		Log.d("[DEBUG] org.saulmm.signin.MainActivity.fillOutputTextView ", "Result: " + parsedJson);
		jsonOutput.setText(parsedJson);

	}


	/**
	 * Beautifies the given json
	 */
	private String beautifyJSon (String json) {
		String parsedJson = "";
		int childLevel = 0;

		for (int i = 0; i < json.length(); i++) {
			String letter = ""+json.charAt(i);

			if(letter.equals("}") || letter.equals("]")) {
				parsedJson += "\n";

				for (int j = 0; j < childLevel -1; j++) {
					parsedJson += "\t";
				}
				childLevel--;
			}


			parsedJson += letter;

			if(letter.equals("{")) {
				childLevel++;
			}

			if (letter.equals(",") || letter.equals("{") || letter.equals("[")) {
				parsedJson += "\n";

				for (int j = 0; j < childLevel; j++) {
					parsedJson += "\t";
				}
			}
		}
		return parsedJson;
	}


	@Override
	public void onConnectionFailed (ConnectionResult connectionResult) {
		this.connectionResult = connectionResult;
	}
}