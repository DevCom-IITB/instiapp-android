package app.insti.api.response;

public class QuestionCreateResponse {
    private String result;
    private String questionId;

    public QuestionCreateResponse(String result, String questionId) {
        this.result = result;
        this.questionId = questionId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
}
