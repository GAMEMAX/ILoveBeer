package com.espositoandrea.ilovebeer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    HomeFragment homeFragment = new HomeFragment();
    AboutFragment aboutFragment = new AboutFragment();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
