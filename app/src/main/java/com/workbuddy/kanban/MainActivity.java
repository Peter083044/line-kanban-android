package com.workbuddy.kanban;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private WebView webView;
    private LinearLayout loadingView;
    private LinearLayout errorView;
    private String kanbanUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 地址统一在 res/values/strings.xml 的 kanban_url 维护
        kanbanUrl = getString(R.string.kanban_url);

        loadingView = findViewById(R.id.loading_view);
        errorView = findViewById(R.id.error_view);
        webView = findViewById(R.id.webview);

        // 保持屏幕常亮，便于长时间盯着看板
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);              // 看板依赖 JS
        ws.setDomStorageEnabled(true);              // 关键：localStorage 持久化数据
        ws.setDatabaseEnabled(true);
        ws.setAllowFileAccess(false);
        ws.setAllowContentAccess(false);
        ws.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setSupportZoom(false);                   // 禁止双指缩放，体验更像原生 App
        ws.setBuiltInZoomControls(false);
        ws.setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                loadingView.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                // 仅当主框架加载失败才弹出错误层，避免资源级小错干扰
                if (request.isForMainFrame()) {
                    loadingView.setVisibility(View.GONE);
                    errorView.setVisibility(View.VISIBLE);
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        Button retry = findViewById(R.id.retry_button);
        retry.setOnClickListener(v -> reload());

        reload();
    }

    private void reload() {
        errorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        webView.loadUrl(kanbanUrl);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
