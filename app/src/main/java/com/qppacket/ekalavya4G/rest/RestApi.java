package com.qppacket.ekalavya4G.rest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.qppacket.ekalavya4G.model.Data;
import com.qppacket.ekalavya4G.model.Question;
import com.qppacket.ekalavya4G.model.Store;
import com.qppacket.ekalavya4G.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gorillalogic on 6/12/15.
 */
public class RestApi {
    public static final String TAG_XML_VERSION = "xmlversion";
    private static final String DOWNLOAD_PENDING_URLS = "download_pending_urls";
    public static final String OFFLINE_QUESTIONS = "offline_questions";

    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    private OnPostExecuteListener mExecuteListener;

    private String mProgressDialogMessage="";
    private String mUrl;
    private boolean mIsParseSuccessfull;
    private Store mStore;
    private File IMG_DOWNLOAD_DIR;
    private JSONObject mDownloadPendingUrls;
    private static int mDownloadIssued, mDownloadSuccess;
    private boolean mForceClose;
    private boolean mPostAfterDownloadComplete;

    public RestApi(Activity a) {
        mActivity = a;
        mStore = new Store(a);
        mDownloadPendingUrls = new JSONObject();
        IMG_DOWNLOAD_DIR = a.getExternalFilesDir(null) != null ? a.getExternalFilesDir(null) : a.getCacheDir();
    }

    public RestApi(Activity a, OnPostExecuteListener listener) {
        this(a);
        mExecuteListener = listener;
    }

    public void setPostExecuteListener(OnPostExecuteListener listener) {
        mExecuteListener = listener;
    }

