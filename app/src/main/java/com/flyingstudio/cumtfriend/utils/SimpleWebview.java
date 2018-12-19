package com.flyingstudio.cumtfriend.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;

import com.flyingstudio.cumtfriend.view.WebViewActivity;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

public class SimpleWebview extends com.tencent.smtt.sdk.WebView {
    private Context context;

    public SimpleWebview(Context context) {
        super(context);
        init();
    }

    public SimpleWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpleWebview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {

        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);

        this.setWebViewClient(new SimpleWebViewClient(getContext()));

        this.setWebChromeClient(new WebChromeClient() {
            //这里可以设置进度条。但我是用另外一种
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
            }
        });
    }

    @Override
    public void loadUrl(String s) {
        super.loadUrl(s);
        com.tencent.smtt.sdk.CookieManager webCookieManager = com.tencent.smtt.sdk.CookieManager.getInstance();
        webCookieManager.setAcceptCookie(true);
        String cookie = SPUtil.getValue(getContext(), "JSESSIONID");
        webCookieManager.setCookie("jwxt.cumt.edu.cn", "JSESSIONID=" + cookie);
        com.tencent.smtt.sdk.CookieSyncManager.createInstance(getContext()).sync();
    }

    public static class SimpleWebViewClient extends com.tencent.smtt.sdk.WebViewClient {
        private Context context;
        public SimpleWebViewClient(Context context) {
            this.context = context;
        }

        @Override
        public com.tencent.smtt.export.external.interfaces.WebResourceResponse shouldInterceptRequest(com.tencent.smtt.sdk.WebView webView, String url) {
            //做广告拦截，ADFIlterTool 为广告拦截工具类
            return super.shouldInterceptRequest(webView, url);
        }

        /**
         * 防止加载网页时调起系统浏览器
         */
        @Override
        public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView webView, String url) {
            if (url == null) return false;

            try {
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                    return true;
                }
            } catch (Exception e) {//防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                return true;//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
            }

            // TODO Auto-generated method stub
            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
            webView.loadUrl(url);
            return true;
        }


        public interface ADClick{
            void click();
        }
        public ADClick adClick;

        public void setAdClick(ADClick adClick){
            this.adClick = adClick;
        }
    }




}


