package com.altas.gateway.schema;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public class Account {
    private String name;
    private Integer num;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