    public void get(String url) {
        mUrl = url;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mProgressDialogMessage != null) {
                    mProgressDialog = new ProgressDialog(mActivity);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setMessage(mProgressDialogMessage.isEmpty() ? "Loading..." : mProgressDialogMessage);
                    mProgressDialog.show();
                }
            }

            @Override
            protected Void doInBackground(Void... voids) {
                parseXmlDoc();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (mDownloadIssued != mDownloadSuccess)
                    mProgressDialog.setMessage("Downloading latest question paper...");
                if (mExecuteListener != null) {
                    if (mIsParseSuccessfull == true)
                        if (isDownloadComplete())
                            mExecuteListener.onSuccess();
                        else
                            mPostAfterDownloadComplete = true;
                    else
                        mExecuteListener.onFailure();
                }
                dismissProgress();
            }
        }.execute();
    }

    protected void dismissProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            if (isDownloadComplete() || mForceClose)
                mProgressDialog.dismiss();
        }
    }

    private boolean isDownloadComplete() {
        return mDownloadIssued == mDownloadSuccess;
    }

    private void parseXmlDoc() {
        InputStream in = Api.getData(mUrl);
        String name = null, subject = null;
        boolean isDataControl = false;
        List<Question> questions = new ArrayList<>();
        if (in != null) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(in, "UTF-8");
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        name = parser.getName();
                        if (name.equalsIgnoreCase("xml")
                                || name.equalsIgnoreCase("questions")
                                || name.equalsIgnoreCase("question")) {
                            // ignore
                        } else if (isDataControl(name)) {
                            isDataControl = true;
                        } else {
                            subject = name;
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (!parser.getText().isEmpty()
                                && !parser.getText().trim().isEmpty()) {
                            if (isDataControl) {
                                isDataControl = false;
                                if (!parseControlData(parser.getText(), name)) {
                                    // Retrieve offline
                                    mForceClose = true;
                                    mIsParseSuccessfull = true;
                                    Data.getInstance().setQuestions(getOfflineQuestions());
                                    return;
                                }
                            } else {
                                questions.add(parseQuestion(parser.getText(), subject));
                            }
                        }
                    }
                    eventType = parser.next();
                }
                mIsParseSuccessfull = true;
                Data.getInstance().setQuestions(questions);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                mIsParseSuccessfull = false;
            } catch (IOException e) {
                e.printStackTrace();
                mIsParseSuccessfull = false;
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            storeOffline(questions);
        } else {
            // Retrieve offline
            mForceClose = true;
            mIsParseSuccessfull = true;
            Data.getInstance().setQuestions(getOfflineQuestions());
        }

    }

    private List<Question> getOfflineQuestions() {
        List<Question> questions = new ArrayList<>();
        try {
            JSONArray qs = new JSONArray(mStore.getStore().getString(OFFLINE_QUESTIONS, new JSONArray().toString()));
            for (int i=0; i<qs.length(); i++) {
                questions.add(Question.parseJson(qs.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questions;
    }

    private void storeOffline(List<Question> questions) {
        JSONArray qs = new JSONArray();
        for (Question q : questions) {
            try {
                qs.put(new JSONObject(q.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mStore.getStoreEditor().putString(OFFLINE_QUESTIONS, qs.toString()).commit();
    }

    private boolean parseControlData(String text, String name) {
        Log.i(".DemoQuiz", "parseControlData() text = " + text + "\nName = " + name);
        text = text.trim();
        if (name.equalsIgnoreCase(TAG_XML_VERSION)
                && mStore.getStore().getString(name, "").equalsIgnoreCase(text)) {
            return false;
        }
        mStore.getStoreEditor().putString(name, text).commit();
        return true;
    }

    private boolean isDataControl(String name) {
        return name.equalsIgnoreCase(TAG_XML_VERSION)
                || name.equalsIgnoreCase(Utils.TAG_CORRECT_ANS_MARK)
                || name.equalsIgnoreCase(Utils.TAG_WRONG_ANS_MARK)
                || name.equalsIgnoreCase(Utils.TAG_SECONDS_PER_QUESTION);
    }

    private Question parseQuestion(String q, String subject) {
        Log.i(".DemoQuiz", "text = " + q + "\nSubject = " + subject);
        Question question = new Question();
        String[] split = q.split("\r");
        String[] temp;
        String url = "";
        for (int i=1; i<split.length; i++) {
            if (!split[i].trim().isEmpty()) {
                if (i == 1) {
                    if (split[i].contains("../QPIMages/")) {
                        temp = split[i].split("../QPIMages/");
                        url = Utils.PREFIX_IMAGE + temp[1].substring(0, temp[1].indexOf('.')) + ".png";
                        question.question = IMG_DOWNLOAD_DIR.getAbsolutePath() + "/" + Utils.parseUrlForFilename(url);
                        new DownloadAsync(url).execute();
                        mDownloadIssued++;
                    } else {
                        temp = split[i].split(", ");
                        question.question = temp[1];
                    }
                    question.subject = subject;
                    question.qNo = Integer.parseInt(temp[0].substring(0, temp[0].indexOf(',')).trim());
                } else {
                    if (split[i].contains("../QPIMages/")) {
                        temp = split[i].split("../QPIMages/");
                        if (temp[0].contains(",oc,"))
                            question.correctch = i - 2;
                        url = Utils.PREFIX_IMAGE + temp[1].substring(0, temp[1].indexOf('.')) + ".png";
                        question.options.add(IMG_DOWNLOAD_DIR.getAbsolutePath() + "/" + Utils.parseUrlForFilename(url));
                        new DownloadAsync(url).execute();
                        mDownloadIssued++;
                    } else {
                        temp = split[i].split(", ");
                        if (temp[0].contains(",oc"))
                            question.correctch = i - 2;
                        question.options.add(temp[1].trim());
                    }
                }
            }
        }

        return question;
    }

    public void setMessage(String message) {
        mProgressDialogMessage = message;
    }

    private class DownloadAsync extends AsyncTask<Void, Void, Boolean> {
        private String mDownloadUrl;
        DownloadAsync(String url) {
            mDownloadUrl = url;
        }

        @Override
        protected Boolean doInBackground(Void... args) {
            Log.i(".DemoQuiz", "Downloading..." + mDownloadUrl);
            return downloadFile(mDownloadUrl);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            String key = Utils.parseUrlForFilename(mDownloadUrl);
            Log.i(".DemoQuiz", key + " Success: " + result.booleanValue());
            try {
                mDownloadPendingUrls = new JSONObject(mStore.getStore().getString(DOWNLOAD_PENDING_URLS, mDownloadPendingUrls.toString()));
                if (!result.booleanValue()) {
                    // Download failed!
                    mDownloadPendingUrls.put(key, mDownloadUrl);
                    mForceClose = true;
                } else {
                    // Download success
                    mDownloadSuccess++;
                    mDownloadPendingUrls.remove(key);
                }
                mStore.getStoreEditor().putString(DOWNLOAD_PENDING_URLS, mDownloadPendingUrls.toString()).commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dismissProgress();
            postAfterDownloadComplete();
        }

        private void postAfterDownloadComplete() {
            if (mPostAfterDownloadComplete && isDownloadComplete())
                mExecuteListener.onSuccess();
        }

        private boolean downloadFile(String url) {
            BufferedInputStream bufferinstream = null;
            InputStream content = Api.getData(url);
            FileOutputStream fos = null;
            File f = null;
            boolean downloaded = false;
            try {
                bufferinstream = new BufferedInputStream(content);

                f = new File(IMG_DOWNLOAD_DIR, Utils.parseUrlForFilename(url));
                fos = new FileOutputStream(f);
                byte data[] = new byte[5120];
                int count = 0;
                while ((count=bufferinstream.read(data)) != -1) {
                    fos.write(data, 0, count);
                }
                downloaded = true;

            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                downloaded = false;
            } finally {
                try {

                    if(content != null) {
                        content.close();
                    }
                    if(bufferinstream != null) {
                        bufferinstream.close();
                    }
                    if (fos != null) {
                        fos.flush();
                        fos.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //Delete the incomplete file.
            if (!downloaded && f != null) {
                f.delete();
            }
            return downloaded;
        }
    }
}
