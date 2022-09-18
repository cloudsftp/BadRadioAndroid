package com.badradio.nz.Models;

public class RadioList {

    public String name;
    public String streamURL;
    public String imageURL;
    public String desc;
    public String longDesc;

    public RadioList(){

    }

    public RadioList(String name, String streamURL, String imageURL, String desc, String longDesc) {
        this.name = name;
        this.streamURL = streamURL;
        this.imageURL = imageURL;
        this.desc = desc;
        this.longDesc = longDesc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreamURL() {
        return streamURL;
    }

    public void setStreamURL(String streamURL) {
        this.streamURL = streamURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }
}
