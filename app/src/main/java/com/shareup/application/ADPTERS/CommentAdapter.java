package com.shareup.application.ADPTERS;

import com.shareup.application.ADPTERS.BASE.GenericAdapter;
import com.shareup.model.Comment;
import com.shareup.model.Post;

import java.util.List;

public class CommentAdapter extends GenericAdapter<Comment> {

    public CommentAdapter(List<Comment> items, int layoutId, InitializeViewHolder initializeViewHolder, BindViewHolder<Comment> bindViewHolder) {
        super(items, layoutId, initializeViewHolder, bindViewHolder);
    }
}
