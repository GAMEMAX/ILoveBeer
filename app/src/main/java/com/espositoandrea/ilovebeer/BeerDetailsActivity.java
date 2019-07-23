package com.espositoandrea.ilovebeer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Objects;

public class BeerDetailsActivity extends AppCompatActivity {

    boolean isFavourite;


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        isFavourite = checkIfFavourite();

        Bundle b = getIntent().getExtras();
        int value = -1; // or other values
        if (b != null)
            value = b.getInt("id");

        if (value != -1) {
            TextView beerAlcolView = findViewById(R.id.beerAlcolView);
            beerAlcolView.setText(b.getString("alcol"));
            TextView beerTypeView = findViewById(R.id.beerTypeView);
            beerTypeView.setText(b.getString("type"));
            TextView beerDescView = findViewById(R.id.beerDescView);
            beerDescView.setText(Html.fromHtml(b.getString("description")));

            new DownloadImageTask(findViewById(R.id.beerImage))
                    .execute(b.getString("imageDir"));
            //ImageView beerImg = (ImageView) findViewById(R.id.beerImage);

        }

        toolbar.setTitle(Objects.requireNonNull(b).getString("name"));
        setSupportActionBar(toolbar);


        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageResource(isFavourite ? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp);
        fab.setOnClickListener(view -> {
            if (!isFavourite) {
                writeToFile(getIntent().getExtras().getString("name") + ";", Context.MODE_APPEND);
                isFavourite = true;
                fab.setImageResource(R.drawable.ic_favorite_black_24dp);
                Snackbar.make(view, R.string.added_to_favourites, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            } else {
                StringBuilder favsString = new StringBuilder(readFromFile());

                String[] array = favsString.toString().split(";");
                favsString = new StringBuilder();

                for (String s : array)
                    if (!s.equals(getIntent().getExtras().getString("name")))
                        favsString.append(s).append(";");

                writeToFile(favsString.toString(), Context.MODE_PRIVATE);

                isFavourite = false;
                fab.setImageResource(R.drawable.ic_favorite_border_black_24dp);

                Snackbar.make(view, R.string.removed_from_favourites, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

            }

        });
    }

    void writeToFile(String data, int OpenParameter) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("favouriteBeersFile.txt", OpenParameter));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    String readFromFile() {
        String favsString = "";
        try {
            InputStream inputStream = getApplicationContext().openFileInput("favouriteBeersFile.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                favsString = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return favsString;
    }

    boolean checkIfFavourite() {
        String favsString = readFromFile();
        return favsString.contains(Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).getString("name")));
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
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
