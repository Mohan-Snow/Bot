package com.itmo.bot.entities.jsonentities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Snow {

    @JsonProperty("1h")
    private double oneH;

    @JsonProperty("3h")
    private double threeH;

    public double getOneH() {
        return oneH;
    }

    public void setOneH(double oneH) {
        this.oneH = oneH;
    }

    public double getThreeH() {
        return threeH;
    }

    public void setThreeH(double threeH) {
        this.threeH = threeH;
    }
}
