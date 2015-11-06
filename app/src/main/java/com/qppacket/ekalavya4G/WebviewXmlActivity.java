package com.qppacket.ekalavya4G;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

/**
 * Created by gorillalogic on 7/29/15.
 */
public class WebviewXmlActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_xml);
        ((WebView)findViewById(R.id.web_view)).loadUrl(getIntent().getStringExtra("url"));
    }
}
