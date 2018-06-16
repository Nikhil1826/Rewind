package com.fasttech.rewind;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.fasttech.rewind.Model.ImageClass;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class ViewVideoActivtiy extends AppCompatActivity {
    VideoView mainVideoView;
    ImageView playBtn;
    TextView currentTimer;
    TextView durationTimer;
    ProgressBar currentProgress;

    String ItemId="";
    FirebaseDatabase database;
    DatabaseReference items;
    ImageClass item;

    FirebaseStorage storage;
    StorageReference storageReference;

    boolean isPlaying;
    int current = 0;
    int duration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video_activtiy);


        database = FirebaseDatabase.getInstance();
        items = database.getReference("Upload");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mainVideoView = (VideoView)findViewById(R.id.videoView);
        playBtn = (ImageView) findViewById(R.id.playButton);
        currentTimer = (TextView) findViewById(R.id.currentTimer);
        durationTimer = (TextView) findViewById(R.id.durationTimer);
        currentProgress = (ProgressBar) findViewById(R.id.currentProgress);
        currentProgress.setMax(100);
        isPlaying = false;

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
                String uri = item.getImage();
                Uri videoUri = Uri.parse(uri);
                mainVideoView.setVideoURI(videoUri);
                mainVideoView.requestFocus();

                mainVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                        if(i == mediaPlayer.MEDIA_INFO_BUFFERING_START){
                           // buffProgress.setVisibility(View.VISIBLE);
                        }else if(i == mediaPlayer.MEDIA_INFO_BUFFERING_END){
                            //buffProgress.setVisibility(View.INVISIBLE);

                        }
                        return false;
                    }

                });


                mainVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        duration = mediaPlayer.getDuration()/1000;
                        String durationString = String.format("%02d:%02d", duration/60, duration%60);
                        durationTimer.setText(durationString);
                    }
                });
                mainVideoView.start();
                isPlaying = true;
                playBtn.setImageResource(R.drawable.ic_pause_black_24dp);

                new VideoProgress().execute();

                playBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isPlaying){
                            mainVideoView.pause();
                            isPlaying = false;
                            playBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        }else{
                            mainVideoView.start();
                            isPlaying = true;
                            playBtn.setImageResource(R.drawable.ic_pause_black_24dp);
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        isPlaying = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isPlaying = false;
    }

    public class VideoProgress extends AsyncTask<Void, Integer, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            do{

                if(isPlaying) {
                    current = mainVideoView.getCurrentPosition() / 1000;
                    publishProgress(current);
                }



            }while (currentProgress.getProgress() <= 100);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            try {
                int currentPercent = current *100/duration;
                currentProgress.setProgress(currentPercent);
                String currentString = String.format("%02d:%02d", values[0] / 60, values[0] % 60);
                currentTimer.setText(currentString);
            }catch (Exception e){

            }
        }
    }



}
