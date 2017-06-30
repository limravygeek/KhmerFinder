package com.hangtom.ravy.khmerfinder.model;

/**
 * Created by Ravy on 1/20/2017.
 */

public class Game {
    private int tag;
    private int image;
    private boolean lock;

    public Game() {
    }

    public Game(int tag, int image, boolean lock) {
        this.tag = tag;
        this.image = image;
        this.lock = lock;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer name) {
        this.tag = name;
    }


    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }


    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }
}
