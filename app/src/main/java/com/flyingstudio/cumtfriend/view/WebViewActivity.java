package com.flyingstudio.cumtfriend.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.flyingstudio.cumtfriend.MainActivity;
import com.flyingstudio.cumtfriend.R;
import com.flyingstudio.cumtfriend.utils.SPUtil;
import com.flyingstudio.cumtfriend.utils.SimpleWebview;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

public class WebViewActivity extends AppCompatActivity {
    private SimpleWebview webView;
    private final static String TAG = "WebViewActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("正在加载");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        Log.d(TAG, "initView: " + url);
        webView = findViewById(R.id.web);
        webView.loadUrl(url);

        webView.setWebChromeClient(new WebChromeClient() {
            //这里可以设置进度条。但我是用另外一种
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                if (i == 100)setTitle(webView.getTitle());
            }
        });


    }
}
