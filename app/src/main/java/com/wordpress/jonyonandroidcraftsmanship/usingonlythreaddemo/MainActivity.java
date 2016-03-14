package com.wordpress.jonyonandroidcraftsmanship.usingonlythreaddemo;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private EditText etDownloadURL = null;
    private ListView lvURLLinks = null;
    private LinearLayout llLoadingSection = null;
    private ProgressBar pbDownload = null;
    private String[] listOfImageURLs = null;

    private Handler handler=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {
        etDownloadURL = (EditText) findViewById(R.id.etDownloadURL);
        lvURLLinks = (ListView) findViewById(R.id.lvURLLinks);
        llLoadingSection = (LinearLayout) findViewById(R.id.llLoadingSection);
        pbDownload = (ProgressBar) findViewById(R.id.pbDownload);
        listOfImageURLs = getResources().getStringArray(R.array.image_urls);
        lvURLLinks.setOnItemClickListener(this);
        handler=new Handler();
    }

    public void downloadImage(View view) {
        String url = etDownloadURL.getText().toString();
        Thread thread = new Thread(new DownloadImageThread(url));
        thread.start();
//        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        String url=listOfImageURLs[0];
//        Uri uri=Uri.parse(url);
//        Logger.log(uri.getLastPathSegment());
//        Logger.log(file.getAbsolutePath());
    }

    public boolean downloadImageUsingThreads(String url) {
        boolean successsful = false;
        URL downloadUrl = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        File file = null;
        FileOutputStream fileOutputStream = null;
        try {
            downloadUrl = new URL(url);
            connection = (HttpURLConnection) downloadUrl.openConnection();
            inputStream = connection.getInputStream();

            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + "/" + Uri.parse(url).getLastPathSegment());
//            Logger.log(file.toString());
            fileOutputStream = new FileOutputStream(file);
            int read = -1;
            byte[] buffer = new byte[1024];
            while ((read = inputStream.read(buffer)) != -1) {
                Logger.log("" + read);
                fileOutputStream.write(buffer, 0, read);
            }
            successsful = true;
        } catch (MalformedURLException e) {
            Logger.log(e.toString());
        } catch (IOException e) {
            Logger.log(e.toString());
        } finally {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    llLoadingSection.setVisibility(View.GONE);
//                }
//            });
            handler.post(new Runnable() {
                @Override
                public void run() {
                    llLoadingSection.setVisibility(View.GONE);
                }
            });
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Logger.log(e.toString());
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Logger.log(e.toString());
                }
            }
        }
        return successsful;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        etDownloadURL.setText(listOfImageURLs[position]);
    }

    private class DownloadImageThread implements Runnable {

        private String url = null;

        public DownloadImageThread(String url) {
            this.url = url;
        }

        @Override
        public void run() {
//            MainActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    llLoadingSection.setVisibility(View.VISIBLE);
//                }
//            });
            handler.post(new Runnable() {
                @Override
                public void run() {
                    llLoadingSection.setVisibility(View.VISIBLE);
                }
            });
            downloadImageUsingThreads(url);
        }
    }
}
