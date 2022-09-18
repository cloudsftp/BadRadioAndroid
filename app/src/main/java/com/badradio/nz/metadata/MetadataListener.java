package com.badradio.nz.metadata;


interface MetadataListener {
    void onMetadataReceived(String artist, String song, String show);
}
