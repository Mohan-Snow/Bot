package com.itmo.bot.entities;

import org.springframework.stereotype.Component;
import javax.persistence.Id;

import javax.persistence.*;

//@Entity
//@Table(name = "locations")
public class Location {

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private float latitude;
    private float longitude;

    public Location() {
    }

    public Location(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
