package com.hzy.face.morphme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hzy.face.morphme.R;
import com.hzy.face.morphme.bean.FaceImage;
import com.hzy.face.morphme.widget.Ratio34ImageView;
import com.hzy.face.morphme.widget.recycler.MovableAdapter;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageGridAdapter extends MovableAdapter<ImageGridAdapter.ViewHolder> {

    private List<FaceImage> mDataList;

    public ImageGridAdapter(List<FaceImage> images) {
        this.mDataList = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_source_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ratio34ImageView imageView = holder.mSourceImageView;
        if (position < mDataList.size()) {
            FaceImage data = mDataList.get(position);
            Glide.with(imageView).load(data.path).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_head);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size() + 1;
    }

    @Override
    public void onSwap(int p1, int p2) {
        if (p1 < mDataList.size() && p2 < mDataList.size()) {
            Collections.swap(mDataList, p1, p2);
        }
    }

    @Override
    public void onRemove(int position) {
        if (position < mDataList.size()) {
            mDataList.remove(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.source_image_view)
        public Ratio34ImageView mSourceImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
