package com.qppacket.ekalavya4G;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qppacket.ekalavya4G.model.Store;
import com.qppacket.ekalavya4G.rest.OnPostExecuteListener;
import com.qppacket.ekalavya4G.rest.RestApi;
import com.qppacket.ekalavya4G.utils.Utils;


public class MainActivity extends Activity implements View.OnClickListener, OnPostExecuteListener {

    private View mLogin, mSplash;
    private boolean isDownloaded, isSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLogin = findViewById(R.id.login_screen);
        mSplash = findViewById(R.id.splash_screen);
        findViewById(R.id.practice_exam).setOnClickListener(this);


        isSplash = true;
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Show signup - Login screen
                        mSplash.setVisibility(View.GONE);
                        mLogin.setVisibility(View.VISIBLE);
                        getQuestionsFromServer();
                    }
                });
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isDownloaded && !isSplash) {
            getQuestionsFromServer();
        }
        isSplash = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.practice_exam:
                if (isDownloaded)
                    showQuestionsScreen();
                else
                    Toast.makeText(this, "No Internet. App needs to connect to server to download questions for the first time.", Toast.LENGTH_LONG).show();
                break;

        }
    }

    private void getQuestionsFromServer() {
        RestApi api = new RestApi(this);
        api.setMessage("Getting Questions...");
        api.setPostExecuteListener(this);
        api.get(Utils.URL_VIEW_QUESTION);
    }

    private void showQuestionsScreen() {
        Intent i = new Intent(this, QuestionsActivity.class);
        i.putExtra("is_downloaded", isDownloaded);
        startActivity(i);
    }

    private void showWebXmlView(int id) {
        Intent i = new Intent(this, WebviewXmlActivity.class);
       // i.putExtra("url", id == R.id.why_ekalvya ? Utils.URL_WHY_EKALAVYA : Utils.URL_ENTRANCE_NEWS);
        startActivity(i);
    }

    @Override
    public void onSuccess() {
        Store store = new Store(this);
        isDownloaded = store.getStore().contains(RestApi.OFFLINE_QUESTIONS);
        ((TextView)findViewById(R.id.practice_exam_version))
                .setText("for week " + store.getStore().getString(RestApi.TAG_XML_VERSION, "<no data>"));
    }

    @Override
    public void onFailure() {
        Toast.makeText(this, "Failure: Getting questions from server.", Toast.LENGTH_SHORT).show();
    }
}
