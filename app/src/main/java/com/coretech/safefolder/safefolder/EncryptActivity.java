package com.coretech.safefolder.safefolder;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coretech.safefolder.safefolder.entities.ListItem;
import com.coretech.safefolder.safefolder.utilities.ListAdapter;

public class EncryptActivity extends Activity {

	//region Private members
    private ListAdapter _emailListViewAdapter;
	//endregion

	//region Constructor
	public EncryptActivity(){
		SafeFolder.Instance().setCurrentActivity(this);
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

		final TextView emailTextBox = (TextView) findViewById(R.id.firstNameText);
		final ListView emailListView = (ListView) findViewById(R.id.emailListView);
		emailListView.setSelection(-1);

        _emailListViewAdapter = new ListAdapter(EncryptActivity.this, SafeFolder.Instance().Email().Collection());

        Button addEmailButton = (Button) findViewById(R.id.add_email);
		Button removeEmailButton = (Button) findViewById(R.id.remove_email);

        final Button sendViaButton = (Button) findViewById(R.id.send_via);
        final Button encryptButton = (Button)findViewById(R.id.encrypt);

		final int[] emailListViewSelectedIndex = new int[1];
		emailListViewSelectedIndex[0] = -1;

        addEmailButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                String emailAddress = emailTextBox.getText().toString();
				emailTextBox.setText("");
                ListItem item = new ListItem();
                item.setText(emailAddress);

                // Take the text from the box and add it to the list view
                if(emailListViewSelectedIndex[0] < 0){
					SafeFolder.Instance().Email().Collection().add(item);
				}else{
					SafeFolder.Instance().Email().Collection().set(emailListViewSelectedIndex[0], item);
					emailListViewSelectedIndex[0] = -1;
				}

				Bind(emailListView);
            }
        });

		removeEmailButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
                RemoveEmailFromList(emailListViewSelectedIndex, emailTextBox, emailListView);
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

				sendViaButton.setEnabled(false);

				SafeFolder.Instance().Security().EncryptFiles(SafeFolder.Instance().File().Collection(), SafeFolder.Instance().Email().Collection());
				sendViaButton.setEnabled(true);
			}
		});

        encryptButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
				encryptButton.setEnabled(false);
				SafeFolder.Instance().Security().EncryptFiles(SafeFolder.Instance().File().GetCollection(), SafeFolder.Instance().Email().Collection());
				encryptButton.setEnabled(true);
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

    public void RemoveEmailFromList(int[] emailListViewSelectedIndex, TextView emailTextBox, ListView emailListView) {
        if(emailListViewSelectedIndex[0] > -1) {
            SafeFolder.Instance().Email().Collection().remove(emailListViewSelectedIndex[0]);
            emailListViewSelectedIndex[0] = -1;
            emailTextBox.setText("");

            Bind(emailListView);
        }else {
            Toast.makeText(SafeFolder.Instance().getApplicationContext(), "Please select an item to remove.", Toast.LENGTH_LONG).show();
        }
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
