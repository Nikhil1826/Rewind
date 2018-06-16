package com.fasttech.rewind;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.fasttech.rewind.Model.ImageClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;


public class ViewItemActivity extends AppCompatActivity {
    String ItemId="";
    FirebaseDatabase database;
    DatabaseReference items;
    ImageView itemImage;


    ImageClass item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        database = FirebaseDatabase.getInstance();
        items = database.getReference("Upload");
        itemImage = (ImageView)findViewById(R.id.itemImage);

        if(getIntent()!=null){
            ItemId = getIntent().getStringExtra("ItemId");
        }

        if(!ItemId.isEmpty() && ItemId!=null){
            getItem(ItemId);
        }

    }


    private void getItem(String itemId) {
        items.child(itemId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                item = dataSnapshot.getValue(ImageClass.class);
                    Picasso.with(getBaseContext()).load(item.getImage()).into(itemImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
