package com.melihakkose.mycampadvisor_02.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.melihakkose.mycampadvisor_02.R;
import com.melihakkose.mycampadvisor_02.models.Place;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Place> {

    /*
    Kendi Adapter' imizi olusturarak daha hizli ve istedigimiz sonuca ulasacagiz.
    1- Onemli olan kisimlardan birisi "ArrayAdapter<Place>" i extend etmek
    2- Adapterde kullanacagimiz context ve listeyi tanimlamak
    3- Constructor olusturmak (Boylelikle adapter icerisine istedigimiz bilgileri saglayabilecegiz)
    4- View getView methodu ile xml dosyamiz ile kodumuzu baglayabilecegiz ve xml dosyasi icerisinde-
    ki elemanlara ulasabilecegiz
    5- View getView, geriye View dondurecegi icin kendi customView' mizi veriyoruz.
     */

    ArrayList<Place> placeList;
    Context context;

    public CustomAdapter(@NonNull Context context,ArrayList<Place> placeList) {
        super(context, R.layout.custom_list_row,placeList);
        this.placeList=placeList;
        this.context=context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View customView=layoutInflater.inflate(R.layout.custom_list_row,parent,false);
        TextView nameTextView=customView.findViewById(R.id.nameTextView);
        nameTextView.setText(placeList.get(position).name);

        return customView;
    }
}
