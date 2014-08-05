package com.coretech.safefolder.safefolder;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;

public class DecryptActivity extends Activity {

	//region Private Members
	private ArrayAdapter<String> _listViewAdapter;
	private ArrayList<String> _listArray = new ArrayList<String>();
	//endregion

	//region Constructor
	public DecryptActivity(){
		SafeFolder.Instance().setCurrentActivity(this);
	}
	//endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);

		final ListView fileListView = (ListView) findViewById(R.id.fileListView);
		Button decryptButton = (Button) findViewById(R.id.decryptButton);

		_listViewAdapter = new ArrayAdapter<String>(SafeFolder.Instance().getCurrentActivity(), android.R.layout.simple_expandable_list_item_1,_listArray);

		addEncryptedFilesToList();
		Bind(fileListView);

		decryptButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				SafeFolder.Instance().Security().DecryptFiles(SafeFolder.Instance().File().Collection, SafeFolder.Instance().Email().Collection);
			}
		});
    }

	private void addEncryptedFilesToList(){
		if(SafeFolder.Instance().File().Collection.size() > 0){
			for(String item : SafeFolder.Instance().File().Collection){
				_listArray.add(SafeFolder.Instance().File().getNameFromPath(item.toString()));
			}
		}
	}

	private void Bind(ListView emailListView) {
		emailListView.setAdapter(_listViewAdapter);
		_listViewAdapter.notifyDataSetChanged();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.decrypt, menu);
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
