package id.ac.undip.ce.student.muhammadrizqi.inkubatorbayi;


import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipeRefreshLayout;
    WebView webView;
    WebSettings webSettings;
    private ValueCallback<Uri>mUploadMessage;
    private ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    String URL = "http://web.box-of-life.online";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        webView = (WebView) findViewById(R.id.webview);

        swipeRefreshLayout.setOnRefreshListener(this);

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Untuk mengaktifkan javascript
        webSettings.getUseWideViewPort();
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setSupportZoom(false);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // Menampilkan loading ketika webview proses load halaman
                swipeRefreshLayout.setRefreshing(true);
            }
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }


            // For Lollipop 5.0+ Devices
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                try
                {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e)
                {
                    uploadMessage = null;
                    Toast.makeText(MainActivity.this.getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
            {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            // Ketika webview error atau selesai load page loading akan dismiss

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if (savedInstanceState == null) {
            webView.loadUrl(URL);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        //konfig untuk menyimpan state agar tidak kerefresh
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        //menyesuaikan state terahir tanpa refresh halaman
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    @Override
    public void onRefresh() {
        // Untuk refresh webview dengan swipe
        webView.reload();
    }

    @Override
    public void onBackPressed() {
        // Jika Webview bisa di back maka backward page sebelumnya
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (requestCode == REQUEST_SELECT_FILE)
            {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        }
        else if (requestCode == FILECHOOSER_RESULTCODE)
        {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
        else
            Toast.makeText(MainActivity.this.getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }

//    public  void createWebPagePrint(WebView webView) {
//		/*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
//            return;*/
//        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
//        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();
//        String jobName = getString(R.string.webapp_name) + " Document";
//        PrintAttributes.Builder builder = new PrintAttributes.Builder();
//        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A5);
//        PrintJob printJob = printManager.print(jobName, printAdapter, builder.build());
//
//        if(printJob.isCompleted()){
//            Toast.makeText(getApplicationContext(), R.string.print_complete, Toast.LENGTH_LONG).show();
//        }
//        else if(printJob.isFailed()){
//            Toast.makeText(getApplicationContext(), R.string.print_failed, Toast.LENGTH_LONG).show();
//        }
//        // Save the job object for later status checking
//    }

}
