package com.softechworld.browser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class MainActivity extends AppCompatActivity {

    private String postUrl = "https://www.google.com";
    private WebView webView;
    private ProgressBar progressBar;
    private float m_downX;
    private RewardedVideoAd mRewardedVideoAd;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_browser);

        //  *************  Rewarded Video Ads  ******************
        MobileAds.initialize(this, "ca-app-pub-4036219497911064~3064152394");

        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {

            @Override
            public void onRewardedVideoAdLoaded() {
                mRewardedVideoAd.show();
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                loadRewardedVideoAd();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Log.i("AdLoad","Ad failed to load-"+i);
                loadRewardedVideoAd();
            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });

        loadRewardedVideoAd();

        mAdView = findViewById(R.id.adView);
        final AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId("ca-app-pub-4036219497911064/9530322546");
        mAdView.loadAd(adRequest);

        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (!TextUtils.isEmpty(getIntent().getStringExtra("postUrl"))) {
            postUrl = getIntent().getStringExtra("postUrl");
        }

        initWebView();
        renderPost();


        // enable / disable javascript
        // webView.getSettings().setJavaScriptEnabled(true);

        // loading url into web view
        // webView.loadUrl("http://www.google.com");

        /**
         * loading custom html into webivew
         * */
        /*
        String customHtml = "<html><body><h1>Hello, WebView</h1> <h1>Heading 1</h1><h2>Heading 2</h2><h3>Heading 3</h3>" +
                "<p>This is a sample paragraph.</p></body></html>";
        webView.loadData(customHtml, "text/html", "UTF-8");
        */

        /**
         * Enabling zoom-in controls
         * */
        /*
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        */

        // Loading local html file into web view
        // webView.loadUrl("file:///android_asset/sample.html");

        /**
         * Loading custom fonts and css
         * */
        /*
        String style = "<style type='text/css'>@font-face { font-family: 'roboto'; src: url('Roboto-Light.ttf');}@font-face { font-family: 'roboto-medium'; src: url('Roboto-Medium.ttf'); }" +
                "body{color:#666;font-family: 'roboto';padding: 0.3em;}";
        style += "a{color:" + String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(this, R.color.colorPrimaryDark))) + "}</style>";
        String customHtml = "<h1>Hello, WebView</h1> <h1>Heading 1</h1><h2>Heading 2</h2><h3>Heading 3</h3>" +
                "<p>This is a sample paragraph.</p>";
        String content = "<html>" + style + "<body'>" + customHtml + "</body></Html>";
        webView.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "utf-8", null);
        */
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-4036219497911064/8269227679",
                new AdRequest.Builder().build());
    }

    private void initWebView() {
        webView.setWebChromeClient(new MyWebChromeClient(this));
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /**
                 * Check for the url, if the url is from same domain
                 * open the url in the same activity as new intent
                 * else pass the url to browser activity
                 * */
                if (Utils.isSameDomain(postUrl, url)) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("postUrl", url);
                    startActivity(intent);
                } else {
                    // launch in-app browser i.e BrowserActivity
                    openInAppBrowser(url);
                }

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getPointerCount() > 1) {
                    //Multi touch detected
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        // save the x
                        m_downX = event.getX();
                    }
                    break;

                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        // set x so that it doesn't move
                        event.setLocation(m_downX, event.getY());
                    }
                    break;

                }

                return false;
            }
        });
    }

    private void renderPost() {
        webView.loadUrl(postUrl);

        // webView.loadUrl("file:///android_asset/sample.html");
    }

    private void openInAppBrowser(String url) {
        Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar txtPostTitle on scroll
     */


    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        public MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }


    }
}
