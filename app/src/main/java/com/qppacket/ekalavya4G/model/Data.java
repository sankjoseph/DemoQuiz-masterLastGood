package com.qppacket.ekalavya4G.model;

import java.util.List;

/**
 * Created by gorillalogic on 6/12/15.
 */
public class Data {
    private static Data mInstance;

    private List<QuestionPaper> mPapers;
    private List<Question> mQuestions;
    private Question mQuestion;
    private String mUsername;

    private Data() {}

    public static Data getInstance() {
        if (mInstance == null)
            mInstance = new Data();
        return mInstance;
    }

    public void setQuestions(List<Question> questions) {
        mQuestions = questions;
    }

    public List<Question> getAllQuestions() {
        return mQuestions;
    }

    public Question getCurrentQuestion() {
        return mQuestion;
    }

    public String getUsername() {
        return mUsername == null || mUsername.isEmpty() ? "Username" : mUsername;
    }

    public void setCurrentQuestion(Question q) {
        mQuestion = q;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public void setQuestionPaperList(List<QuestionPaper> list) {
        mPapers = list;
    }

    public List<QuestionPaper> getQuestionPapersList() {
        return mPapers;
    }
}
