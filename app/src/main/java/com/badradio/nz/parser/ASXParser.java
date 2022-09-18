package com.badradio.nz.parser;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

public class ASXParser {


    public LinkedList<String> getRawUrl(String url) {
        LinkedList<String> murls = null;
        try {
            return getRawUrl(getConnection(url));
        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }
        return murls;
    }

    public LinkedList<String> getRawUrl(URLConnection conn) {

        final BufferedReader br;
        String murl = null;
        LinkedList<String> murls = null;
        murls = new LinkedList<String>();
        try {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while (true) {
                try {
                    String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    murl = parseLine(line);
                    if (murl != null && !murl.equals("")) {
                        murls.add(murl);
                    }
                } catch (IOException e) {

                }
            }
        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }
        //murls.add(conn.getURL().toString());
        return murls;
    }

    private String parseLine(String line) {
        if (line == null) {
            return null;
        }
        String trimmed = line.trim();
        if (trimmed.startsWith("<ref href=\"")) {
            trimmed = trimmed.replace("<ref href=\"", "");
            trimmed = trimmed.replace("/>", "").trim();
            if (trimmed.endsWith("\"")) {
                trimmed = trimmed.replace("\"", "");
                Log.v("INFO", "ASX: " + trimmed);
                return trimmed;
            }
        }
        return "";
    }

    private URLConnection getConnection(String url) throws IOException {
        URLConnection mUrl = new URL(url).openConnection();
        return mUrl;
    }

}

