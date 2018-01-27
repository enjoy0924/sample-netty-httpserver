package com.alr.core.annotation.pojo;

import com.alr.core.annotation.restful.enumeration.MimeType;

/**
 * Created by G_dragon on 2017/7/10.
 */
public class ProduceConstraint {
    private MimeType mimeType;

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    public MimeType getMimeType() {
        return mimeType;
    }
}
