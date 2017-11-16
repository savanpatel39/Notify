package com.example.noname.notify20;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    public  DataAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DBHandler dbHandler;
    private SearchView srcBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        doInitializations();
        searchFunc();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(srcBar.getVisibility() == View.VISIBLE) {
            srcBar.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            if(srcBar.getVisibility() == View.GONE)
            {
                srcBar.setVisibility(View.VISIBLE);
                srcBar.setIconified(false);
                srcBar.setQueryHint("Type here to search...");
                srcBar.requestFocus();
            }
            else
            {
                srcBar.setVisibility(View.GONE);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        List<Category> srtNote = new ArrayList<>();

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0));

        int id = item.getItemId();

        if (id == R.id.srtTitleAsc)
        {
            srtNote = dbHandler.getCatAscByTitle();
            srtNote.add(new Category(0,"+"));
            adapter = null;
            adapter = new DataAdapter(MainActivity.this, srtNote);
            mRecyclerView.setAdapter(adapter);
        }
        else if (id == R.id.srtTitleDsc)
        {
            srtNote = dbHandler.getCatDescByTitle();
            srtNote.add(new Category(0,"+"));
            adapter = null;
            adapter = new DataAdapter(MainActivity.this, srtNote);
            mRecyclerView.setAdapter(adapter);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void doInitializations()
    {
        dbHandler = new DBHandler(this,null,null,1);

        mRecyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        srcBar = (SearchView) findViewById(R.id.sBar);

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(-5));

        adapter = new DataAdapter(MainActivity.this,getData());
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.animate();
    }

    //Will get data from database and assign to category object and from here it'll be passed to DataAdapter...
    private List<Category> getData()
    {
//        List<Category> data = new ArrayList<>();
//        String[] titles = dbHandler.getCategoryNames();
//
//        Log.i("Any","Array After : "+titles.length);
//        Log.i("Any", "Length : " + titles.length);
//        for (int i = 0;i<titles.length;i++)
//        {
//            Category current = new Category();
//            current.catname = titles[i];
//            data.add(current);
//        }
//        return data;
        List<Category> data = dbHandler.getCategoryNames();
        data.add(new Category(0, "+"));
        return data;
    }


    private void searchFunc()
    {
        //*** setOnQueryTextFocusChangeListener ***
        srcBar.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

//                Toast.makeText(getBaseContext(), String.valueOf(hasFocus), Toast.LENGTH_SHORT).show();
            }
        });

        //*** setOnQueryTextListener ***
        srcBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                //searchInDatabase(query);
//                Toast.makeText(getBaseContext(), query,Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub

                    searchInDatabase(newText);

//                Toast.makeText(getBaseContext(), newText,Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void searchInDatabase(String query)
    {
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0));

        List<Category> srcCat = dbHandler.searchAllCategories(query);
        srcCat.add(new Category(0,"+"));
        adapter = null;
        if(!srcCat.isEmpty())
        {
            adapter = new DataAdapter(MainActivity.this, srcCat);
        }
        mRecyclerView.setAdapter(adapter);
    }
}
