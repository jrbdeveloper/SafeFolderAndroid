package com.coretech.safefolder.safefolder;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EncryptActivity extends Activity {

    ArrayAdapter<String> emailListViewAdapter;
	SafeFolder application = (SafeFolder) getApplicationContext();

    /**
     * First to be called when the Activity starts; only called once
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

		final TextView emailTextBox = (TextView) findViewById(R.id.editText);
        final ArrayList<String> emailAddressArray = new ArrayList<String>();
		final ListView emailListView = (ListView) findViewById(R.id.emailListView);
		emailListView.setSelection(-1);

        emailListViewAdapter = new ArrayAdapter<String>(EncryptActivity.this, android.R.layout.simple_expandable_list_item_1,emailAddressArray);

        Button addEmailButton = (Button) findViewById(R.id.add_email);
		Button removeEmailButton = (Button) findViewById(R.id.remove_email);
        Button sendViaButton = (Button) findViewById(R.id.send_via);
        Button encryptButton = (Button)findViewById(R.id.encrypt);

		final int[] emailListViewSelectedIndex = new int[1];
		emailListViewSelectedIndex[0] = -1;

        addEmailButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                String emailAddress = emailTextBox.getText().toString();
				emailTextBox.setText(""); // Clear the textbox

                // Take the text from the box and add it to the list view
                if(emailListViewSelectedIndex[0] < 0){
					emailAddressArray.add(emailAddress);
				}else{
					emailAddressArray.set(emailListViewSelectedIndex[0], emailAddress);
					emailListViewSelectedIndex[0] = -1;
				}

				Bind(emailListView);
            }
        });

		removeEmailButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				if(emailListViewSelectedIndex[0] > -1) {
					emailAddressArray.remove(emailListViewSelectedIndex[0]);
					emailListViewSelectedIndex[0] = -1;
					emailTextBox.setText(""); // Clear the textbox

					Bind(emailListView);
				}else {
					Toast.makeText(getApplicationContext(), "Please select an item to remove.", Toast.LENGTH_LONG).show();
				}
			}
		});

		sendViaButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				application.getEncryptService().EncryptFiles(EncryptActivity.this, application.getFileService().GetFileList(EncryptActivity.this), emailAddressArray);

				application.getEncryptService().setOnFinishedListener(new EncryptService.OnFinishedListener() {
					@Override
					public void onFinished() {

						application.getEmailSerivce().Send(EncryptActivity.this, application.getFileService().GetFileList(EncryptActivity.this), emailAddressArray);
					}
				});
				//Close();
			}
		});

        encryptButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
			String response = application.getEncryptService().EncryptFiles(EncryptActivity.this, application.getFileService().GetFileList(EncryptActivity.this), emailAddressArray);
            }
        });

		emailListView.setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String selectedText = parent.getItemAtPosition(position).toString();
				emailTextBox.setText(selectedText);
				emailListViewSelectedIndex[0] = position;
			}
		});
    }

	/**
	 * Method that binds the array adapter to the list view control
	 * @param emailListView
	 */
	private void Bind(ListView emailListView) {
		emailListView.setAdapter(emailListViewAdapter);
		emailListViewAdapter.notifyDataSetChanged();
	}

	/**
     * Called after onCreate() and before onResume()
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Testing", "On Start .....");
		//CheckForSafeFiles();
    }

    /**
     * Called after onStart() as a result of onPause()
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Testing", "On Resume .....");
		//CheckForSafeFiles();
    }

    /**
     * Called after the Activity is running and before onStop()
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Testing", "On Pause .....");
    }

    /**
     * Called before onDestroy()
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Testing", "On Stop .....");
    }

    /**
     * Called as a result of onStop()
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("Testing", "On Restart .....");
    }

    /**
     * Called just before the activity is shut down
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Testing", "On Destroy .....");
    }

    private void Close(){
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.encrypt, menu);
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
