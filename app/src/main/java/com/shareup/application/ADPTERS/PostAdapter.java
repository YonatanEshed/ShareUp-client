package com.shareup.application.ADPTERS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shareup.application.ADPTERS.BASE.GenericAdapter;
import com.shareup.application.R;
import com.shareup.model.Post;

import java.util.List;

public class PostAdapter extends GenericAdapter<Post> {

    public PostAdapter(List<Post> items, int layoutId, InitializeViewHolder initializeViewHolder, BindViewHolder<Post> bindViewHolder) {
        super(items, layoutId, initializeViewHolder, bindViewHolder);
    }
}