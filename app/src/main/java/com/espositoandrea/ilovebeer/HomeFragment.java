package com.espositoandrea.ilovebeer;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private List<GetDataAdapter> GetDataAdapter1;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;

    RecyclerViewAdapter recyclerViewAdapter;

    private AppCompatActivity mContext;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        mContext = (AppCompatActivity) Objects.requireNonNull(getActivity());

        GetDataAdapter1 = new ArrayList<>();


        mSwipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            GetDataAdapter1.clear();
            JSON_DATA_WEB_CALL();
        });

        recyclerView = v.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        Toolbar mToolbar = v.findViewById(R.id.toolbar);
        mContext.setSupportActionBar(mToolbar);

        mSwipeRefreshLayout.setRefreshing(true);
        JSON_DATA_WEB_CALL();
        return v;
    }

    @Override
    public void onRefresh() {
        GetDataAdapter1.clear();
        JSON_DATA_WEB_CALL();
    }

    private void JSON_DATA_WEB_CALL() {
        mSwipeRefreshLayout.setRefreshing(true);
        // Do nothing
        String GET_JSON_DATA_HTTP_URL = "http://ilovebeer.altervista.org/jsonData.php";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(GET_JSON_DATA_HTTP_URL,
                response -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    JSON_PARSE_DATA_AFTER_WEBCALL(response);
                },
                error -> {
                    // Do nothing
                });

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            GetDataAdapter GetDataAdapter2 = new GetDataAdapter();

            JSONObject json;
            try {
                json = array.getJSONObject(i);
                String JSON_ID = "id";
                GetDataAdapter2.setId(json.getInt(JSON_ID));
                String JSON_NAME = "name";
                GetDataAdapter2.setName(json.getString(JSON_NAME));
                String JSON_ALCOHOL = "alcol";
                GetDataAdapter2.setAlcol(json.getString(JSON_ALCOHOL));
                String JSON_TYPE = "type";
                GetDataAdapter2.setType(json.getString(JSON_TYPE));
                String JSON_DESCRIPTION = "description";
                GetDataAdapter2.setDescription(json.getString(JSON_DESCRIPTION));
                String JSON_IMAGE_PATH = "imageDir";
                GetDataAdapter2.setImageDir(json.getString(JSON_IMAGE_PATH));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            GetDataAdapter1.add(GetDataAdapter2);
        }

        recyclerViewAdapter = new RecyclerViewAdapter(GetDataAdapter1, mContext);
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        recyclerViewAdapter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.top_toolbar_menu, menu);
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapter.filter(newText);
                return false;
            }
        });
    }

}
