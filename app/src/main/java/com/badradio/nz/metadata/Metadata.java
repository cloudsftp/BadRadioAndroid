package com.badradio.nz.metadata;


public class Metadata {
    private final String artist;
    private final String song;
    private final String show;
    private final String channels;
    private final String bitrate;
    private final String station;
    private final String genre;
    private final String url;

    public Metadata(String artist, String song, String show, String channels, String bitrate, String station, String genre, String url) {
        this.artist = artist;
        this.song = song;
        this.show = show;
        this.channels = channels;
        this.bitrate = bitrate;
        this.station = station;
        this.genre = genre;
        this.url = url;
    }

    public String getArtist() {
        return artist;
    }

    public String getSong() {
        return song;
    }

    public String getShow() {
        return show;
    }

    public String getChannels() {
        return channels;
    }

    public String getBitrate() {
        return bitrate;
    }

    public String getStation() {
        return station;
    }

    public String getGenre() {
        return genre;
    }

    public String getUrl() {
        return url;
    }
}