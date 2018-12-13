package com.flyingstudio.cumtfriend.entity;

import org.litepal.crud.LitePalSupport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class Exam extends LitePalSupport {
    private int id;
    private String room;
    private String name;
    private String course_id;
    private String time;
}
