package id.ac.undip.ce.student.muhammadrizqi.inkubatorbayi;


import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MainActivity extends AppCompatActivity {
    WebView view;



    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView view = this.findViewById(R.id.webview);
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setAllowFileAccessFromFileURLs(true);
        view.getSettings().setAllowUniversalAccessFromFileURLs(true);
        view.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        view.getSettings().getAllowContentAccess();
        view.getSettings().setSupportMultipleWindows(true);
        view.getSettings().setDomStorageEnabled(true);
        view.setWebViewClient(new MyBrowser());

        view.loadUrl("http://web.box-of-life.online/");
        view.setWebChromeClient(new WebChromeClient());
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            view.loadUrl(url);
            return true;
        }
    }
//    public boolean onKeyDown(int keycode, KeyEvent event){
//        if((keycode== KeyEvent.KEYCODE_BACK)&& view.canGoBack()){
//            view.goBack();
//            return  true;
//        }
//        return super.onKeyDown(keycode, event);
//    }

}
