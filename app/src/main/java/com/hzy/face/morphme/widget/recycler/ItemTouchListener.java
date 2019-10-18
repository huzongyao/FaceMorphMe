package com.hzy.face.morphme.widget.recycler;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchListener implements RecyclerView.OnItemTouchListener {

    private MovableAdapter mAdapter;
    private GestureDetectorCompat mGestureDetectorCompat;
    private RecyclerView mRecyclerView;
    private ItemTouchHelper mTouchHelper;
    private ItemClickListener mItemClickListener;

    public ItemTouchListener(RecyclerView recyclerView, MovableAdapter adapter) {
        mAdapter = adapter;
        ItemTouchCallback callback = new ItemTouchCallback(adapter);
        mTouchHelper = new ItemTouchHelper(callback);
        mTouchHelper.attachToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mGestureDetectorCompat = new GestureDetectorCompat(mRecyclerView.getContext(),
                new ItemTouchHelperGestureListener());
    }

    public void setItemClickListener(ItemClickListener l) {
        mItemClickListener = l;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetectorCompat.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetectorCompat.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }


    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View childViewUnder = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if (childViewUnder != null) {
                RecyclerView.ViewHolder childViewHolder = mRecyclerView.getChildViewHolder(childViewUnder);
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(childViewHolder);
                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            View childViewUnder = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if (childViewUnder != null) {
                RecyclerView.ViewHolder childViewHolder = mRecyclerView.getChildViewHolder(childViewUnder);
                // last one do not drag
                if (childViewHolder.getLayoutPosition() != mAdapter.getItemCount() - 1) {
                    mTouchHelper.startDrag(childViewHolder);
                }
                if (mItemClickListener != null) {
                    mItemClickListener.onLongClick(childViewHolder);
                }
            }
        }
    }
}
