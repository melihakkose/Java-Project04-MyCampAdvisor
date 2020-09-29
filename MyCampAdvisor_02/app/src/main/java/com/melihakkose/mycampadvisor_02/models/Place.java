package com.melihakkose.mycampadvisor_02.models;

import java.io.Serializable;

public class Place implements Serializable {

    /*
    Programda kullanmak adina kendi modelimizi olusturuyoruz.
    1- Daha hizli islem
    2- Daha az kod ile cok is
    3- public tanimli degiskenler ile bircok yerden erisim sagliyoruz
    4- Constructor ile istedigimiz bilgileri kullanicidan alabiliriz.
    5- Modelimizi Serializable yapiyoruz
     */

    public String name;
    public Double latitude;
    public Double longitude;

    public Place(String name,Double latitude,Double longitude){
        this.name=name;
        this.longitude=longitude;
        this.latitude=latitude;

    }

}
