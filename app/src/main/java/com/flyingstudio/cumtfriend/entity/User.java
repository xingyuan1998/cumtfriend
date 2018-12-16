package com.flyingstudio.cumtfriend.entity;

import org.litepal.crud.LitePalSupport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor(suppressConstructorProperties = true)
public class User extends LitePalSupport {
    private int id;
    private String name;
    private String stuNum;
    private String gender;
    private String password;
    private String school;
    private String major;
    private String university;
    private int year;
    private String create_time;
}
