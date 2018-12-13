package com.flyingstudio.cumtfriend.entity;

import org.litepal.crud.LitePalSupport;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor(suppressConstructorProperties = true)
public class Record extends LitePalSupport {
    private int id;
    private String create_time;
    private String name;
    private String grade;
    private String credit;
    private String gpa;
    private String teacher;
    private String course_id;
}
