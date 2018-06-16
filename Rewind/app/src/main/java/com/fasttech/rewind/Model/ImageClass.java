package com.fasttech.rewind.Model;

/**
 * Created by dell on 6/14/2018.
 */

public class ImageClass {
    private String Name;
    private String Image;
    private String Mobile;
    private String type;

    public ImageClass() {
    }

    public ImageClass(String name, String image, String mobileId, String type) {
        Name = name;
        Image = image;
        Mobile = mobileId;
        type = type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobileId) {
        Mobile = mobileId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
