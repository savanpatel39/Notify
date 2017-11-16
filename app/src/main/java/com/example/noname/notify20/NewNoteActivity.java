package com.example.noname.notify20;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class NewNoteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    private DBHandler db;
    boolean btnShown = false;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private int id;
    private TextView titlebox;
    private TextView detailsbox;
    private LinearLayout iLL;
    private TextView dateLbl;
    //private Button btnPlay;
    private Note temp;
    private String date;
    private int cat_id;
    private boolean loc_flag = false;
    private String statusFlag;
    private Double lon;
    private Double lat;
    private int imgCount;
    private int audioCount;
    private int counter;
    private int prevLoc = -1;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private MediaRecorder myAudioRecorder;
    private MediaPlayer m;
    private ArrayList<String> audioPaths;
    private ArrayList<String> imgPaths;
    private String outputFile = null;
    private ListView audioList;
    private ArrayAdapter<String> audioAdapter;

    FloatingActionButton up;
    FloatingActionButton cam;
    FloatingActionButton gal;
    FloatingActionButton map;
    FloatingActionButton rec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initComponents();
        setListners();

        Intent i = getIntent();
        statusFlag = i.getStringExtra("Status");
        cat_id = i.getIntExtra("cid", -1);
        Log.i("CatId", "New Note Activity Cid : " + cat_id);

        if (statusFlag.equals("edit")) {
            id = i.getIntExtra("nid", -1);
            //Toast.makeText(this, "Edit note!! : " + id, Toast.LENGTH_SHORT).show();
            date = "";
            Log.i("Nid", "NewNoteActivity : " + id);
            loadAllData(id);
        } else if (statusFlag.equals("new")) {
            Toast.makeText(NewNoteActivity.this, "Searching for location...", Toast.LENGTH_LONG).show();
            //location manager
            date = dateToString(new Date(), "MMM,dd yyyy hh:mm:ss");
            Log.i("Any", "Date : " + date);
            dateLbl.setText("Created on : " + date);

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //
            //
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10);
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},20);
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
            }
            else
            {
                loc_flag = false;
            }
            if( loc_flag )
            {
                Log.i("Loc","True");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
            else
            {
                Log.i("Loc","False");
            }
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            //Toast.makeText(this,"New note!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            case 10:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    loc_flag = true;
                    Log.i("Loc",""+loc_flag);
                }
                break;
            case 20:
                if(grantResults.length>0 && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    //Assign a true false flag hre for saving image here.....
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            if(m != null)
            {
                m.stop();
                m.reset();
                m = null;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save) {

            if(titlebox.getText().length() == 0)
            {
//                Log.i("Save", "inside first if...");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (!isFinishing()) {
                            new AlertDialog.Builder(NewNoteActivity.this)
                                    .setTitle("Notify")
                                    .setMessage("Please enter title to save note...")
                                    .setCancelable(false)
                                    .setPositiveButton("Got !t", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            titlebox.requestFocus();
                                        }
                                    }).create().show();
                        }
                    }
                });

            }
            else if (detailsbox.getText().length() == 0)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (!isFinishing()) {
                            new AlertDialog.Builder(NewNoteActivity.this)
                                    .setTitle("Notify")
                                    .setMessage("Please enter description to save note...")
                                    .setCancelable(false)
                                    .setPositiveButton("Got !t", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            detailsbox.requestFocus();
                                        }
                                    }).create().show();
                        }
                    }
                });
            }
            else
            {
                //save data
                Log.i("DB","In Save func");
                //check here for empty title or content //typical dialogue box
                saveDataToDatabase();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    new AlertDialog.Builder(NewNoteActivity.this)
                            .setTitle("Notify")
                            .setMessage("Do you want to save note before you go?")
                            .setCancelable(false)
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(NewNoteActivity.this,MainActivity.class);
                                    startActivity(i);
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveDataToDatabase();
                                    Intent i = new Intent(NewNoteActivity.this,MainActivity.class);
                                    startActivity(i);
                                }
                            }).create().show();

                }
            });
