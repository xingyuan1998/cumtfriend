package com.flyingstudio.cumtfriend.entity;

import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.model.ScheduleEnable;

import org.litepal.annotation.Column;
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
public class Subject extends LitePalSupport implements ScheduleEnable {
    @Column(ignore = true)
    private String id;
    private int day;
    private String teacher;
    private String name;
    private String room;
    private int start;
    private int step;
    private List<Integer>week_list;

    @Override
    public Schedule getSchedule() {
        Schedule schedule = new Schedule();
        schedule.setDay(getDay());
        schedule.setTeacher(getTeacher());
        schedule.setName(getName());
        schedule.setStart(getStart());
        schedule.setStep(getStep());
        schedule.setRoom(getRoom());
        schedule.setWeekList(getWeek_list());
        schedule.setColorRandom(3);
        return schedule;
    }
}
