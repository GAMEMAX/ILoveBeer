package com.espositoandrea.ilovebeer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    Context context;

    List<GetDataAdapter> getDataAdapter;
    List<GetDataAdapter> dataAdapterCopy;
    public boolean hasToBeFilteredToFav;

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView NameTextView;
        TextView AlcolTextView;
        ImageView BeerImageView;
        String beerName;
        String beerAlcol;
        String beerType;
        String beerDesc;
        String beerImgDir;
        public int id;



        RecyclerViewHolder(View itemView)
        {
            super(itemView);

            NameTextView = itemView.findViewById(R.id.textView4);
            AlcolTextView = itemView.findViewById(R.id.textView8);
            BeerImageView = itemView.findViewById(R.id.logoView);

            itemView.setOnClickListener(v -> {
                if (!beerName.equals(context.getString(R.string.no_fav_beer))) {
                    Intent intent = new Intent(context, BeerDetailsActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("id", id); //Your id
                    b.putString("name", beerName);
                    b.putString("type", beerType);
                    b.putString("alcol", beerAlcol);
                    b.putString("description", beerDesc);
                    b.putString("imageDir", beerImgDir);
                    intent.putExtras(b); //Put your id to your next Intent
                    ((Activity) context).startActivityForResult(intent, 1);
                }
            });
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
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

    public RecyclerViewAdapter(List<GetDataAdapter> getDataAdapter, Context context){

        super();

        this.getDataAdapter = getDataAdapter;
        dataAdapterCopy = new ArrayList<>(getDataAdapter);
        this.context = context;
        hasToBeFilteredToFav = false;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent databack)
    {
        if (requestCode == 1)
            if(resultCode == Activity.RESULT_OK)
                filterFavs(hasToBeFilteredToFav);
    }

    private String readFromFile()
    {
        String favsString = "";
        try {
            InputStream inputStream = context.openFileInput("favouriteBeersFile.txt");
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

    public void filter(String text) {
        getDataAdapter.clear();

        if(text.isEmpty())
            getDataAdapter.addAll(dataAdapterCopy);
        else
        {
            text = text.toLowerCase();
            for(GetDataAdapter item: dataAdapterCopy)
                if(item.name.toLowerCase().contains(text))
                {
                    if (!hasToBeFilteredToFav)
                        getDataAdapter.add(item);
                    else if (readFromFile().contains(item.name))
                        getDataAdapter.add(item);
                }
        }

        notifyDataSetChanged();
    }

    public void filterFavs(boolean showOnlyFavs) {
        getDataAdapter.clear();
        hasToBeFilteredToFav = showOnlyFavs;

        if(!showOnlyFavs)
            getDataAdapter.addAll(dataAdapterCopy);
        else
        {
            String favsString = readFromFile();

            for(GetDataAdapter item: dataAdapterCopy)
                if(favsString.contains(item.name))
                    getDataAdapter.add(item);
        }

        if(getDataAdapter.isEmpty())
        {
            GetDataAdapter item = new GetDataAdapter(context.getString(R.string.no_fav_beer), "XXXX");
            item.setImageDir("");
            getDataAdapter.add(item);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        GetDataAdapter getDataAdapter1 =  getDataAdapter.get(position);

        holder.NameTextView.setText(getDataAdapter1.getName());

        holder.AlcolTextView.setText(getDataAdapter1.getAlcol());

        new DownloadImageTask(holder.BeerImageView)
                .execute(getDataAdapter1.getImageDir());

        holder.beerName = getDataAdapter1.getName();
        holder.beerAlcol = getDataAdapter1.getAlcol();
        holder.beerType = getDataAdapter1.getType();
        holder.id = getDataAdapter1.getId();
        holder.beerDesc = getDataAdapter1.getDescription();
        holder.beerImgDir = getDataAdapter1.getImageDir();
    }

    @Override
    public int getItemCount() {
        return getDataAdapter.size();
    }
}
