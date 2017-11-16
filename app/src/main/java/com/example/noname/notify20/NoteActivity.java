package com.example.noname.notify20;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private RecyclerView nRecyclerView;
    public  NoteAdapter adapter;
    private RecyclerView.LayoutManager nLayoutManager;
    private DBHandler dbHandler;
    private int cat_id;
    private TextView noNote;
    private SearchView srcBarN;
    private int tempCat_id;
    private int nc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i("Any","In add note");
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

        doInitializations();
        searchFunc();
    }

    private List<Note> getData()
    {
//        cat_id = 1;
        List<Note> data = dbHandler.getNotes(cat_id);
        return data;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(srcBarN.getVisibility() == View.VISIBLE) {
            srcBarN.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            if(srcBarN.getVisibility() == View.GONE)
            {
                srcBarN.setVisibility(View.VISIBLE);
                srcBarN.setIconified(false);
                srcBarN.setQueryHint("Type here to search...");
                srcBarN.requestFocus();
            }
            else
            {
                srcBarN.setVisibility(View.GONE);
            }
            return true;
        }
        else if (id == R.id.add)
        {
            Intent i = new Intent(NoteActivity.this,NewNoteActivity.class);
            i.putExtra("Status", "new");
            Intent ti = getIntent();
            i.putExtra("cid", ti.getIntExtra("id",-1));

            Log.i("CatId", "Note Activity Add func Cid : " + ti.getIntExtra("id",-1));
            startActivity(i);
            Log.i("Any", "In add note");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        List<Note> srtNote = new ArrayList<>();

        nLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        nRecyclerView.setLayoutManager(nLayoutManager);
        nRecyclerView.addItemDecoration(new SpacesItemDecoration(0));

        if (id == R.id.srtDateAsc)
        {
            Log.i("Nav","Notes srtDateAsc" + cat_id);
            srtNote = dbHandler.getNotesAscByDate(cat_id);
            adapter = null;
            adapter = new NoteAdapter(NoteActivity.this, srtNote, cat_id);
            nRecyclerView.setAdapter(adapter);
        }
        else if (id == R.id.srtDateDsc)
        {
            srtNote = dbHandler.getNotesDescByDate(cat_id);
            Log.i("Nav","Notes srtDateDesc : " + srtNote.size());
            adapter = null;
            adapter = new NoteAdapter(NoteActivity.this, srtNote, cat_id);
            nRecyclerView.setAdapter(adapter);
        }
        else if (id == R.id.srtTitleAsc)
        {
            Log.i("Nav","Notes srtTitleAsc" + cat_id);
            srtNote = dbHandler.getNotesAscByTitle(cat_id);
            adapter = new NoteAdapter(NoteActivity.this, srtNote, cat_id);
            nRecyclerView.setAdapter(adapter);
        }
        else if (id == R.id.srtTitleDsc)
        {
            Log.i("Nav","Notes srtTitleDesc" + cat_id);
            srtNote = dbHandler.getNotesDescByTitle(cat_id);
            adapter = new NoteAdapter(NoteActivity.this, srtNote, cat_id);
            nRecyclerView.setAdapter(adapter);
        }
        else if (id == R.id.nav_home) {
            Intent i = new Intent(NoteActivity.this,MainActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cat_id == -1)
        {
            Intent i = getIntent();
            cat_id = i.getIntExtra("cid", -1);
            Log.i("CatId", "In resume..If..:" + cat_id);
            adapter = null;
            adapter = new NoteAdapter(NoteActivity.this,getData(),cat_id);
            nRecyclerView.setAdapter(adapter);
        }
    }

    public void doInitializations()
    {
        dbHandler = new DBHandler(this,null,null,1);

        srcBarN = (SearchView) findViewById(R.id.sBarNote);

        Intent i = getIntent();
        if( i != null) {
                cat_id = i.getIntExtra("id", -1);
                Log.i("CatId","In do Initializations() : " + cat_id);
                String temp = i.getStringExtra("checking");
                nc = i.getIntExtra("notescount", -1);
//                Log.i("CatId", " ID : " + cat_id);
//                Log.i("Int", " CHK : " + temp);
        }

        nRecyclerView = (RecyclerView) findViewById(R.id.noteRecyclerView);
        noNote = (TextView) findViewById(R.id.noNotes);
        if(nc != 0 )//Notes to preview
        {
            nRecyclerView.setHasFixedSize(true);

            nLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            nRecyclerView.setLayoutManager(nLayoutManager);
            nRecyclerView.addItemDecoration(new SpacesItemDecoration(-5));

//            Log.i("CatId", "Note Activity Cid : " + cat_id);
            adapter = new NoteAdapter(NoteActivity.this,getData(),cat_id);
            nRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            nRecyclerView.animate();
//            Log.i("Any","In view visible");
            nRecyclerView.setVisibility(View.VISIBLE);
            noNote.setVisibility(View.GONE);
        }
        else// No notes to preview
        {
            Log.i("Nav"," Recycler View Invisible");
            noNote.setVisibility(View.VISIBLE);
            nRecyclerView.setVisibility(View.GONE);
        }
    }


    private void searchFunc()
    {
        //*** setOnQueryTextFocusChangeListener ***
        srcBarN.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

//                Toast.makeText(getBaseContext(), String.valueOf(hasFocus),Toast.LENGTH_SHORT).show();
            }
        });

        //*** setOnQueryTextListener ***
        srcBarN.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                //searchInDatabase(query);
//                Toast.makeText(getBaseContext(), query, Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub

                searchInDatabase(newText);

//                Toast.makeText(getBaseContext(), newText, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void searchInDatabase(String query)
    {
        nLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        nRecyclerView.setLayoutManager(nLayoutManager);
        nRecyclerView.addItemDecoration(new SpacesItemDecoration(0));

        List<Note> srcNotes = dbHandler.searchAllNotes(query);
        adapter = null;
        if(!srcNotes.isEmpty())
        {
            adapter = new NoteAdapter(NoteActivity.this, srcNotes,cat_id);
        }
        nRecyclerView.setAdapter(adapter);
    }


}