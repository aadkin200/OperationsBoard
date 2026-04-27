package apsoftware.operationsboard.dto;

public class CommentCreateRequest {

    private String message;

    public CommentCreateRequest() {}

    public String getMessage() { return message; }

    public void setMessage(String message) {
        this.message = message;
    }
}
