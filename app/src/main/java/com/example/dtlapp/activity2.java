package com.example.dtlapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseApp;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class activity2 extends AppCompatActivity {

    static final int ACTION_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    TextView cords;
    String coordinates, times, remarkstxt;
    TextView timeholder;
    EditText remarks;
    Firebase f;
    FirebaseStorage storage;
    StorageReference mstorage;
    DatabaseReference mdatabse;
    ProgressDialog mprog;
    Intent data1;
    Uri downloadUri;
    StorageReference imagesRef;
    //DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    //DatabaseReference databaseReference;
    FirebaseFirestore db;
    Uri uri;
    Uri imgg;
    RadioGroup rdg;
    RadioButton grb;
    String selectedRB;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    double lat;
    double longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(("GarbageGone"));
        setContentView(R.layout.activity_activity2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        imageView = (ImageView) findViewById(R.id.pictHolder);
        cords = (TextView) findViewById(R.id.Cords);
        timeholder = (TextView) findViewById(R.id.timeholder);
        remarks = (EditText) findViewById(R.id.remarks);
        setSupportActionBar(toolbar);
        mprog = new ProgressDialog(this);
        //Firebase.setAndroidContext(this);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mstorage = FirebaseStorage.getInstance().getReference("Photos");
        mdatabse = FirebaseDatabase.getInstance().getReference("Photos");

        rdg = (RadioGroup) findViewById(R.id.radiogroup);

        //f = new Firebase("https://garbagegone-fa7e4.firebaseio.com/");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("message");

        //databaseReference.setValue("Hello, World!");

        //databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://garbagegone-fa7e4.firebaseio.com/");

        //databaseReference.setValue("Hello, World!");

        db = FirebaseFirestore.getInstance();
/*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

 */
    }

    private Uri getImageUri(Context applicationContext, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(activity2.this.getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }


    public void takePhoto(View view) {

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            ActivityCompat.requestPermissions(activity2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);


        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            getcurrentloc();
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getcurrentloc();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            //if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "camera permission granted", Toast.LENGTH_SHORT).show();
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
            //}
            // else {
            //Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            //}
        }
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            getcurrentloc();
        } else {
            Toast.makeText(activity2.this, "Location Permission denied, Please grant permission and try capturing again", Toast.LENGTH_LONG).show();
        }
    }


    private void getcurrentloc() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(activity2.this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(activity2.this).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                    lat = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    longi = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                }
            }
        }, Looper.getMainLooper());

    }


    public void dosomething(View view) {
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            imageView.setImageBitmap(photo);
        }
    }*/



    public String currentDateandTime;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            imgg = data.getData();

            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            imageView.setImageBitmap(photo);

            imgg = getImageUri(getApplicationContext(), photo);
            if (imgg == (null)) {
                Toast.makeText(activity2.this, "DATA IS NULL", Toast.LENGTH_LONG).show();
            }

            data1 = data;

        }


        if (requestCode == CAMERA_REQUEST) {

            if (resultCode == RESULT_OK) {
                //previewCapturedImage();
                if (lat != 0 && longi != 0) {
                    // \n is for new line

                    cords.setText("geo:" + lat + "," + longi);
                    coordinates = ("geo:0,0?q=" + lat + "," + longi + "(Pin)");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
                    currentDateandTime = sdf.format(new Date().getTime());
                    timeholder.setText("Time: " + currentDateandTime);
                    times = ("Time: " + currentDateandTime);

                    //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                } else {
                    // Can't get location.
                    // GPS or network is not enabled.
                    // Ask user to enable GPS/network in settings.

                }

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "Cancelled", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Error!", Toast.LENGTH_SHORT)
                        .show();
            }
        }




    }


    public void submitb(View view) {
        //DatabaseReference conditionRef = mRootRef.child("condition");
        //final DatabaseReference time = mRootRef.child("Time");
        //final DatabaseReference loc = mRootRef.child("Location");
        //final DatabaseReference rem = mRootRef.child("Remarks");


        remarkstxt = remarks.getText().toString();
        int RadioID = rdg.getCheckedRadioButtonId();
        grb = findViewById(RadioID);
        selectedRB = (String) grb.getText();
        Toast.makeText(this,"SELECTED:"+selectedRB,Toast.LENGTH_LONG);
        if(remarkstxt.equals(""))
        {
            Toast.makeText(activity2.this,"Please Enter Remarks",Toast.LENGTH_LONG).show();
        }
        else if(coordinates== null || times == null)
        {
             Toast.makeText(activity2.this,"Location Disabled, PLease enable location services and try capturing again",Toast.LENGTH_LONG).show();
        }
        else
        {

            mprog.setMessage("Uploading...");
            mprog.show();
            UploadFile();
        /*
        conditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
            if (currentDateandTime == null) {
                Toast.makeText(getApplicationContext(), "NO TIME STAMP captured", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Time Stamp: " + currentDateandTime, Toast.LENGTH_LONG).show();


                Bundle extras = data1.getExtras();
                Bitmap bitmap = (Bitmap) data1.getExtras().get("data");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dataBAOS = baos.toByteArray();
                //imageView.setImageBitmap(bitmap);

                final StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://garbagegone-fa7e4.appspot.com/");
                //final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://garbagegone-fa7e4.firebaseio.com/");

                imagesRef = mstorage.child("filename" + new Date().getTime());

                uri = data1.getData();
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpg")
                        .build();


                //StorageReference filepath = mstorage.child("Photos").child(uri.getLastPathSegment());
                UploadTask uploadTask = mstorage.putBytes(dataBAOS);

                //databaseReference.setValue("Blllllwwww");

                //databaseReference.setValue(timeholder.getText());
                //databaseReference.setValue(remarks.getText());
                //databaseReference.setValue(cords.getText());
                //Toast.makeText(activity2.this,"Successfull cords:" + cords.getText(),Toast.LENGTH_LONG).show();
                System.out.println("Remarks:" + remarks.getText());

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Uri downloadurl = storageRef.getStorage().getDownloaduUrl();

                        //DatabaseReference newPost = databaseReference.push();
                        //newPost.child("Time").setValue(timeholder.getText());
                        //newPost.child("Location").setValue(cords.getText());
                        //newPost.child("Remarks").setValue(remarks.getText());
                        //newPost.child("Image").setValue(downloadUri);


                        Toast.makeText(activity2.this, "Successfull", Toast.LENGTH_LONG);
                        mprog.dismiss();
                        //finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }



    }
    private String getFileExtension(Uri gfeuri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(gfeuri));
    }

    private void UploadFile()
    {

        //Toast.makeText(activity2.this,"Successfull cords:" + cords.getText(),Toast.LENGTH_LONG).show();
        if(imgg != null)
        {
            StorageReference filereRef = mstorage.child(+System.currentTimeMillis()+"."+getFileExtension(imgg));
            filereRef.putFile(imgg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    /*
                    mprog.setProgress(0);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mprog.setProgress(0);
                        }
                    },500);

                     */

                    /*
                    Toast.makeText(activity2.this,"Upload Successfull!",Toast.LENGTH_SHORT).show();
                    Upload up = new Upload(coordinates.toString().trim(),times,remarkstxt,taskSnapshot.getUploadSessionUri().toString());
                    String uploadID = mdatabse.push().getKey();
                    mdatabse.child(uploadID).setValue(up);
                    */
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();

                    //Log.d(TAG, "onSuccess: firebase download url: " + downloadUrl.toString()); //use if testing...don't need this line.
                    Upload upload = new Upload(coordinates.toString().trim(),times,remarkstxt,downloadUrl.toString(),selectedRB);

                    String uploadId = mdatabse.push().getKey();
                    mdatabse.child(uploadId).setValue(upload);

                    //mdatabse.child(uploadID).setValue("YEEEEEEEEEEEEE");

                    startActivity(new Intent(activity2.this, MainActivity.class));


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(activity2.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    //mprog.setProgress((int) progress);

                }
            });
        }
        else
        {
            Toast.makeText(activity2.this,"NO IMAGE CAPTURED",Toast.LENGTH_LONG).show();
        }

    }


}
