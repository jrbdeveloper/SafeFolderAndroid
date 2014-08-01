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

public class EncryptActivity extends Activity {

	//region Private members
    private ArrayAdapter<String> _emailListViewAdapter;
	private SafeFolder _application;
	//endregion

	//region Constructor
	public EncryptActivity(){
		if(_application == null){
			_application = SafeFolder.getInstance();
			_application.setCurrentActivity(this);
		}
	}
	//endregion

    /**
     * First to be called when the Activity starts; only called once
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

		final TextView emailTextBox = (TextView) findViewById(R.id.editText);
		final ListView emailListView = (ListView) findViewById(R.id.emailListView);
		emailListView.setSelection(-1);

		_emailListViewAdapter = new ArrayAdapter<String>(_application.getCurrentActivity(), android.R.layout.simple_expandable_list_item_1, _application.EmailList);

        Button addEmailButton = (Button) findViewById(R.id.add_email);
		Button removeEmailButton = (Button) findViewById(R.id.remove_email);
        Button sendViaButton = (Button) findViewById(R.id.send_via);
        Button encryptButton = (Button)findViewById(R.id.encrypt);

		final int[] emailListViewSelectedIndex = new int[1];
		emailListViewSelectedIndex[0] = -1;

        addEmailButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                String emailAddress = emailTextBox.getText().toString();
				emailTextBox.setText("");

                // Take the text from the box and add it to the list view
                if(emailListViewSelectedIndex[0] < 0){
					_application.EmailList.add(emailAddress);
				}else{
					_application.EmailList.set(emailListViewSelectedIndex[0], emailAddress);
					emailListViewSelectedIndex[0] = -1;
				}

				Bind(emailListView);
            }
        });

		removeEmailButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				if(emailListViewSelectedIndex[0] > -1) {
					_application.EmailList.remove(emailListViewSelectedIndex[0]);
					emailListViewSelectedIndex[0] = -1;
					emailTextBox.setText("");

					Bind(emailListView);
				}else {
					Toast.makeText(_application.getApplicationContext(), "Please select an item to remove.", Toast.LENGTH_LONG).show();
				}
			}
		});

		sendViaButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				/*_application.EncryptService().setOnFinishedListener(new EncryptService.OnFinishedListener() {
					@Override
					public void onFinished() {
						_application.EmailSerivce().Send(_application.getCurrentActivity(), _application.FileService().GetFileList(), emailAddressArray);
					}
				});*/

				_application.EncryptService().EncryptFiles(_application.FileService().GetFileList(), _application.EmailList);
			}
		});

        encryptButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
			_application.EncryptService().EncryptFiles(_application.FileService().GetFileList(), _application.EmailList);
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
		emailListView.setAdapter(_emailListViewAdapter);
		_emailListViewAdapter.notifyDataSetChanged();
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
