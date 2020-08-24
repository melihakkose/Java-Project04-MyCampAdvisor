package com.melihakkose.androidstudio_java_project04_mycampadvisor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> names =new ArrayList<String>();
    static ArrayList<LatLng> locations=new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;

    //MENUYE TIKLANDIGINDA YAPILACAK ISLEMLER
    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        //EGER TIKLANAN MENU ITEM'i add_place' ise
        if(item.getItemId()==R.id.add_place){
            //intent
            Intent intent =new Intent(getApplicationContext(),MapsActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    //MENUYU BAGLAMAK ICIN GEREKLİ
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.add_place,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView =(ListView) findViewById(R.id.listView);

        //SQL OKUMA ISLEMLERI (VERI CEKME)
        try {

            MapsActivity.database=this.openOrCreateDatabase("Places",MODE_PRIVATE,null);

            Cursor cursor=MapsActivity.database.rawQuery("SELECT *FROM places",null);

            int nameIx=cursor.getColumnIndex("name");
            int latitudeIx=cursor.getColumnIndex("latitude");
            int longIx=cursor.getColumnIndex("longitude");


            while (cursor.moveToNext()){
                String nameFromDATA=cursor.getString(nameIx);
                String latitudeFromDATA=cursor.getString(latitudeIx);
                String longitudeFromDATA=cursor.getString(longIx);

                //VERILERI LISTEYE ATMA
                names.add(nameFromDATA);

                Double l1=Double.parseDouble(latitudeFromDATA);
                Double l2=Double.parseDouble(longitudeFromDATA);
                LatLng locationFromDATA=new LatLng(l1,l2);

                locations.add(locationFromDATA);
                System.out.println("name: "+nameFromDATA);

            }
            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        arrayAdapter =new ArrayAdapter(this,android.R.layout.simple_list_item_1,names);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("info","old");
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }
}