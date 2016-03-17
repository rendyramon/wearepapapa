package com.hotcast.vr;

import android.content.Intent;
import android.net.http.SslError;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import butterknife.InjectView;

/**
 * Created by lostnote on 16/3/14.
 */
public class WebViewActivity extends BaseActivity {
    @InjectView(R.id.web)
    WebView webView;
    @InjectView(R.id.iv_back)
    ImageView iv_back;
    String rec_ur;

    @Override
    public int getLayoutId() {
        return R.layout.layout_webview;
    }

    @Override
    public void init() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("gb2312");

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return false;
            }

            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                // 　　//handler.cancel(); 默认的处理方式，WebView变成空白页
                handler.proceed(); // 接受证书
                // handleMessage(Message msg); 其他处理

            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        if (!TextUtils.isEmpty(rec_ur)){
            webView.loadUrl(rec_ur);
        }else {
            webView.loadUrl("http://weidian.com/i/1767988840?wfr=c");
        }

    }

    @Override
    public void getIntentData(Intent intent) {
        rec_ur = intent.getStringExtra("rec_ur");
        System.out.println("---rec_ur="+rec_ur);
    }

}
