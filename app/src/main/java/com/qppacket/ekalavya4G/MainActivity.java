package com.qppacket.ekalavya4G;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.qppacket.ekalavya4G.model.Data;
import com.qppacket.ekalavya4G.model.Question;
import com.qppacket.ekalavya4G.model.QuestionPaper;
import com.qppacket.ekalavya4G.model.Store;
import com.qppacket.ekalavya4G.rest.OnPostExecuteListener;
import com.qppacket.ekalavya4G.rest.QuestionPaperListApi;
import com.qppacket.ekalavya4G.rest.RestApi;
import com.qppacket.ekalavya4G.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener, OnPostExecuteListener {

    private View mLogin, mSplash;
    private boolean isSplash;
    private View mOldQuestions;
    private Store mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLogin = findViewById(R.id.login_screen);
        mSplash = findViewById(R.id.splash_screen);
        mOldQuestions = findViewById(R.id.old_questions);
        findViewById(R.id.latest_exam).setOnClickListener(this);

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
                        getQuestionsFromServer(Utils.URL_LATEST_QUESTION_PAPER);
                        getOldQuestionPapers();
                    }
                });
            }
        }, 1000);
        mStore = new Store(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isAvailableOffline(Utils.URL_LATEST_QUESTION_PAPER) && !isSplash) {
            getQuestionsFromServer(Utils.URL_LATEST_QUESTION_PAPER);
            getOldQuestionPapers();
        }
        isSplash = false;
    }

    @Override
    public void onClick(View v) {
        if (!isAvailableOffline(Utils.URL_LATEST_QUESTION_PAPER)) {
            Toast.makeText(this, "No Internet. App needs to connect to server to download questions for the first time.", Toast.LENGTH_LONG).show();
            return;
        }

        switch (v.getId()) {
            case R.id.latest_exam:
                showQuestionsScreen(Utils.URL_LATEST_QUESTION_PAPER);
                break;

            case R.id.old_questions:
                showOldQPapersList();
                break;
        }
    }

    private void showOldQPapersList() {
        final List<QuestionPaper> papers = Data.getInstance().getQuestionPapersList();
        if (papers != null) {
            ArrayList<String> names = new ArrayList<>();
            for (QuestionPaper p : papers) {
                names.add(p.getName());
            }

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Select Question Paper")
                    .setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, names)
                            , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getQuestionsFromServer(papers.get(i).getQPath());
                        }
                    })
                    .show();
        }
    }

    private void getQuestionsFromServer(String url) {
        // Get latest questions
        RestApi api = new RestApi(this);
        api.setMessage("Getting Questions...");
        api.setPostExecuteListener(this);
        api.get(url);
    }

    private void getOldQuestionPapers() {
        // Get Old Question Papers
        QuestionPaperListApi api = new QuestionPaperListApi(this);
        api.setPostExecuteListener(new OnPostExecuteListener() {
            @Override
            public void onSuccess(String url) {
                mOldQuestions.setEnabled(true);
                mOldQuestions.setOnClickListener(MainActivity.this);
            }

            @Override
            public void onFailure(String url) {
                mOldQuestions.setEnabled(false);
            }
        });
        api.get(Utils.URL_QUESTION_PAPER_LIST);
    }

    private void showQuestionsScreen(String url) {
        Data.getInstance().setQuestions(getOfflineQuestions(url));
        Intent i = new Intent(this, QuestionsActivity.class);
        i.putExtra("is_downloaded", isAvailableOffline(url));
        i.putExtra("url", url);
        startActivity(i);
    }

    private void showWebXmlView(int id) {
        Intent i = new Intent(this, WebviewXmlActivity.class);
        // i.putExtra("url", id == R.id.why_ekalvya ? Utils.URL_WHY_EKALAVYA : Utils.URL_ENTRANCE_NEWS);
        startActivity(i);
    }

    private boolean isAvailableOffline(String url) {
        return mStore.getStore().contains(RestApi.OFFLINE_QUESTIONS + url);
    }

    private List<Question> getOfflineQuestions(String url) {
        List<Question> questions = new ArrayList<>();
        try {
            JSONArray qs = new JSONArray(mStore.getStore().getString(RestApi.OFFLINE_QUESTIONS + url, new JSONArray().toString()));
            for (int i=0; i<qs.length(); i++) {
                questions.add(Question.parseJson(qs.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questions;
    }

    @Override
    public void onSuccess(String url) {
        if (url.equals(Utils.URL_LATEST_QUESTION_PAPER)) {
            ((TextView) findViewById(R.id.practice_exam_version))
                    .setText(mStore.getStore().getString(RestApi.TAG_XML_VERSION, "<no data>"));
        } else {
            showQuestionsScreen(url);
        }
    }

    @Override
    public void onFailure(String url) {
        Toast.makeText(this, "Failure: Getting questions from server.", Toast.LENGTH_SHORT).show();
    }
}
