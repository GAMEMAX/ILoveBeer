package com.espositoandrea.ilovebeer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

public class AboutFragment extends Fragment {
    private AppCompatActivity mContext;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_about, container, false);

        mContext = (AppCompatActivity) Objects.requireNonNull(getActivity());
//        Objects.requireNonNull(mContext.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//        mContext.getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:gamemax.entertainment@outlook.com?subject=I Love Beer - "));
            startActivity(intent);
        });
        return v;
    }
}
