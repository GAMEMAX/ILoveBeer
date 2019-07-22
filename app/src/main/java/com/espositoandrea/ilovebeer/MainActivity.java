package com.espositoandrea.ilovebeer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    HomeFragment homeFragment = new HomeFragment();
    AboutActivity aboutFragment = new AboutActivity();
    Fragment activeFragment;
    FragmentManager fragmentManager;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        RecyclerViewAdapter recyclerViewAdapter = homeFragment.recyclerViewAdapter;
        if (item.getItemId() == R.id.navigation_home) {
            if (recyclerViewAdapter.hasToBeFilteredToFav)
                recyclerViewAdapter.filterFavs(false);
            if (!activeFragment.equals(homeFragment)) {
                fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
            }
        } else if (item.getItemId() == R.id.navigation_favorite) {
            if (!recyclerViewAdapter.hasToBeFilteredToFav)
                recyclerViewAdapter.filterFavs(true);
            if (!activeFragment.equals(homeFragment)) {
                fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
            }
        } else if (item.getItemId() == R.id.navigation_about) {
            fragmentManager.beginTransaction().hide(activeFragment).show(aboutFragment).commit();
            activeFragment = aboutFragment;
        } else return false;
        return true;
    };
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_container, aboutFragment, "2").hide(aboutFragment).commit();
        fragmentManager.beginTransaction().add(R.id.content_container, homeFragment, "1").commit();
        activeFragment = homeFragment;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
//
//        final MenuItem searchItem = menu.findItem(R.id.action_search);
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                recyclerViewAdapter.filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                recyclerViewAdapter.filter(newText);
//                return false;
//            }
//        });

        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        switch (id)
//        {
//            case R.id.nav_home:
//                // Do nothing
//                break;
//            case R.id.nav_about:
//                Intent intent = new Intent(MainActivity2.this, About.class);
//                startActivity(intent);
//                break;
//            /*case R.id.nav_fav:
//                if(!recyclerViewAdapter.hasToBeFilteredToFav)
//                    recyclerViewAdapter.filterFavs(true);
//                getSupportActionBar().setTitle("Favourites");
//                break;*/
//            /*case R.id.nav_donate:
//                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/AndEsposito"));
//                startActivity(intent);
//                break;*/
//
//            default:
//                break;
//        }
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (activeFragment.equals(homeFragment)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(getString(R.string.are_you_sure_to_exit))
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, (arg0, arg1) -> MainActivity.super.onBackPressed())
                    .create()
                    .show();
        }
        else {
            navView.setSelectedItemId(R.id.navigation_home);
        }
    }

}
