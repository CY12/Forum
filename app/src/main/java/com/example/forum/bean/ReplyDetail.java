package com.example.forum.bean;

import java.util.List;

public class ReplyDetail {
    private Comment comment;
    private List<Reply> replyList;

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public List<Reply> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<Reply> replyList) {
        this.replyList = replyList;
    }

}
