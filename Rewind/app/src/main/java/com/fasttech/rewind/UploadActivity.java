package com.fasttech.rewind;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.fasttech.rewind.Interface.ItemClickListener;
import com.fasttech.rewind.Model.ImageClass;
import com.fasttech.rewind.ViewHolder.ImageViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class UploadActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView txtFullName;

    FirebaseDatabase database;
    DatabaseReference uploads;
    FirebaseRecyclerAdapter<ImageClass,ImageViewHolder> adapter;
    FirebaseStorage storage;
    StorageReference storageReference;
    MaterialEditText edtNameDialog;
    FButton btnUpload,btnSelect,btnUploadImage,btnUploadVideo;

    String MobileId="";
    ImageClass item;

    RecyclerView recycler_upload;
    RecyclerView.LayoutManager layoutManager;

    ImageClass newUpload;

    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("UPLOADS");
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        uploads = database.getReference("Upload");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Paper.init(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectDialog();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
       txtFullName = (TextView)headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(CurrentUser.cUser.getName());

        recycler_upload = (RecyclerView)findViewById(R.id.recycler_view);
        recycler_upload.setHasFixedSize(true);
        //layoutManager = new LinearLayoutManager(this);
        //recycler_upload.setLayoutManager(layoutManager);
        recycler_upload.setLayoutManager(new GridLayoutManager(this,3));

        if(getIntent()!=null){
            MobileId = getIntent().getStringExtra("MobileNo");
        }

        if(!MobileId.isEmpty() && MobileId!=null){
            loadUploadList(MobileId);
        }




    }

    private void showSelectDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(UploadActivity.this);
        alertDialog.setTitle("What You Want to Upload");
        alertDialog.setMessage("Image OR Video");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.imageorvideo,null);

        btnUploadImage = add_menu_layout.findViewById(R.id.btnUploadImage);
        btnUploadVideo = add_menu_layout.findViewById(R.id.btnUploadVideo);

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddImageDialog();
            }
        });

        btnUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             showAddVideoDialog();
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.show();
    }

    private void showAddVideoDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(UploadActivity.this);
        alertDialog.setTitle("Add New Video");
        alertDialog.setMessage("Enter the details");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_image_layout,null);

        edtNameDialog = add_menu_layout.findViewById(R.id.edtNameDialog);

        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseVideo();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saveUri==null || edtNameDialog.getText().toString().isEmpty()){
                    Toast.makeText(UploadActivity.this,"Please fill the entire details!!\nOR\nMake sure an Image is selected!!",Toast.LENGTH_LONG).show();
                }else
                    uploadVideo();

            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_image_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if(newUpload!=null){
                    uploads.push().setValue(newUpload);
                    Toast.makeText(getApplicationContext(), "New Upload"+"\""+newUpload.getName()+"\""+" added", Toast.LENGTH_SHORT).show();

                }

            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void uploadVideo() {
        final SpotsDialog dialog = new SpotsDialog(UploadActivity.this, R.style.Custom);
        dialog.setMessage("Uploading...");
        dialog.show();

        String videoName = UUID.randomUUID().toString();
        final StorageReference videoFolder = storageReference.child("videos/" + videoName);
        videoFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dialog.dismiss();
                Toast.makeText(UploadActivity.this, "Uploaded!!", Toast.LENGTH_SHORT).show();

                videoFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        newUpload = new ImageClass();
                        newUpload.setName(edtNameDialog.getText().toString());
                        newUpload.setMobile(MobileId);
                        newUpload.setImage(uri.toString());
                        newUpload.setType("video");

                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(UploadActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        //dialog.setMessage("Uploaded " + progress + "%");
                    }
                });
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),CurrentUser.pick_image_request);
    }

    private void showAddImageDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(UploadActivity.this);
        alertDialog.setTitle("Add New Image");
        alertDialog.setMessage("Enter the details");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_image_layout,null);

        edtNameDialog = add_menu_layout.findViewById(R.id.edtNameDialog);

        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saveUri==null || edtNameDialog.getText().toString().isEmpty()){
                    Toast.makeText(UploadActivity.this,"Please fill the entire details!!\nOR\nMake sure an Image is selected!!",Toast.LENGTH_LONG).show();
                }else
                    uploadImage();

            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_image_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if(newUpload!=null){
                    uploads.push().setValue(newUpload);
                    Toast.makeText(getApplicationContext(), "New Upload"+"\""+newUpload.getName()+"\""+" added", Toast.LENGTH_SHORT).show();

                }

            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();


    }

    private void uploadImage() {
        final SpotsDialog dialog = new SpotsDialog(UploadActivity.this, R.style.Custom);
        dialog.setMessage("Uploading...");
        dialog.show();

        String imageName = UUID.randomUUID().toString();
        final StorageReference imageFolder = storageReference.child("images/" + imageName);
        imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dialog.dismiss();
                Toast.makeText(UploadActivity.this, "Uploaded!!", Toast.LENGTH_SHORT).show();

                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        newUpload = new ImageClass();
                        newUpload.setName(edtNameDialog.getText().toString());
                        newUpload.setMobile(MobileId);
                        newUpload.setImage(uri.toString());
                        newUpload.setType("image");

                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(UploadActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        //dialog.setMessage("Uploaded " + progress + "%");
                    }
                });

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),CurrentUser.pick_image_request);
    }

    private void loadUploadList(String mobileId) {
        adapter = new FirebaseRecyclerAdapter<ImageClass, ImageViewHolder>(ImageClass.class, R.layout.upload_item, ImageViewHolder.class, uploads.orderByChild("mobile").equalTo(mobileId)
        ) {
            @Override
            protected void populateViewHolder(final ImageViewHolder viewHolder, ImageClass model, int position) {
                viewHolder.uploadName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, final int position, boolean isLongClick) {
                        database = FirebaseDatabase.getInstance();
                        uploads = database.getReference("Upload");
                        uploads.child(adapter.getRef(position).getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                item = dataSnapshot.getValue(ImageClass.class);
                                if(item.getType().equals("image")){
                                    Intent intent = new Intent(UploadActivity.this, ViewItemActivity.class);
                                    intent.putExtra("ItemId", adapter.getRef(position).getKey());
                                    startActivity(intent);
                                }else{
                                    Intent intent = new Intent(UploadActivity.this, ViewVideoActivtiy.class);
                                    intent.putExtra("ItemId", adapter.getRef(position).getKey());
                                    //intent.putExtra("videoUri", saveUri.toString());
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                });

            }
        };

        adapter.notifyDataSetChanged();
        recycler_upload.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CurrentUser.pick_image_request && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            saveUri = data.getData();
            btnSelect.setText("Item Selected");
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            Toast.makeText(getApplicationContext(),"Log out to return",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            Paper.book().destroy();
            Intent intent = new Intent(UploadActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
