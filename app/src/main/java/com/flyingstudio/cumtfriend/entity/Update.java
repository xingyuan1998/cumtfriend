package com.flyingstudio.cumtfriend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class Update {
    private int id;
    private String title;
    private String content;
    private String version_name;
    private String url;
    private int force;
    private int version_id;
}
