package com.alkhalildevelopers.myapplication;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ProgressDialog bar;
    private static String TAG = "MainActivity";
    private int AppVersion = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView heading = findViewById(R.id.heading);
        Button update_btn = findViewById(R.id.btn);

        heading.setText("App Version: " + AppVersion);

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadNewVersion().execute();
            }
        });

    }

    class DownloadNewVersion extends AsyncTask<String,Integer,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            bar = new ProgressDialog(MainActivity.this);
            bar.setCancelable(false);

            bar.setMessage("Downloading...");
            bar.setIndeterminate(true);
            bar.setCanceledOnTouchOutside(false);
            bar.show();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            bar.setIndeterminate(false);
            bar.setMax(100);
            bar.setProgress(progress [0]);
            String msg = "";
            if (progress[0] > 99){
                msg = "Finishing...";
            }else {
                msg = "Downloading..." + progress[0] + "%";
            }
            bar.setMessage(msg);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            bar.dismiss();

            if (result){
                Toast.makeText(MainActivity.this, "Update Done" , Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this, "Error: Try Again", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean flag = false;

            try {

                URL url = new URL("http://download942.mediafire.com/ntkjeks1wiyg/5ae2mafo2h4v87p/Fast+QR+Scanner.apk");

                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                String Path = Environment.getExternalStorageDirectory()+"/Download/";
                File file = new File(Path);
                file.mkdirs();

                File outputFile = new File(file, "Fast+QR+Scanner.apk");

                if (outputFile.exists()) {
                    outputFile.delete();
                }

                InputStream is = c.getInputStream();

                int total_size = 5913968; //size of apk

                byte[] buffer = new byte[1024];
                int len1 = 0;
                int per = 0;
                int downloaded = 0;

                FileOutputStream fos = new FileOutputStream(outputFile);

                while ((len1 = is.read(buffer)) != -1){
                    fos.write(buffer, 0, len1);
                    downloaded += len1;
                    per = (int) (downloaded * 100 / total_size);
                    publishProgress(per);
                }
                fos.close();
                is.close();

                OpenNewVersion(Path);

                flag = true;
            } catch (Exception e) {
                Log.e(TAG,"Update Error : " + e.getMessage());
                flag = false;
            }


            return flag;
        }
    }

    void  OpenNewVersion(String location){

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(location + "Fast+QR+Scanner.apk")),"application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
