package app.insti.api.request;

import com.google.gson.annotations.SerializedName;

public class QuestionCreateRequest {
    @SerializedName("question")
    private String question;

    public QuestionCreateRequest(String text) {
        this.question = text;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String text) {
        this.question = text;
    }
}
