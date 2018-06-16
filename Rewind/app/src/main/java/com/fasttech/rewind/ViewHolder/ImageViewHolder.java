package com.fasttech.rewind.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasttech.rewind.Interface.ItemClickListener;
import com.fasttech.rewind.R;

/**
 * Created by dell on 6/14/2018.
 */

public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {
    public TextView uploadName;
    public ImageView imageView;

    private ItemClickListener itemClickListener;

    public ImageViewHolder(View itemView) {
        super(itemView);

        uploadName = (TextView)itemView.findViewById(R.id.item_name);
        imageView = (ImageView)itemView.findViewById(R.id.item_image);
        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        //contextMenu.setHeaderTitle("Play Video");
        //contextMenu.add(0,0,getAdapterPosition(),"Play");
        //contextMenu.add(0,1,getAdapterPosition(), CurrentUser.DELETE);


    }
}
