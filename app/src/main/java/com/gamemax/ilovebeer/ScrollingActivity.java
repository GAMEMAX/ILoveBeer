package com.gamemax.ilovebeer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    boolean isFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        isFavourite = checkIfFavourite();

        Bundle b = getIntent().getExtras();
        int value = -1; // or other values
        if(b != null)
            value = b.getInt("id");

        if(value!= -1)
        {
            TextView beerAlcolView = (TextView) findViewById(R.id.beerAlcolView);
            beerAlcolView.setText(b.getString("alcol"));
            TextView beerTypeView = (TextView) findViewById(R.id.beerTypeView);
            beerTypeView.setText(b.getString("type"));
            TextView beerDescView = (TextView) findViewById(R.id.beerDescView);
            beerDescView.setText(Html.fromHtml(b.getString("description")));

            new DownloadImageTask((ImageView) findViewById(R.id.beerImage))
                    .execute(b.getString("imageDir"));
            //ImageView beerImg = (ImageView) findViewById(R.id.beerImage);

        }

        toolbar.setTitle(b.getString("name"));
        setSupportActionBar(toolbar);



        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(isFavourite)
            fab.setImageResource(R.drawable.ic_button_favourite);
        else
            fab.setImageResource(R.drawable.ic_button_notfavourite);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFavourite)
                {
                    writeToFile(getIntent().getExtras().getString("name") + ";", Context.MODE_APPEND);
                    isFavourite = true;
                    fab.setImageResource(R.drawable.ic_button_favourite);
                    Snackbar.make(view, R.string.added_to_favourites, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                }
                else
                {
                    String favsString = readFromFile();

                    String[] array = favsString.split(";");
                    favsString = "";

                    for(String s: array)
                        if(!s.equals(getIntent().getExtras().getString("name")))
                            favsString += s + ";";

                    writeToFile(favsString, Context.MODE_PRIVATE);

                    isFavourite = false;
                    fab.setImageResource(R.drawable.ic_button_notfavourite);

                    Snackbar.make(view, R.string.removed_from_favourites, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                }

            }
        });
    }

    void writeToFile(String data, int OpenParameter)
    {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("favouriteBeersFile.txt", OpenParameter));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    String readFromFile()
    {
        String favsString = "";
        try {
            InputStream inputStream = getApplicationContext().openFileInput("favouriteBeersFile.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                favsString = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return favsString;
    }

    boolean checkIfFavourite()
    {
        String favsString = readFromFile();

        if(favsString.contains(getIntent().getExtras().getString("name")))
            return true;
        else
            return false;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
