package com.mc.cse535.group10_brainer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.mc.cse535.group10_brainer.properties.Constants;

public class MainActivity extends AppCompatActivity {

    public static String appFolderPath;
    public static String systemPath;
    static MainActivity mainActivity;
    String TAG = "MAIN ACTIVITY";
    TextView t1;
    TextView authResult;
    TextView t2;
    String res = "";

    String trainDataFile;
    String outputModelPath;
    String testDataFolder;
    String predictedFile;
    ArrayAdapter<String> spinAdapter;

    Spinner algoSpinner;
    Spinner nameSpinner;
    Spinner serverType;
    NaiveBayesAlgorithm nbi;
    SVMAlgorithm svmI;

    Button runButton;

    int algo;
    String name;
    long fogLatency;
    long cloudLatency;

    public static final String processId = Integer.toString(android.os.Process.myPid());

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;
        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE
            );
        }

        // request app permissions
        systemPath = Environment.getExternalStorageDirectory() + "/";
        appFolderPath = systemPath+ Constants.APP_FOLDER;
        createAssetsFolder();


        t2 = (TextView) findViewById(R.id.text2);
        t1 = (TextView) findViewById(R.id.text1);
        algoSpinner = (Spinner) findViewById(R.id.algorithm_spinner);
        nameSpinner = (Spinner) findViewById(R.id.name_spinner);
        serverType = (Spinner) findViewById(R.id.server_type_spinner);
        runButton = (Button) findViewById(R.id.server_run_btn);
        authResult = (TextView) findViewById(R.id.authenticate_result);

        spinAdapter = new ArrayAdapter<>(mainActivity,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.algorithms));
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        algoSpinner.setAdapter(spinAdapter);

        spinAdapter = new ArrayAdapter<>(mainActivity,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.names));
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameSpinner.setAdapter(spinAdapter);

        spinAdapter = new ArrayAdapter<>(mainActivity,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.server_type));
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serverType.setAdapter(spinAdapter);

        trainDataFile = appFolderPath + Constants.TRAIN_DATA;
        outputModelPath = appFolderPath + Constants.MODEL_FILE;
        predictedFile = appFolderPath + Constants.PREDICT_DATA;
        testDataFolder = appFolderPath + Constants.TEST_DATA;
        final String svmParams = "-t 2 ";

        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runButton.setEnabled(false);
                algo = algoSpinner.getSelectedItemPosition();
                name = nameSpinner.getSelectedItem().toString().toLowerCase();
                cloudLatency = 0; fogLatency = 0;
                t1.setText(""); t2.setText(""); authResult.setText(""); res = "";
                if(serverType.getSelectedItemPosition() == 0)
                    new processFogReq().execute(new String[]{Integer.toString(algo+1)+"&"+name});
                else if(serverType.getSelectedItemPosition() == 1)
                    new processCloudReq().execute(new String[]{Integer.toString(algo+1)+"&"+name});
                else if(serverType.getSelectedItemPosition() == 2) {
                    if(algo == 0){
                        new processNBReq().execute();
                        nbi = new NaiveBayesAlgorithm(mainActivity);
                    }else if(algo == 1){
                        svmI = new SVMAlgorithm(mainActivity, trainDataFile, outputModelPath, testDataFolder + name + ".txt", predictedFile);
                        svmI.runSVM(svmParams, authResult, t1, runButton);
                    }else {
                        authResult.setText("Invalid Combination of Inputs");
                        runButton.setEnabled(true);
                    }
                }else {
                    new processFogReq().execute(new String[]{Integer.toString(algo + 1)+"&"+name});
                    new processCloudReq().execute(new String[]{Integer.toString(algo + 1)+"&"+name});
                }
            }
        });

    }
    private class processNBReq extends AsyncTask<Void, Void, Void>{

        long startTime,endTime;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... strings) {
            startTime = System.currentTimeMillis();
            nbi = new NaiveBayesAlgorithm(mainActivity);
            res = nbi.runNB(trainDataFile, testDataFolder + name + ".txt");
            endTime = System.currentTimeMillis();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            t1.append(" App NB Time = "+Long.toString(endTime - startTime) +"ms");
            authResult.setText(res);
            runButton.setEnabled(true);
        }
    }
    private void createAssetsFolder(){
        // create app assets folder if not created
        File folder = new File(appFolderPath);

        if (!folder.exists()) {
            Log.d(TAG,"Assets folder does not exist, creating one");
            folder.mkdirs();
        } else {
            Log.w(TAG,"INFO: Assets folder already exists.");
        }
    }



    private class processCloudReq extends AsyncTask<String, Void, Void>
    {
        long startTime,endTime;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            try{
                String urlStr = Constants.CLOUD_URL+params[0];
                URL url = new URL(urlStr);
                startTime = System.currentTimeMillis();
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are getting data
                c.setReadTimeout(5000);
                c.setConnectTimeout(2000);
                c.connect();//connect the URL Connection
                endTime = System.currentTimeMillis();
                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode() + " " + c.getResponseMessage());
                }
                InputStream is = c.getInputStream();//Get InputStream for connection
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    if(line.equalsIgnoreCase("1"))
                        res = "User Authenticated";
                    else
                        res = "Invalid User";
                }
                is.close();
            }catch (Exception e){
                res = "error connecting cloud";
                endTime = System.currentTimeMillis();
                e.printStackTrace();
                Log.e(TAG, "Error Connecting Cloud Exception " + e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            cloudLatency = endTime - startTime;
            t1.append(" Cloud Latency = "+Long.toString(cloudLatency) +"ms");
            authResult.setText(res);
            if(fogLatency>0)
                if(cloudLatency>fogLatency)
                    t2.setText("Fog is better");
                else
                    t2.setText("Cloud is better");
            runButton.setEnabled(true);
        }
    }
    private class processFogReq extends AsyncTask<String, Void, Void>
    {
        long startTime,endTime;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            try{
                String urlStr = Constants.FOG_URL+params[0];
                URL url = new URL(urlStr);
                startTime = System.currentTimeMillis();
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are getting data
                c.setReadTimeout(5000);
                c.setConnectTimeout(2000);
                c.connect();//connect the URL Connection
                endTime = System.currentTimeMillis();
                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode() + " " + c.getResponseMessage());
                }
                InputStream is = c.getInputStream();//Get InputStream for connection
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    if(line.equalsIgnoreCase("1"))
                        res = "User Authenticated";
                    else
                        res = "Invalid User";
                }
                is.close();
            }catch (Exception e){
                res = "error connecting fog";
                endTime = System.currentTimeMillis();
                e.printStackTrace();
                Log.e(TAG, "Error Connecting Fog Exception " + e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            fogLatency = endTime - startTime;
            t1.append(" Fog Latency = "+Long.toString(fogLatency) + "ms");
            authResult.setText(res);
            if(cloudLatency>0)
                if(cloudLatency>fogLatency)
                    t2.setText("Fog is better");
                else
                    t2.setText("Cloud is better");
            runButton.setEnabled(true);
        }
    }
}