//            Intent i = new Intent(NewNoteActivity.this,MainActivity.class);
//            startActivity(i);
        }
//        else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadAllData(int id)
    {
        temp = db.getNoteData(id);
        if( temp != null)
        {
            titlebox.setText(temp.getNtitle());
            detailsbox.setText(temp.getNcontent());
            dateLbl.setText("Created on : "+ temp.getNdate());

            lat = Double.parseDouble(temp.getNlat());
            lon = Double.parseDouble(temp.getNlon());

            setImageOnScrolView();
            setAudioOnListView();
        }
        else
        {
            Log.i("Error", "Object not created!");
        }
    }

    private void initComponents()
    {
        audioPaths = new ArrayList<>();
        imgPaths = new ArrayList<>();
        counter = 0;

        db = new DBHandler(this,null,null,1);

        titlebox = (TextView) findViewById(R.id.titlebox);
        detailsbox = (TextView) findViewById(R.id.detailsbox);
        iLL = (LinearLayout) findViewById(R.id.imgLL);
        dateLbl = (TextView) findViewById(R.id.dateLbl);
       // btnPlay = (Button) findViewById(R.id.recFile);

        up = (FloatingActionButton) findViewById(R.id.up);
        cam = (FloatingActionButton) findViewById(R.id.cam);
        gal = (FloatingActionButton) findViewById(R.id.gal);
        map = (FloatingActionButton) findViewById(R.id.map);
        rec = (FloatingActionButton) findViewById(R.id.rec);

        cam.setVisibility(View.INVISIBLE);
        gal.setVisibility(View.INVISIBLE);
        map.setVisibility(View.INVISIBLE);
        rec.setVisibility(View.INVISIBLE);

        audioList = (ListView) findViewById(R.id.audioList);
        m = new MediaPlayer();
    }

    private void beforeRec()
    {
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/notify-recording" + ts + ".3gp";

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
    }

    private void setListners() {

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnShown) {
                    cam.setVisibility(View.INVISIBLE);
                    gal.setVisibility(View.INVISIBLE);
                    map.setVisibility(View.INVISIBLE);
                    rec.setVisibility(View.INVISIBLE);

                    up.setImageResource(R.drawable.up);

                    btnShown = false;
                } else {
                    cam.setVisibility(View.VISIBLE);
                    gal.setVisibility(View.VISIBLE);
                    map.setVisibility(View.VISIBLE);
                    rec.setVisibility(View.VISIBLE);

                    up.setImageResource(R.drawable.down);

                    btnShown = true;
                }
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( (lat != null) && (lon != null) )
                {
                    Intent i = new Intent(NewNoteActivity.this,MapsActivity.class);
                    i.putExtra("Lat",lat);
                    i.putExtra("Lon",lon);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(NewNoteActivity.this,"Location not available", Toast.LENGTH_LONG).show();
                }
            }
        });

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });

        gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
            }
        });

        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rec.getBackgroundTintList() == ColorStateList.valueOf(Color.WHITE)) {

                    try {
                        beforeRec();
                        myAudioRecorder.prepare();
                        myAudioRecorder.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    rec.setBackgroundTintList(ColorStateList.valueOf(Color.argb(90, 255, 0, 0)));
                    Toast.makeText(getApplicationContext(), "Recording...", Toast.LENGTH_SHORT).show();
                } else {
                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    myAudioRecorder = null;
                    Log.i("Recording file path: ", outputFile);
                    if(audioAdapter != null) {
                        audioPaths.add(outputFile);
                        audioAdapter.notifyDataSetChanged();

                    }
                    else {
                        audioAdapter = new ArrayAdapter<String>(NewNoteActivity.this, android.R.layout.simple_expandable_list_item_1, audioPaths);
                        audioList.setAdapter(audioAdapter);
                        audioPaths.add(outputFile);
                        audioAdapter.notifyDataSetChanged();
                        audioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                if (position == prevLoc) {
                                    counter++;
                                } else {
                                    counter = 0;
                                    m.stop();
                                    m.reset();
                                }
                                if ((counter % 2) == 0) {
                                    prevLoc = position;
                                    try {
                                        m.setDataSource(audioPaths.get(position));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        m.prepare();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    m.start();
                                } else {
                                    //  MediaPlayer m = new MediaPlayer();
                                    m.stop();
                                    m.reset();
                                }
                                Log.i("Rec", "Clicked AudioList View...");
                            }
                        });

                    }
