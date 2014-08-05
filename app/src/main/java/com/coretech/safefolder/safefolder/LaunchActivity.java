package com.coretech.safefolder.safefolder;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.coretech.safefolder.safefolder.entities.User;
import com.encrypticslibrary.api.response.EncrypticsResponseCode;

import java.util.List;

public class LaunchActivity extends Activity {

	//region Private Members
	private boolean _needToDetermineWhatToDo = true;
	//endregion

	//region Constructor
	public LaunchActivity(){
		SafeFolder.Instance().setCurrentActivity(this);
	}
	//endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

		if(_needToDetermineWhatToDo){
			DetermineWhatToDo();
		}

		Button registerButton = (Button) findViewById(R.id.register_button);
		Button signInButton = (Button) findViewById(R.id.sign_in_button);

		final EditText usernameText = (EditText) findViewById(R.id.usernameText);
		final EditText passwordText = (EditText) findViewById(R.id.passwordText);
		final CheckBox rememberMeCheck = (CheckBox) findViewById(R.id.remember_me_check);

		signInButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){

				String username = usernameText.getText().toString();
				String password = passwordText.getText().toString();

				User user = new User(username, password);
				SafeFolder.Instance().User(user);

				if(rememberMeCheck.isChecked()){
					SafeFolder.Instance().User().Account().setUsername(username);
					SafeFolder.Instance().User().Account().setPassword(password);
				}

				EncrypticsResponseCode authenticateResponse = SafeFolder.Instance().User().Account().Authenticate();

				if(authenticateResponse == EncrypticsResponseCode.SUCCESS){
					SafeFolder.Instance().Close();
				}else{
					Toast.makeText(SafeFolder.Instance().getApplicationContext(), "Error Logging In", Toast.LENGTH_LONG);
				}
			}
		});

		registerButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				ShowRegistrationActivity();
			}
		});
    }

	@Override
	protected void onStart() {
		super.onStart();

		if(SafeFolder.Instance().hasFiles()){
			//DetermineWhatToDo();
		}
	}

	private void DetermineWhatToDo(){
		int count = 0;
		SafeFolder.Instance().File().Collection = SafeFolder.Instance().File().GetCollection();
		int originalListSize = SafeFolder.Instance().File().Collection.size();

		if(originalListSize > 0) {
			for(String item : SafeFolder.Instance().File().Collection){
				if(item.contains(".safe")){
					count ++;
				}
			}
		}

		if(originalListSize == 0){ // we have no files; check for log on
			//ShowFileManager();
			if(SafeFolder.Instance().User().IsLoggedIn()){
				SafeFolder.Instance().Close();
			}

			_needToDetermineWhatToDo = false;
		}else if(count == 0 && originalListSize > 0){ // We have files in the list but none are .safe files
			ShowEncryptActivity();
		}else if(count >= originalListSize && originalListSize > 0){ // we have files and all are .safe
			ShowDecryptActivity();
		}else {
			Toast.makeText(LaunchActivity.this, "You have mixed files...",  Toast.LENGTH_LONG).show();
		}
	}

	private void ShowEncryptActivity(){
		Intent showEncrypt = new Intent(this, EncryptActivity.class);
		startActivity(showEncrypt);
	}

	private void ShowDecryptActivity(){
		Intent showDecrypt = new Intent(this, DecryptActivity.class);
		startActivity(showDecrypt);
	}

	private void ShowRegistrationActivity(){
		Intent showRegister = new Intent(this, RegisterActivity.class);
		startActivity(showRegister);
	}

	/**
	 * Method to show the user a file manager for selecting files
	 * Not used currently used - john bales 7/29/14
	 */
	private void ShowFileManager(){

		Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
		PackageManager packageManager = getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);

		if(activities.size() > 0){
			//intent.setAction(Intent.ACTION_SEND_MULTIPLE);
			intent.putExtra("CONTENT_TYPE", "*/*");
			startActivityForResult(intent, 1);
		}else{
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			//intent.setAction(Intent.ACTION_SEND_MULTIPLE);
			Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
			intent.setDataAndType(uri, "*/*");
			startActivity(Intent.createChooser(intent, "Open folder"));
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.launch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
