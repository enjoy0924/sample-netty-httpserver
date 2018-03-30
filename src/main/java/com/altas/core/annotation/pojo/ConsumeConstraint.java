package com.altas.core.annotation.pojo;

import com.altas.core.annotation.restful.enumeration.HttpMethod;
import com.altas.core.annotation.restful.enumeration.MimeType;

public class ConsumeConstraint {
    private HttpMethod method;
    private MimeType mimeType;

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public HttpMethod getMethod() {
        return method;
    }
}
