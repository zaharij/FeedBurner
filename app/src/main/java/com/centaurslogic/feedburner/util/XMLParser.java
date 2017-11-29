package com.centaurslogic.feedburner.util;


import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XMLParser {
    private final String TITLE_TAG = "title";
    private final String LINK_TAG = "feedburner:origLink";
    private final String ENTRY_TAG = "entry";

    public class ThemeItem {
        private String title;
        private String link;

        ThemeItem(String title, String link) {
            this.title = title;
            this.link = link;
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }
    }

    public List<ThemeItem> parse(InputStream xml){
        List<ThemeItem> themeItems = null;
        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(xml, null);
            xmlPullParser.nextTag();
            themeItems = readXml(xmlPullParser);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return themeItems;
    }

    private List<ThemeItem> readXml(XmlPullParser pullParser) throws IOException, XmlPullParserException {
        List<ThemeItem> themeItems = new ArrayList<>();
        int eventType = pullParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (pullParser.getName() != null && pullParser.getName().equals("entry")){
                pullParser.next();
                themeItems.add(readItem(pullParser));
            }
            eventType = pullParser.next();
        }
        return themeItems;
    }

    private ThemeItem readItem(XmlPullParser pullParser) throws IOException, XmlPullParserException {
        String title = null;
        String link = null;
        while (pullParser.getName() == null || !pullParser.getName().equals(ENTRY_TAG)){
            if (pullParser.getName() != null && pullParser.getName().equals(TITLE_TAG)){
                title = readTitle(pullParser);
            } else if (pullParser.getName() != null && pullParser.getName().equals(LINK_TAG)){
                link = readLink(pullParser);
            }
            pullParser.next();
        }
        return new ThemeItem(title, link);
    }

    private String readTitle(XmlPullParser pullParser) throws IOException, XmlPullParserException {
        String title;
        pullParser.require(XmlPullParser.START_TAG, null, TITLE_TAG);
        title = readText(pullParser);
        pullParser.require(XmlPullParser.END_TAG, null, TITLE_TAG);
        return title;
    }

    private String readLink(XmlPullParser pullParser) throws IOException, XmlPullParserException {
        String link;
        pullParser.require(XmlPullParser.START_TAG, null, LINK_TAG);
        link = readText(pullParser);
        pullParser.require(XmlPullParser.END_TAG, null, LINK_TAG);
        return link;
    }

    private String readText(XmlPullParser pullParser) throws IOException, XmlPullParserException {
        String text = null;
        if (pullParser.next() == XmlPullParser.TEXT){
            text = pullParser.getText();
            pullParser.nextTag();
        }
        return text;
    }
}
