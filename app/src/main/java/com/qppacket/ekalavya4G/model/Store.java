package com.qppacket.ekalavya4G.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.qppacket.ekalavya4G.utils.Utils;

/**
 * Created by gorillalogic on 7/21/15.
 */
public class Store {

    Context mContext;
    private SharedPreferences mPrefs;

    Store() {}

    public Store(Context c) {
        mContext = c;
    }

    public SharedPreferences getStore() {
        if (mPrefs == null)
            mPrefs = mContext.getSharedPreferences("LOCAL_STORE", Context.MODE_PRIVATE);
        return mPrefs;
    }

    public SharedPreferences.Editor getStoreEditor() {
        return getStore().edit();
    }

    public double getCorrectAnswerMark() {
        return Double.parseDouble(getStore().getString(Utils.TAG_CORRECT_ANS_MARK, "1"));
    }

    public double getWrongAnswerMark() {
        return -1 * Double.parseDouble(getStore().getString(Utils.TAG_WRONG_ANS_MARK, "-0.25"));
    }

    // Return no of seconds per question.
    public int getAverageTimePerQuestion() {
        return Integer.parseInt(getStore().getString(Utils.TAG_SECONDS_PER_QUESTION, "45"));
    }
}
