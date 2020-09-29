package com.melihakkose.mycampadvisor_02.view;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.melihakkose.mycampadvisor_02.R;
import com.melihakkose.mycampadvisor_02.models.Place;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    //DATABASE TANIMLAMA
    SQLiteDatabase database;

    //GERI TUSUNA BASINCA NE OLACAK
    //BUNUN AMACI GERI TUSUNA BASILDIGI ZAMAN YAPILAN DEGISIKLIKLERI ANINDA GOREBILMEMIZI SAGLIYOR
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToMain=new Intent(this,MainActivity.class);
        startActivity(intentToMain);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);


        //MENUDEN MI GİRİS YAPILDI LISTEDEN MI
        Intent intent=getIntent();
        String info = intent.getStringExtra("info");

        if(info.matches("new")){
            //KONUM AYARLAMALARI YAPMAK ICIN LISTENER VE MANAGERIMIZI EKLIYORUZ
            locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {

                    //DAHA ONCEDEN KONUM VERISI VAR MI BIR KERE CALISACAK
                    SharedPreferences sharedPreferences= MapsActivity.this.getSharedPreferences("com.melihakkose.mycampadvisor_02",MODE_PRIVATE);
                    boolean trackBoolean= sharedPreferences.getBoolean("trackBoolean",false);
                    if(!trackBoolean){
                        //KULLANICI YERINI BELIRLEYIP KAMERA ICIN ZOOM AYARLIYORUZ
                        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,17f));
                        sharedPreferences.edit().putBoolean("trackBoolean",true).apply();
                    }

                }
            };

            //ISLEMLERIMIZI YAPMADAN ONCE IZIN ALIP ALMADIGIMIZI KONTROL EDIYORUZ
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else{
                //KONUM GUNCELLEMELERI
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                //SON BILINEN KONUM VAR MI YOK MU
                Location lastLocation= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastLocation!=null){
                    LatLng lastUserlocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserlocation,17f));
                }
            }
        }else{
            //DATABASEDEN CEKILEN VERILERIN OLDUGU ADRESE ODAKLANACAGIZ
            mMap.clear();
            Place place = (Place)intent.getSerializableExtra("place");
            LatLng latLng=new LatLng(place.latitude,place.longitude);
            String placeName=place.name;

            mMap.addMarker(new MarkerOptions().position(latLng).title(placeName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f));
        }



    }

    //IZIN KONTROLLERI
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0){
            if(requestCode==1){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                    Intent intent=getIntent();
                    String info=intent.getStringExtra("new");

                    if(info.matches("new")){
                        //SON BILINEN KONUM VAR MI YOK MU
                        Location lastLocation= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(lastLocation!=null){
                            LatLng lastUserlocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserlocation,17f));
                    }

                }else{
                        //DATABASEDEN CEKILEN VERILERIN OLDUGU ADRESE ODAKLANACAGIZ (SQLITE && INTENT DATA)
                        mMap.clear();
                        Place place = (Place)intent.getSerializableExtra("place");
                        LatLng latLng=new LatLng(place.latitude,place.longitude);
                        String placeName=place.name;

                        mMap.addMarker(new MarkerOptions().position(latLng).title(placeName));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f));
                    }

                }
            }
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        //HARITAYA UZUN TIKLANINCA NE OLACAK ?
        Geocoder geocoder= new Geocoder(getApplicationContext(), Locale.getDefault());
        String address="";

        try {
            List<Address> addressList= geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            if(addressList!=null && addressList.size()>0){
                if(addressList.get(0).getThoroughfare()!=null){
                    address+=addressList.get(0).getThoroughfare();

                    if(addressList.get(0).getSubThoroughfare()!=null){
                        address+="";
                        address+=addressList.get(0).getSubThoroughfare();
                    }
                }
            }else{
                address="New Place";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.clear();;

        mMap.addMarker(new MarkerOptions().title(address).position(latLng));

        Double latitude = latLng.latitude;
        Double longitude= latLng.longitude;

        final Place place= new Place(address,latitude,longitude);

        //UZUN TIKLANAN YERI KAYDETMEK ISTIYOR MUSUNUZ ISTEMIYOR MUSUNUZ?
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(MapsActivity.this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Are you sure you want to save?");
        alertDialog.setMessage(place.name);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{

                    //DATABASE KODLARI OLUSTURMA (VERILERI KAYDETME)
                    database= MapsActivity.this.openOrCreateDatabase("Camps",MODE_PRIVATE,null);
                    database.execSQL("CREATE TABLE IF NOT EXISTS places (id INTEGER PRIMARY KEY,name VARCHAR, latitude VARCHAR, longitude VARCHAR )");

                    String toCompile= "INSERT INTO places (name,latitude,longitude) VALUES (?,?,?)";
                    SQLiteStatement sqLiteStatement=database.compileStatement(toCompile);
                    sqLiteStatement.bindString(1,place.name);
                    sqLiteStatement.bindString(2,String.valueOf(place.latitude));
                    sqLiteStatement.bindString(3,String.valueOf(place.longitude));
                    sqLiteStatement.execute();

                    Toast.makeText(getApplicationContext(),"Saved!",Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    e.printStackTrace();
                }



            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"Canceled!",Toast.LENGTH_LONG).show();
            }
        });
        alertDialog.show();





    }
}