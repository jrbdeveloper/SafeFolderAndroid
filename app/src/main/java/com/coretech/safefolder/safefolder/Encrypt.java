package com.coretech.safefolder.safefolder;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class Encrypt extends Activity {

    String[] items = {};
    ArrayAdapter<String> emailListViewAdapter;
    private final static String TAG = "TestActivity";

    /**
     * First to be called when the Activity starts; only called once
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

        final ListView emailListView = (ListView) findViewById(R.id.listView);
        final ArrayList<String> emailAddressArray = new ArrayList();
        emailListViewAdapter = new ArrayAdapter<String>(Encrypt.this, android.R.layout.simple_expandable_list_item_1,emailAddressArray);

        Button addEmailButton = (Button) findViewById(R.id.add_email);
        Button sendViaButton = (Button) findViewById(R.id.send_via);
        Button encryptButton = (Button)findViewById(R.id.encrypt);

        addEmailButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                //add email button click event.  Add email to list
                TextView emailTextBox = (TextView) findViewById(R.id.editText);
                String emailAddress = (String) emailTextBox.getText().toString();
                emailTextBox.setText(""); // Clear the textbox

                // Take the text from the box and add it to the list view
                emailAddressArray.add(emailAddress);
                emailListView.setAdapter(emailListViewAdapter);
                emailListViewAdapter.notifyDataSetChanged();
            }
        });

        sendViaButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                EmailService emailService = new EmailService();
                FileService fileService = new FileService();
                EncryptService encryptService = new EncryptService();

                String response = encryptService.EncryptFiles(Encrypt.this, fileService.GetFileList(Encrypt.this), emailAddressArray);
                emailService.Send(Encrypt.this, fileService.GetFileList(Encrypt.this), emailAddressArray);
//                Close();
            }
        });

        encryptButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                FileService fileService = new FileService();
                EncryptService encryptService = new EncryptService();

                String response = encryptService.EncryptFiles(Encrypt.this, fileService.GetFileList(Encrypt.this), emailAddressArray);
//                Close();
            }
        });
    }

    /**
     * Called after onCreate() and before onResume()
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "On Start .....");
    }

    /**
     * Called after onStart() as a result of onPause()
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "On Resume .....");
    }

    /**
     * Called after the Activity is running and before onStop()
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "On Pause .....");
    }

    /**
     * Called before onDestroy()
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "On Stop .....");
    }

    /**
     * Called as a result of onStop()
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "On Restart .....");
    }

    /**
     * Called just before the activity is shut down
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "On Destroy .....");
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
