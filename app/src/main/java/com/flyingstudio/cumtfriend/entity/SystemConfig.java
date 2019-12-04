package com.flyingstudio.cumtfriend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(suppressConstructorProperties = true)
@NoArgsConstructor
public class SystemConfig {
    /**
     * "open": "2019-08-26 08:08:00",
     * "term": 1,
     * "year": 2019
     */

    private String open;
    private int term;
    private int year;
}
