package com.itmo.bot.entities;

import javax.persistence.Id;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String username;
//    private Location location;
    private long chatId;

    public User() {
    }

    public User(String username, Location location, long chatId) {
        this.username = username;
//        this.location = location;
        this.chatId = chatId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//    public Location getLocation() {
//        return location;
//    }

//    public void setLocation(Location location) {
//        this.location = location;
//    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }
}
