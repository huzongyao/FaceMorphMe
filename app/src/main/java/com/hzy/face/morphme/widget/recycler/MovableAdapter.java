package com.hzy.face.morphme.widget.recycler;

import androidx.recyclerview.widget.RecyclerView;

public abstract class MovableAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    public abstract void onSwap(int p1, int p2);

    public abstract void onRemove(int position);
}
