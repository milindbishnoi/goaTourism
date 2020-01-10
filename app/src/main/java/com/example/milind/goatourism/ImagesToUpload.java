package com.example.milind.goatourism;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;
@IgnoreExtraProperties
public class ImagesToUpload  {
    private String ImageId;
    private String url;
    private Date time;
    public ImagesToUpload() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public ImagesToUpload(String ImageId, String url) {
        this.ImageId=ImageId;
        this.url=url;
    }
}
