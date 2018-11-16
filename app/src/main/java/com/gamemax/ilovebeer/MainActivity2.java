package com.gamemax.ilovebeer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.transition.Visibility;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener{
    List<GetDataAdapter> GetDataAdapter1;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView recyclerView;

    RecyclerView.LayoutManager recyclerViewlayoutManager;

    RecyclerViewAdapter recyclerViewAdapter;

    String GET_JSON_DATA_HTTP_URL = "http://ilovebeer.altervista.org/jsonData.php";
    String JSON_ID = "id";
    String JSON_NAME = "name";
    String JSON_ALCOL = "alcol";
    String JSON_TYPE = "type";
    String JSON_DESCRIPTION = "description";
    String JSON_IMAGEPATH = "imageDir";

    JsonArrayRequest jsonArrayRequest ;

    RequestQueue requestQueue ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Toolbar secondToolbar = (Toolbar) findViewById(R.id.toolbar2);
        //secondToolbar.inflateMenu(R.menu.second_toolbar_menu);
        final RadioButton homeBtn = (RadioButton) findViewById(R.id.radioButton);
        final RadioButton favBtn = (RadioButton) findViewById(R.id.radioButton2);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewAdapter.hasToBeFilteredToFav)
                    recyclerViewAdapter.filterFavs(false);
                getSupportActionBar().setTitle(R.string.app_name);
                favBtn.setChecked(false);
            }
        });
        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recyclerViewAdapter.hasToBeFilteredToFav)
                    recyclerViewAdapter.filterFavs(true);
                getSupportActionBar().setTitle(R.string.fav_screen_title);
                homeBtn.setChecked(false);
            }
        });
        /*secondToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                switch (id) {
                    case R.id.nav_home:
                        if (recyclerViewAdapter.hasToBeFilteredToFav)
                            recyclerViewAdapter.filterFavs(false);
                        getSupportActionBar().setTitle("I Love Beer");
                        fab.setEnabled(true);
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case R.id.nav_fav:
                        if (!recyclerViewAdapter.hasToBeFilteredToFav)
                            recyclerViewAdapter.filterFavs(true);
                        getSupportActionBar().setTitle("Favourites");
                        fab.setEnabled(false);
                        fab.setVisibility(View.INVISIBLE);
                        break;

                    default:
                        break;
                }
                return true;
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        GetDataAdapter1 = new ArrayList<>();

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);

                GetDataAdapter1.clear();
                JSON_DATA_WEB_CALL();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);

        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        mSwipeRefreshLayout.setRefreshing(true);
        JSON_DATA_WEB_CALL();
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {

        GetDataAdapter1.clear();
        JSON_DATA_WEB_CALL();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                recyclerViewAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapter.filter(newText);
                return false;
            }
        });

        return true;
    }


    public void JSON_DATA_WEB_CALL(){
        mSwipeRefreshLayout.setRefreshing(true);
        jsonArrayRequest = new JsonArrayRequest(GET_JSON_DATA_HTTP_URL,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {


                        mSwipeRefreshLayout.setRefreshing(false);

                        JSON_PARSE_DATA_AFTER_WEBCALL(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(jsonArrayRequest);
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array){

        for(int i = 0; i<array.length(); i++) {

            GetDataAdapter GetDataAdapter2 = new GetDataAdapter();

            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                GetDataAdapter2.setId(json.getInt(JSON_ID));

                GetDataAdapter2.setName(json.getString(JSON_NAME));

                GetDataAdapter2.setAlcol(json.getString(JSON_ALCOL));

                GetDataAdapter2.setType(json.getString(JSON_TYPE));

                GetDataAdapter2.setDescription(json.getString(JSON_DESCRIPTION));

                GetDataAdapter2.setImageDir(json.getString(JSON_IMAGEPATH));

            } catch (JSONException e) {

                e.printStackTrace();
            }
            GetDataAdapter1.add(GetDataAdapter2);
        }

        recyclerViewAdapter = new RecyclerViewAdapter(GetDataAdapter1, this);


        recyclerView.setAdapter(recyclerViewAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        recyclerViewAdapter.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.nav_home:
                // Do nothing
                break;
            case R.id.nav_about:
                Intent intent = new Intent(MainActivity2.this, About.class);
                startActivity(intent);
                break;
            /*case R.id.nav_fav:
                if(!recyclerViewAdapter.hasToBeFilteredToFav)
                    recyclerViewAdapter.filterFavs(true);
                getSupportActionBar().setTitle("Favourites");
                break;*/
            /*case R.id.nav_donate:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/AndEsposito"));
                startActivity(intent);
                break;*/

            default:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
