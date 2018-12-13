package com.flyingstudio.cumtfriend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class Index {
    private int id;
    private String title;
    private String icon;
    private String type;
    private String value;
}
