package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder>{

    private Context context;
    private ArrayList<WeatherRVModel> weatherRVModels; //this arraylist stores all the class objects

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModel> weatherRVModels) {
        this.context = context;
        this.weatherRVModels = weatherRVModels;
    }

    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {
        WeatherRVModel model = weatherRVModels.get(position);
        holder.tempraturetv.setText(model.getTemprature()+"°c");
        Picasso.get().load("https:".concat(model.getIcon())).into(holder.conditioniv);
        holder.windtv.setText(model.getWindspeed()+"km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try{
            Date t= input.parse(model.getTime());
            holder.timetv.setText(output.format(t));
        }catch(ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherRVModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView windtv ,tempraturetv,timetv;
        private ImageView conditioniv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            windtv = itemView.findViewById(R.id.idtvwindspeed);
            tempraturetv = itemView.findViewById(R.id.idtvtempraturerv);
            timetv = itemView.findViewById(R.id.idtvtime);
            conditioniv = itemView.findViewById(R.id.idivconditionrv);


        }
    }
}
