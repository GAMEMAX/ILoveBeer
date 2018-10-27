package com.gamemax.ilovebeer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.transition.Visibility;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
        implements NavigationView.OnNavigationItemSelectedListener {
    List<GetDataAdapter> GetDataAdapter1;


    RecyclerView recyclerView;

    RecyclerView.LayoutManager recyclerViewlayoutManager;

    RecyclerViewAdapter recyclerViewAdapter;

    ProgressBar progressBar;

    String GET_JSON_DATA_HTTP_URL = "http://ilovebeer.altervista.org/jsonData.php";
    String JSON_ID = "id";
    String JSON_NAME = "name";
    String JSON_ALCOL = "alcol";
    String JSON_TYPE = "type";
    String JSON_DESCRIPTION = "description";
    String JSON_IMAGEPATH = "imageDir";
    FloatingActionButton fab;

    JsonArrayRequest jsonArrayRequest ;

    RequestQueue requestQueue ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        GetDataAdapter1 = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        progressBar.setVisibility(View.VISIBLE);
        JSON_DATA_WEB_CALL();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.syncing_process_string, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                progressBar.setVisibility(View.VISIBLE);
                GetDataAdapter1.clear();
                JSON_DATA_WEB_CALL();
            }
        });
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

        jsonArrayRequest = new JsonArrayRequest(GET_JSON_DATA_HTTP_URL,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        progressBar.setVisibility(View.GONE);

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
                if(recyclerViewAdapter.hasToBeFilteredToFav)
                    recyclerViewAdapter.filterFavs(false);
                getSupportActionBar().setTitle("I Love Beer");
                fab.setEnabled(true);
                fab.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_about:
                Intent intent = new Intent(MainActivity2.this, About.class);
                startActivity(intent);
                break;
            case R.id.nav_fav:
                if(!recyclerViewAdapter.hasToBeFilteredToFav)
                    recyclerViewAdapter.filterFavs(true);
                getSupportActionBar().setTitle("Favourites");
                fab.setEnabled(false);
                fab.setVisibility(View.INVISIBLE);
                break;
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
