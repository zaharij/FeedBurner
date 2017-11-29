package com.centaurslogic.feedburner.data;


import android.os.Handler;

import com.centaurslogic.feedburner.util.XMLParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FeedsLoader {
    private final int CONNECTION_TIMEOUT = 5000;
    private final int READ_TIMEOUT = 2500;
    private Handler handler;

    public interface IDataCallback<T>{
        void onResponse(T t);
    }

    public FeedsLoader(){
        handler = new Handler();
    }

    public void downloadFeeds(final String path, final IDataCallback<List<XMLParser.ThemeItem>> dataCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<XMLParser.ThemeItem> items;
                InputStream xmlStream;
                xmlStream = downloadXml(path);
                if (xmlStream != null){
                    items = createThemeItemsFromXml(xmlStream);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dataCallback.onResponse(items);
                        }
                    });
                }
            }
        }).start();
    }

    private InputStream downloadXml(String path){
        InputStream inputStream = null;
        try {
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    private List<XMLParser.ThemeItem> createThemeItemsFromXml(InputStream inputStream){
        XMLParser xmlParser = new XMLParser();
        return xmlParser.parse(inputStream);
    }
}
