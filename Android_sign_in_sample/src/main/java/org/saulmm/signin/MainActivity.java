package org.saulmm.signin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;

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


	/**
	 * When the activity creates, load all gui elements and
	 * init the configuration of the google+ client
	 */
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initGUI();
		initGPlusClient();
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
		connectionProgressDialog = new ProgressDialog(this);
		googleSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
		googleSignInButton.setOnClickListener(this);
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
					Log.e("[ERROR] org.saulmm.signin.MainActivity.sigInWithGoogle ", "" + e.getMessage());
					connectionResult = null;
					googlePlusClient.connect();
				}

			}
		}
	}


	@Override
	public void onConnected (Bundle bundle) {
		connectionProgressDialog.dismiss();
		Toast.makeText(this, getString(R.string.now_connected), Toast.LENGTH_LONG).show();

	}


	@Override
	public void onDisconnected () {
		Toast.makeText(this, getString(R.string.now_disconnected), Toast.LENGTH_LONG).show();

	}


	@Override
	public void onConnectionFailed (ConnectionResult connectionResult) {
		this.connectionResult = connectionResult;
	}
}