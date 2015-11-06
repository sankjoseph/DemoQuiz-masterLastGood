package com.qppacket.ekalavya4G.rest;

import android.app.Activity;
import android.os.AsyncTask;

import com.qppacket.ekalavya4G.model.Data;
import com.qppacket.ekalavya4G.model.QuestionPaper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gorillalogic on 11/5/15.
 */
public class QuestionPaperListApi extends RestApi {

    private String mUrl;

    public QuestionPaperListApi(Activity a) {
        super(a);
    }

    public void get(String url) {
        mUrl = url;
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                return parseXmlDoc();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (mExecuteListener != null) {
                    if (result)
                        mExecuteListener.onSuccess(mUrl);
                    else
                        mExecuteListener.onFailure(mUrl);
                }
            }
        }.execute();
    }

    private boolean parseXmlDoc() {
        List<QuestionPaper> papers = new ArrayList<>();
        InputStream in = Api.getData(mUrl);
        if (in != null) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(in, "UTF-8");
                int eventType = parser.getEventType();
                String name = null;
                boolean isName = false, isPath = false, isQAPath = false;
                QuestionPaper p = null;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        name = parser.getName();
                        if (name.equalsIgnoreCase("questionName")) {
                            isName = true;
                            p = new QuestionPaper();
                        } else if (name.equalsIgnoreCase("questionPath")) {
                            isPath = true;
                        } else if (name.equalsIgnoreCase("questionAnsPath")) {
                            isQAPath = true;
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (p != null) {
                            if (isName) {
                                isName = false;
                                p.name = parser.getText().trim();
                            } else if (isPath) {
                                isPath = false;
                                p.qPath = parser.getText().trim();
                            } else if (isQAPath) {
                                isQAPath = false;
                                p.qaPath = parser.getText().trim();
                                papers.add(p);
                            }
                        }
                    }
                    eventType = parser.next();
                }
                Data.getInstance().setQuestionPaperList(papers);
                return true;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
