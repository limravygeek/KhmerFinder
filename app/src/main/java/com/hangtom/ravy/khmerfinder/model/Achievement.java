package com.hangtom.ravy.khmerfinder.model;

/**
 * Created by Ravy on 4/27/2017.
 */

public class Achievement {

    private boolean isInstalled;
    private int app_logo;
    private String app_name;
    private int hint_number;
    private String link_url;
    private boolean isApp;

    public Achievement(boolean isInstalled,String app_name, int hint_number, int app_logo,String link_url,boolean isApp) {
        this.isInstalled = isInstalled;
        this.app_name = app_name;
        this.hint_number = hint_number;
        this.app_logo = app_logo;
        this.link_url = link_url;
        this.isApp = isApp;
    }

    public boolean isInstalled() {
        return isInstalled;
    }

    public void setInstalled(boolean installed) {
        isInstalled = installed;
    }

    public int getApp_logo() {
        return app_logo;
    }

    public void setApp_logo(int app_logo) {
        this.app_logo = app_logo;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public int getHint_number() {
        return hint_number;
    }

    public void setHint_number(int hint_number) {
        this.hint_number = hint_number;
    }


    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
    }

    public boolean isApp() {
        return isApp;
    }

    public void setApp(boolean app) {
        isApp = app;
    }
}
