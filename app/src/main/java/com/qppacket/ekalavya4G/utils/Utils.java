package com.qppacket.ekalavya4G.utils;

/**
 * Created by gorillalogic on 6/12/15.
 */
public class Utils {

//    public static final String URL_VIEW_QUESTION = "http://www.qppacket.com/StudentPages/qpPacketDailyTest.xml";
    public static final String URL_VIEW_QUESTION = "http://www.qppacket.com/mobile/QPPacketDailyTest.xml";
    public static final String URL_WHY_EKALAVYA = "http://qppacket.com/mobile/Whyekalavya.xml";
    public static final String URL_ENTRANCE_NEWS = "http://qppacket.com/mobile/Entrancenews.xml";
    public static final String URL_DETAILED_ANSWERS = "http://www.qppacket.com/mobile/showAnswers.aspx";

    public static final String PREFIX_IMAGE = "http://qppacket.com/QPIMages/";

    public static final String TAG_CORRECT_ANS_MARK = "CorrectAnsMark";
    public static final String TAG_WRONG_ANS_MARK = "WrongAnsMinusMark";
    public static final String TAG_SECONDS_PER_QUESTION = "QuestionPerSecond";

    public static final int CORRECT_ANSWER_MARKS = 1;
    public static final double WRONG_ANSWER_MARKS = -0.25;
    public static final double AVG_TIME_ONE_QUESTION = 0.75; // min

    public static String parseUrlForFilename(String url) {
        String[] splitted = url.split("/");
        return splitted[splitted.length-1];
    }
}
