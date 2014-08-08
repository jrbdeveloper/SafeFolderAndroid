package com.coretech.safefolder.safefolder;

import android.app.Activity;
import android.app.ProgressDialog;
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
import java.util.List;

public class LaunchActivity extends Activity {

	//region Private Members
	private boolean _needToDetermineWhatToDo = true;
	ProgressDialog _progress;
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

		_progress = new ProgressDialog(this);
		_progress.setMessage("Authenticating, please wait...");
		_progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		if(_needToDetermineWhatToDo){
			DetermineWhatToDo();
		}

		Button registerButton = (Button) findViewById(R.id.register_button);
		final Button signInButton = (Button) findViewById(R.id.sign_in_button);

		final EditText usernameText = (EditText) findViewById(R.id.usernameText);
		final EditText passwordText = (EditText) findViewById(R.id.passwordText);
		final CheckBox rememberMeCheck = (CheckBox) findViewById(R.id.remember_me_check);

		signInButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				_progress.setIndeterminate(true);
				_progress.show();
				signInButton.setEnabled(false);

				String username = usernameText.getText().toString();
				String password = passwordText.getText().toString();
				boolean rememberMe = rememberMeCheck.isChecked();

				if(rememberMeCheck.isChecked()){
					SafeFolder.Instance().User().Account().setUsername(username);
					SafeFolder.Instance().User().Account().setPassword(password);
					SafeFolder.Instance().User().Account().setRememberMe(rememberMe);
				}else{
					SafeFolder.Instance().User().Account().setUsername(username);
					SafeFolder.Instance().User().Account().setPassword(password);

					SafeFolder.Instance().User().Username(username);
					SafeFolder.Instance().User().Password(password);
				}

				try {
					SafeFolder.Instance().User().Account().Authenticate(_progress);

					if(!SafeFolder.Instance().User().IsLoggedIn()){
						Toast.makeText(SafeFolder.Instance().getApplicationContext(), "Error Logging In", Toast.LENGTH_LONG);
					}

					signInButton.setEnabled(true);

				} catch (Exception e) {
					e.printStackTrace();
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
		SafeFolder.Instance().File().Collection(SafeFolder.Instance().File().GetCollection());
		int originalListSize = SafeFolder.Instance().File().Collection().size();

		if(originalListSize > 0) {
			for(String item : SafeFolder.Instance().File().Collection()){
				if(item.contains(".safe")){
					count ++;
				}
			}
		}

		if(originalListSize == 0){ // we have no files; check for log on
			//ShowFileManager();
			if(SafeFolder.Instance().User().IsLoggedIn()){
				SafeFolder.Instance().Close();
				_needToDetermineWhatToDo = false;
			}

			_needToDetermineWhatToDo = true;
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
