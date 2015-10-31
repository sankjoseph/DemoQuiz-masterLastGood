package com.qppacket.ekalavya4G.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gorillalogic on 6/12/15.
 */
public class Question {
    public int qNo;
    public String question;
    public List<String> options = new ArrayList<>();
    public int correctch; // options index.
    public String subject;
    private boolean isVerified;

    private int mSelectedChoice = -1;

    public void setSelectedChoice(int selectedChoice) {
        mSelectedChoice = selectedChoice;
    }

    public boolean isAnswered() {
        return mSelectedChoice != -1;
    }

    public boolean isAnswerCorrect() {
        return mSelectedChoice == correctch;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public static Question parseJson(JSONObject data) {
        Question q = new Question();

        q.qNo = data.optInt("qNo");
        q.correctch = data.optInt("correctch");
        q.subject = data.optString("subject");
        q.question = data.optString("question");

        try {
            JSONArray os = new JSONArray(data.optString("options"));
            for (int i=0; i<os.length(); i++) {
                q.options.add(os.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return q;
    }

    public String toString() {
        JSONArray options = new JSONArray();
        for (String o : this.options) {
            options.put(o);
        }

        JSONObject q = null;
        try {
            q = new JSONObject()
                    .put("qNo", qNo)
                    .put("correctch", correctch)
                    .put("subject", subject)
                    .put("question", question)
                    .put("options", options);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return q.toString();
    }
}
