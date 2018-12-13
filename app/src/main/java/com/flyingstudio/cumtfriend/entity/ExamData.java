package com.flyingstudio.cumtfriend.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class ExamData {
    private int id;
    private User user;
    private List<Exam> exams;
}
