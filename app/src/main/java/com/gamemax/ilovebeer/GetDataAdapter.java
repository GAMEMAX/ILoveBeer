package com.gamemax.ilovebeer;

import java.util.ArrayList;

/**
 * Created by JUNED on 6/16/2016.
 */
public class GetDataAdapter {

    int Id;
    String name;
    String type;
    String alcol;
    String description;
    String imageDir;

    public GetDataAdapter (String name, String alcol) {
        this.name = name;
        this.alcol = alcol;
    }
    public GetDataAdapter () {}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getId() {
        return Id;
    }

    public void setId(int Id1) {
        this.Id = Id1;
    }


    public String getType() {
        return type;
    }

    public void setType(String phone_number1) {
        this.type = phone_number1;
    }


    public String getAlcol() {
        return alcol;
    }

    public void setAlcol(String subject1) {
        this.alcol = subject1;
    }


    public void setDescription(String description1) {
        this.description = description1;
    }

    public String getDescription() {
        return description;
    }


    public void setImageDir(String imageDir1) {
        this.imageDir = imageDir1;
    }

    public String getImageDir() {
        return imageDir;
    }

}