package com.bustracker.mustracker.Class;

/**
 * Created by Cat on 08-May-16.
 */
public class itemData {
    String text;
    Integer imageId;
    public itemData(String text, Integer imageId){
        this.text=text;
        this.imageId=imageId;
    }
    public String getText(){
        return text;
    }
    public Integer getImageId(){
        return imageId;
    }
}