//                    audioAdapter.clear();
//                    audioAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,audioPaths);
//                    audioList.setAdapter(audioAdapter);
                    //btnPlay.setVisibility(View.VISIBLE);

                    rec.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    Toast.makeText(getApplicationContext(), "Audio Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        btnPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                playRecorderAudio(outputFile);
//            }
//        });
    }

    private String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    private void saveDataToDatabase()
    {
        if(statusFlag.equals("edit"))
        {
            Log.i("Upd","In New : "+statusFlag);
            Log.i("Save","In save CatId If : "+cat_id);
            Note tempNote = new Note(id, cat_id, titlebox.getText().toString(), detailsbox.getText().toString(),null, "", "");
            db.updateNote(tempNote);

            Log.i("Img", "Count : " + imgCount);
            Log.i("Img", "Count Limit : " + imgPaths.size());

            if(!imgPaths.isEmpty())
            {
                for (int i = imgCount; i < imgPaths.size();i++)
                {
                    Log.i("Img","In for : "+imgPaths.get(i));
                    db.insertImageIntoDatabase(id,imgPaths.get(i));
                }
            }

            Log.i("Reco", "Count : " + audioCount);
            Log.i("reco","Count Limit : "+audioPaths.size());

            if(!audioPaths.isEmpty())
            {
                for (int i = audioCount; i < audioPaths.size(); i++) {
                    Log.i("Reco", "In for : " + audioPaths.get(i));
                    db.insertAudioIntoDatabase(id, audioPaths.get(i));
                }
            }
        }
        else if(statusFlag.equals("new"))
        {
            Log.i("Upd","In Edit : "+statusFlag);
            Log.i("CatId","In save CatId Else : "+cat_id);
            Log.i("","");
            Note tempNote = null;
            if(lon != null && lat != null) {
                tempNote = new Note(0, cat_id, titlebox.getText().toString(), detailsbox.getText().toString(), date, lon.toString(), lat.toString());
            }
            else
            {
                tempNote = new Note(0, cat_id, titlebox.getText().toString(), detailsbox.getText().toString(), date, "-79.171617", "43.770697");
            }
            if (tempNote != null)
            {
                db.insertIntoNotes(tempNote);

                int tempId = 0;
                tempId = db.getLastNid();
                //Inserting Audio paths into database....
                if(!audioPaths.isEmpty())
                {
                    for (String path : audioPaths) {
                        db.insertAudioIntoDatabase(tempId, path);
                        Log.i("Aud", "Path : " + path);
                    }
                }

                if(!imgPaths.isEmpty())
                {
                    for (String path : imgPaths) {
                        db.insertImageIntoDatabase(tempId, path);
                        Log.i("Aud", "Path : " + path);
                    }
                }

//                Intent i = new Intent(NewNoteActivity.this, NoteActivity.class);
//                i.putExtra("cid", cat_id);
//                Log.i("CatId", "Sav" + cat_id);
//                startActivity(i);
            }
            else
            {
                //Print something here in case of failure in creation of NOTE object
                Log.i("DB", "Empty Object");
            }
        }
        Intent i = new Intent(NewNoteActivity.this, NoteActivity.class);
        i.putExtra("cid", cat_id);
        Log.i("CatId", "Save func : " + cat_id);
        startActivity(i);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if(location != null)
        {
            Toast.makeText(NewNoteActivity.this,"Location found", Toast.LENGTH_LONG).show();
//            Toast.makeText(this,"Lat : "+location.getLatitude()+" Lon : "+location.getLongitude(), Toast.LENGTH_LONG).show();
            lat = location.getLatitude();
            lon = location.getLongitude();
            Log.i("Loc", "Lat : " + lat);
            Log.i("Loc","Lon : "+lon);
            locationManager.removeUpdates(this);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
//        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//
//        File destination = new File(Environment.getExternalStorageDirectory(),
//                System.currentTimeMillis() + ".jpg");
//
//        FileOutputStream fo;
//        try {
//            destination.createNewFile();
//            fo = new FileOutputStream(destination);
//            fo.write(bytes.toByteArray());
//            fo.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Log.i("Img", "" + getCapturedImagePath());
        ImageView img = new ImageView(this);
        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(450, 450);
        param.setMargins(10, 0, 0, 10);
        img.setLayoutParams(param);
        img.setImageURI(data.getData());
        Log.i("Img", "Img Path from captured : " + data.getData().getPath());
        iLL.addView(img);
        imgPaths.add(getCapturedImagePath());
// /storage/external_SD/UCDownloads/Bey yaar - 320KBps/Bey Yaar - Cover.jpg
//        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
//        Uri selectedImageUri = data.getData();
//        String[] projection = { MediaStore.MediaColumns.DATA };
//        Cursor cursor = managedQuery(selectedImageUri, projection, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//        cursor.moveToFirst();
//
//        String selectedImagePath = cursor.getString(column_index);
//
//        Bitmap bm;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(selectedImagePath, options);
//        final int REQUIRED_SIZE = 200;
//        int scale = 1;
//        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
//                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
//            scale *= 2;
//        options.inSampleSize = scale;
//        options.inJustDecodeBounds = false;
//        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        ImageView img = new ImageView(this);
        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(450, 450);
        param.setMargins(10, 0, 0, 10);
        img.setLayoutParams(param);
        img.setImageURI(data.getData());
        Log.i("Img", "Img Path from gallery: " + getSelectedImagePath(data.getData()));
        iLL.addView(img);
        imgPaths.add(getSelectedImagePath(data.getData()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //temp for last captured image
    public String getCapturedImagePath() {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        int column_index_data = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToLast();

        return cursor.getString(column_index_data);
    }

    //temp for selected image from gallery
    public String getSelectedImagePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void setImageOnScrolView()
    {
        this.imgPaths = db.getImagePathsFromDatabase(id);
        imgCount = imgPaths.size();
        for (String path : imgPaths)
        {
            ImageView img = new ImageView(this);
            FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(450, 450);
            param.setMargins(10, 0, 0, 10);
            img.setLayoutParams(param);
            img.setImageURI(Uri.parse(path));
            iLL.addView(img);
        }
    }

    private void setAudioOnListView()
    {
        this.audioPaths = db.getAudioPathsFromDatabase(id);
        audioCount = audioPaths.size();

        //Change from here!!!!
        Log.i("Rec", "Count of audioPath : " + audioCount);
        //countryArray.addAll(Arrays.asList(countries));

        audioAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,audioPaths);
        audioList.setAdapter(audioAdapter);
//        audioList.setChoiceMode(audioList.CHOICE_MODE_MULTIPLE);

        audioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == prevLoc) {
                    counter++;
                } else {
                    counter = 0;
                    m.stop();
                    m.reset();
                }
                if ((counter % 2) == 0) {
                    prevLoc = position;
                    try {
                        m.setDataSource(audioPaths.get(position));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        m.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    m.start();
                } else {
                    //  MediaPlayer m = new MediaPlayer();
                    m.stop();
                    m.reset();
                }
                Log.i("Rec", "Clicked AudioList View...");
            }
        });

        audioList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Long","Long Click detected");
                return false;
            }
        });
    }
}