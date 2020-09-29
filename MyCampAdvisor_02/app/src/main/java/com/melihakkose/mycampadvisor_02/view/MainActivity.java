package com.melihakkose.mycampadvisor_02.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.melihakkose.mycampadvisor_02.R;
import com.melihakkose.mycampadvisor_02.adapter.CustomAdapter;
import com.melihakkose.mycampadvisor_02.models.Place;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Activity icerisinde kullanilacak elemanlari tanimliyoruz
    SQLiteDatabase database;
    ArrayList<Place> placeList=new ArrayList<>();
    ListView listView;
    CustomAdapter customAdapter;


    //MENULERI BAGLAMAK ICIN
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //XML' de olusturulan menuyu bagliyoruz
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.add_place,menu);

        return super.onCreateOptionsMenu(menu);
    }

    //MENU SECILIRSE YAPILACAKLAR
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.add_place){
            Intent intent= new Intent(this,MapsActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView);
        getData();

    }

    //VERILERI CEKME
    public void getData(){
        //OLUSTURDUGUMUZ GORONUM ILE BAGLAYACAGIZ
        customAdapter = new CustomAdapter(this,placeList);
        try {

            //VERITABANI AC YA DA OLUSTUR
            database=this.openOrCreateDatabase("Camps",MODE_PRIVATE,null);

            //VERI CEKMEK ICIN CURSOR
            Cursor cursor=database.rawQuery("SELECT * FROM places",null);

            //VERILERI DEGISKENLERE CEKIYORUZ
            int nameIx= cursor.getColumnIndex("name");
            int latitudeIx=cursor.getColumnIndex("latitude");
            int longitudeIx= cursor.getColumnIndex("longitude");

            //VERILERI OKUYORUZ
            while (cursor.moveToNext()){
                //VERILERI DEGISKENLERE ATIYORUZ
                String nameFromDatabase= cursor.getString(nameIx);
                String latitudeFromDatabase=cursor.getString(latitudeIx);
                String longitudeFromDatabase=cursor.getString(longitudeIx);

                //ENLEM BOYLAM DONUSUM ISLEMI YAPILARAK YENI DEGISKENLERE ATILIYOR
                Double latitude = Double.parseDouble(latitudeFromDatabase);
                Double longitude= Double.parseDouble(longitudeFromDatabase);

                //PLACE MODELIMIZI OLUSTURUYORUZ
                Place place =new Place(nameFromDatabase,latitude,longitude);

                System.out.println(place.name);

                //LISTEMIZE CEKILEN VERILERI ATIYORUZ
                placeList.add(place);
            }
            //ADAPTERDE DEGISIKLIKLERI UYGULUYORUZ
            customAdapter.notifyDataSetChanged();
            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        //ADAPTER ILE LISTVIEW BAGLIYORUZ
       listView.setAdapter(customAdapter);

        //LISTVIEW' de GORUNTULENEN DEGERLERE BASILINCA NE OLACAGINIZ KODLUYORUZ
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("info","old");
                intent.putExtra("place",placeList.get(position));
                startActivity(intent);
            }
        });



    }
}