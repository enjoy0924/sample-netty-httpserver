package com.altas.core.annotation.pojo;

import com.altas.core.annotation.restful.enumeration.MimeType;

public class ProduceConstraint {
    private MimeType mimeType;

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    public MimeType getMimeType() {
        return mimeType;
    }
}
