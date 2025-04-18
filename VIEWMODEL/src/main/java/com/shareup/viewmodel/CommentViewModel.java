package com.shareup.viewmodel;

import android.app.Application;

import com.shareup.model.ApiMethod;
import com.shareup.model.Comment;
import com.shareup.service.PostService;
import com.shareup.viewmodel.BASE.BaseViewModel;

public class CommentViewModel extends BaseViewModel<Comment> {
    private final PostService postService;

    public CommentViewModel(Application application) {
        super();
        this.postService = new PostService(application);
    }

    public void getComments(String postId) {
        executeListApiCall(callback -> postService.getComments(postId, callback::onResult));
    }

    public void addComment(String postId, String content) {
        Comment comment = new Comment();
        comment.setContent(content);
        executeApiCall(ApiMethod.POST, callback -> postService.addComment(postId, comment.toMap(), callback::onResult));
    }

    public void deleteComment(String postId, String commentId) {
        executeApiCall(ApiMethod.DELETE, callback -> postService.deleteComment(postId, commentId, callback::onResult));
    }
}
