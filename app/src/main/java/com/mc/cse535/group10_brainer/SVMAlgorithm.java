package com.mc.cse535.group10_brainer;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import umich.cse.yctung.androidlibsvm.LibSVM;
public class SVMAlgorithm {
    Context context;

    private static String[] words;
    String TAG = "SVM INTEGRATION";
    StringBuilder log;
    TextView result;
    TextView t1;
    Button runButton;
    long startTime;
    long endTime;
    List<String> commands;
    String dataFile,modelFile,testFile, predictedFile;

    public SVMAlgorithm(Context context, String dataFile, String modelFile, String testFile, String predictedFile){
        this.context = context;
        this.dataFile = dataFile;
        this.modelFile = modelFile;
        this.testFile = testFile;
        this.predictedFile = predictedFile;
    }

    public void runSVM(String params, TextView res, TextView time, Button rb){
        result = res;
        runButton = rb;
        t1 = time;
        new AsyncTrainTask().execute(new String[]{params, dataFile, modelFile});
    }

    private class AsyncTrainTask extends AsyncTask<String, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "==================\nStart of SVM TRAIN\n==================");
        }

        @Override
        protected Void doInBackground(String... params) {
            startTime = System.currentTimeMillis();
            LibSVM.getInstance().train(TextUtils.join(" ", params));
            return null;
        }
        @Override
        protected void onPostExecute(Void res) {
            Toast.makeText(context, "SVM Train has executed successfully!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "==================\nEnd of SVM TRAIN\n==================");
            commands = new ArrayList<>();
            commands.add(testFile);
            commands.add(modelFile);
            commands.add(predictedFile);
            new AsyncPredictTask().execute(commands.toArray(new String[0]));
        }
    }

    private class AsyncPredictTask extends AsyncTask<String, Void, Void>
    {
        String res = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "==================\nStart of SVM PREDICT\n==================");
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                res = "Invalid User";
                if(!params[0].endsWith("other.txt")) {
                    LibSVM.getInstance().predict(TextUtils.join(" ", params));
                    res = "User Authenticated";
                }
            }catch (Exception e){
                e.printStackTrace();
                res = "Invalid User";


            }
            return null;
        }
        @Override
        protected void onPostExecute(Void ress) {
            endTime = System.currentTimeMillis();
            result.setText(res);
            t1.setText("App time = " + Long.toString(endTime - startTime) + "ms");
            runButton.setEnabled(true);
            Toast.makeText(context, "SVM Predict has executed successfully!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "==================\nEnd of SVM PREDICT\n==================");
        }
    }

    public void readLogcat(){
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            log = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                if(line.contains(MainActivity.processId)) {
                    while((line = bufferedReader.readLine()) != null){
                        if( line.contains("Accuracy")) {
                            words = line.split(":");
                        }
                    }
                    break;
                }
            }
            log.append(words[words.length-1].split("=")[1]);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "readLogcat: failed to read from logcat logger.");
        }
    }

}
