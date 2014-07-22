package com.coretech.safefolder.safefolder;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
                String response = EncryptFiles(GetFileList(), emailAddressArray);
                sendEmail(GetFileList(), emailAddressArray);
                Close();
            }
        });

        encryptButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                String response = EncryptFiles(GetFileList(), emailAddressArray);
                Close();
            }
        });
    }

    protected void sendEmail(ArrayList encryptedFileList, ArrayList emailList){
        String toAddress = StringUtils.join(emailList, ",");
        String subject = "";
        String body = "";
        ArrayList<String> attachmentPath = encryptedFileList;

        try {
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { toAddress });
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, body);
            intent.setType("message/rfc822");

            ArrayList<Uri> uri = new ArrayList<Uri>();
            for (int i = 0; i < attachmentPath.size(); i++) {
                File file = new File(attachmentPath.get(i));
                uri.add(Uri.fromFile(file));
            }

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uri);

            this.startActivity(Intent.createChooser(intent, "Choose an email application..."));
        } catch (Exception ex) {
            ex.printStackTrace();
            //throw ex;
        }
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

    private String EncryptFiles(ArrayList fileList, ArrayList emailList){
        ArrayList<String> files = GetFileList();

        for(String item: files) {
            File inputFile = new File(item);
            Toast.makeText(getApplication().getBaseContext(), inputFile.getName(), Toast.LENGTH_LONG).show();

            // Call Encryptics API here
            //String response = Encryptics.EncryptFile(intputFile, new(){
            //
            //});
        }

        return "";
    }

    //Call this from the encrypt and Send Button.  This method gets the file(s) that were selected in the previous app.
    private ArrayList<String> GetFileList() {
        Intent theIntent = getIntent();
        String theAction = theIntent.getAction();
        ArrayList<String> fileArray = new ArrayList<String>();

        if (Intent.ACTION_SEND.equals(theAction)) {
            Bundle bundle = getIntent().getExtras();
            Uri i = (Uri) bundle.get("android.intent.extra.STREAM");

            String inputfilename = getRealPathFromURI(i);
            String outputfilename = getFileNameFromPath(getRealPathFromURI(i));

            //mws delete the toast and encrypt here
            fileArray.add(inputfilename);

        } else if (Intent.ACTION_SEND_MULTIPLE.equals(theAction) && theIntent.hasExtra(Intent.EXTRA_STREAM)) {

            ArrayList list = theIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

            for (Object p : list) {
                Uri uri = (Uri) p; /// do something with it.
                //Create input and output paths
                String inputfilename = getRealPathFromURI(uri);
                String outputfilename = getFileNameFromPath(getRealPathFromURI(uri));

                //mws delete the toast and encrypt here
                fileArray.add(inputfilename);
            }
        }

        return fileArray;
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

    public String getFileNameFromPath(String input) {
        String[] temp;
        String delimeter = "/";
        temp = input.split(delimeter);
        return temp[temp.length - 1];
    }

    // Convert the image URI to the direct file system path of the image file
    public String getRealPathFromURI(Uri contentUri) {

        /*
        MediaStore.Images.Media.DATA,
        MediaStore.Video.Media.DATA,
        MediaStore.Audio.Media.DATA,
        MediaStore.Files.FileColumns.DATA
        */

        String [] proj = { MediaStore.Audio.Media.DATA };

        Cursor cursor = managedQuery( contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
}
