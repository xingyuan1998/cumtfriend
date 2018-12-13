package com.flyingstudio.cumtfriend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class User {
    private int id;
    private String username;
    private String create_time;
}
