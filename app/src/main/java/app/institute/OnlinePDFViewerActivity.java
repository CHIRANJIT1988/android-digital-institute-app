package app.institute;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import app.institute.alert.CustomAlertDialog;
import app.institute.helper.OnAlertButtonClick;
import app.institute.network.InternetConnectionDetector;

import static app.institute.configuration.Configuration.DIRECTORY_NAME;
import static app.institute.configuration.Configuration.API_URL;


public class OnlinePDFViewerActivity extends AppCompatActivity implements OnAlertButtonClick
{

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;

    public static WebView webView;
    private LinearLayout layoutHeader;
    private ProgressBar progressBar;


    private RelativeLayout layout_download;
    private ProgressBar circularProgressBar;
    private TextView download_percent;

    private static String URL, FILE_NAME;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_pdfviewer);

        webView = (WebView) findViewById(R.id.webView);
        layoutHeader = (LinearLayout) findViewById(R.id.header);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        layout_download = (RelativeLayout) findViewById(R.id.layout_download);
        circularProgressBar = (ProgressBar) findViewById(R.id.progressBar2);
        download_percent = (TextView) findViewById(R.id.download_percent);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FILE_NAME = getIntent().getStringExtra("FILE_NAME");

        try
        {

            if(new InternetConnectionDetector(this).isConnected())
            {
                URL = "https://docs.google.com/viewer?url=" + API_URL + FILE_NAME;
                startWebView();
            }

            else
            {
                new CustomAlertDialog(OnlinePDFViewerActivity.this, this).showOKDialog("Failure", "Internet Connection Failure", "CLOSE");
            }
        }

        catch (Exception e)
        {

        }
    }


    private void startWebView()
    {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link

        webView.setWebViewClient(new WebViewClient() {


            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                URL = url;
                view.loadUrl(URL);
                return true;
            }


            //Show loader on url load
            public void onLoadResource(WebView view, String url) {

            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                layoutHeader.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
                super.onPageStarted(view, url, favicon);
            }


            public void onPageFinished(WebView view, String url) {

                try {

                    progressBar.setProgress(100);
                    layoutHeader.setVisibility(View.GONE);
                }

                catch(Exception exception)
                {
                    exception.printStackTrace();
                }
            }
        });



        // Javascript inabled on webview
        webView.getSettings().setJavaScriptEnabled(true);

        // Other webview options

	    /*webView.getSettings().setLoadWithOverviewMode(true);
	    webView.getSettings().setUseWideViewPort(true);
	    webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
	    webView.setScrollbarFadingEnabled(false);
	    webView.getSettings().setBuiltInZoomControls(true);
	    String summary = "<html><body>You scored <b>192</b> points.</body></html>";
        webView.loadData(summary, "text/html", null);*/

        //Load url in webview
        //webView.getSettings().setLoadWithOverviewMode(true);
        //webView.getSettings().setUseWideViewPort(true);
        //webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        //webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(URL);
    }

    @Override
    public void onAlertButtonClick(boolean flag, int code)
    {

        if (flag && code == 200)
        {
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_download, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case android.R.id.home:
            {
                finish();
                break;
            }

            case R.id.action_download:

                if(!permissionCheckerStorage())
                {
                    return false;
                }

                if(new InternetConnectionDetector(this).isConnected())
                {

                    File file = new File(FILE_NAME);

                    myAsyncTask myWebFetch = new myAsyncTask();
                    myWebFetch.execute(API_URL + FILE_NAME, file.getName());
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "Internet Connection Failure", Toast.LENGTH_LONG).show();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }


    class myAsyncTask extends AsyncTask<String, String, String>
    {

        @Override
        protected void onPostExecute(String result)
        {
            layout_download.setVisibility(View.GONE);
            //listener.onTaskCompleted(true, 200, "refresh");
            super.onPostExecute(result);
        }


        @Override
        protected void onPreExecute()
        {

            //layout_download.setVisibility(View.GONE);
            layout_download.setVisibility(View.VISIBLE);

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... progress)
        {
            circularProgressBar.setProgress(Integer.parseInt(progress[0]));
            download_percent.setText(String.valueOf(progress[0] + "%"));
        }


        protected String doInBackground(String... args)
        {

            try
            {


                Log.v("FILE_URL", args[0]);

                //set the download URL, a url that points to a file on the internet
                //this is the file to be downloaded
                java.net.URL url = new URL(args[0]);

                //create the new connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //set up some things on the connection
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);

                //and connect!
                urlConnection.connect();

                //set the path where we want to save the file
                //in this case, going to save it on the root directory of the
                //sd card.
                //File SDCardRoot = Environment.getExternalStorageDirectory();

                // External sdcard location
                File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), DIRECTORY_NAME);
                // File noMedia = new File ( Environment.getExternalStoragePublicDirectory(DIRECTORY_NAME), ".nomedia" );

                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists())
                {

                    if (!mediaStorageDir.mkdirs())
                    {
                        //Log.d(DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                        return null;
                    }

                    /*if (!noMedia.exists())
                    {
                        FileOutputStream noMediaOutStream = new FileOutputStream ( noMedia );
                        noMediaOutStream.write ( 0 );
                        noMediaOutStream.close();
                    }*/
                }


                Log.v("FILE_NAME", args[1]);

                //create a new file, specifying the path, and the filename
                //which we want to save the file as.
                File file = new File(mediaStorageDir, args[1]);

                //this will be used to write the downloaded data into the file we created
                FileOutputStream fileOutput = new FileOutputStream(file);

                //this will be used in reading the data from the internet
                InputStream inputStream = urlConnection.getInputStream();

                //this is the total size of the file
                int totalSize = urlConnection.getContentLength();

                //variable to store total downloaded bytes
                int downloadedSize = 0;

                //create a buffer...
                byte[] buffer = new byte[1024];
                int bufferLength = 0; //used to store a temporary size of the buffer

                //now, read through the input buffer and write the contents to the file
                while ( (bufferLength = inputStream.read(buffer)) > 0 )
                {

                    //add the data in the buffer to the file in the file output stream (the file on the sd card
                    fileOutput.write(buffer, 0, bufferLength);

                    //add up the size so we know how much is downloaded
                    downloadedSize += bufferLength;

                    //this is where you would do something to report the prgress, like this maybe
                    //updateProgress(downloadedSize, totalSize);

                    // After this onProgressUpdate will be called
                    publishProgress("" + ((downloadedSize * 100) / totalSize));
                }

                //close the output stream when done
                fileOutput.close();

                //catch some possible errors...
            }

            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }


    private boolean checkPermissionStorage()
    {

        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (result == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }

        else
        {
            return false;
        }
    }

    private void requestPermissionStorage(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            makeToast("Storage permission allows us to read or write data onto memory. Please allow in App Settings for read or write data.");
        }

        else
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {

        switch (requestCode)
        {

            case STORAGE_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeToast("Permission Granted");
                } else {
                    makeToast("Permission Denied");
                }

                break;
        }
    }


    private boolean permissionCheckerStorage() {

        if (!checkPermissionStorage())
        {
            requestPermissionStorage();
            return false;
        }

        return true;
    }


    private void makeToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}