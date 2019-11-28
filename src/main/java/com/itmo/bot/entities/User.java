package com.itmo.bot.entities;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long chatId;
    private String username;
    private boolean subscriber;

    @OneToOne(targetEntity = Location.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JoinColumn(name = "id", referencedColumnName = "id")
    private Location location;


    public User() {
    }

    public User(String username, Location location, long chatId) {
        this.username = username;
        this.chatId = chatId;
        this.location = location;
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

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isSubscriber() {
        return subscriber;
    }

    public void setSubscriber(boolean subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", username='" + username + '\'' +
                ", subscriber=" + subscriber +
//                ", locations = " + location.getLatitude() + " : " + location.getLongitude() +
                '}';
    }
}
