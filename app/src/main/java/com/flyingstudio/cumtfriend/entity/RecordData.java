package com.flyingstudio.cumtfriend.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class RecordData {
    private int id;
    private User user;
    private List<Record>records;
    private String create_time;
}
