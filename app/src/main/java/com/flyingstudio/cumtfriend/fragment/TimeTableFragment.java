package com.flyingstudio.cumtfriend.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyingstudio.cumtfriend.R;
import com.flyingstudio.cumtfriend.adapter.SubjectDetailAdapter;
import com.flyingstudio.cumtfriend.entity.Subject;
import com.flyingstudio.cumtfriend.entity.SubjectData;
import com.flyingstudio.cumtfriend.net.Constant;
import com.flyingstudio.cumtfriend.net.LoginTask;
import com.flyingstudio.cumtfriend.net.ScheduleTask;
import com.flyingstudio.cumtfriend.utils.SPUtil;
import com.flyingstudio.cumtfriend.view.LoginActivity;
import com.flyingstudio.cumtfriend.view.SubjectDetailActivity;
import com.google.gson.Gson;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.model.CacheMode;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.view.WeekView;

import org.litepal.LitePal;

import java.util.List;
import java.util.Objects;

public class TimeTableFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TimetableView mTimetableView;
    private WeekView mWeekView;
    int target = -1;

    Button moreButton;
    LinearLayout layout;
    TextView titleTextView;
    List<Subject> mySubjects;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TimeTableFragment() {
        // Required empty public constructor
    }

    public static TimeTableFragment newInstance(String param1, String param2) {
        TimeTableFragment fragment = new TimeTableFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_table, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        // 获取当前周
        String target_week = SPUtil.getValue(getContext(), "target_week");
        if (target_week == null) target = -1;
        else target = Integer.parseInt(target_week) + 1;


        //获取控件
        mTimetableView = getView().findViewById(R.id.id_timetableView);
        mWeekView = getView().findViewById(R.id.id_weekview);

        // 从数据库中取数据

        List<Subject> subjects = LitePal.findAll(Subject.class);
//        if (subjects.size() == 0) {
//            getFromInternetTimeTable();
//        } else {
//            mySubjects = subjects;
//            initTimetableView();
//            Log.d("GET DATABASE DATA", "initView: " + subjects.size());
//        }
        mySubjects = subjects;
        initTimetableView();

        moreButton = getView().findViewById(R.id.id_more);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopmenu();
            }
        });

//        mySubjects = SubjectRepertory.loadDefaultSubjects2();
//        mySubjects.addAll(SubjectRepertory.loadDefaultSubjects());
        titleTextView = getView().findViewById(R.id.id_title);
        layout = getView().findViewById(R.id.id_layout);
        layout.setOnClickListener(this);
//        initTimetableView();


    }

    public void getFromInternetTimeTable() {
        String token = SPUtil.getValue(getContext(), "token");
        Log.e("GET_TIME_TABLE", "initView: " + token);
        if (TextUtils.isEmpty(token)) Log.e("GET_TIMETABLE", "initView: " + "null");
        else EasyHttp.get(Constant.GET_TIME_TABLE)
                .baseUrl(Constant.BASE_URL)
                .headers("token", token)
                .cacheTime(1000)
                .cacheKey("timetable")
                .cacheMode(CacheMode.CACHEANDREMOTE)
                .execute(new SimpleCallBack<SubjectData>() {
                    @Override
                    public void onError(ApiException e) {
                        Log.e("GET_TIME_TABLE", "onError: " + e.getMessage());
//                        if (e.getCode() == 4000) {
//                            Intent intent = new Intent(getContext(), LoginActivity.class);
//                            startActivity(intent);
//                        }
                    }

                    @Override
                    public void onSuccess(SubjectData subjectData) {
                        if (subjectData != null) {
                            Log.e("GET_TIME_TABLE", "onSuccess: " + subjectData.getCreate_time());

                            mySubjects = subjectData.getTimetable();
                            if (mySubjects.size() > 0) {
                                LitePal.deleteAll(Subject.class);
                                Log.d("DELETE DATABASE", "delete timetable info onSuccess: ");
                            }
                            for (Subject subject : mySubjects) {
                                subject.save();
                            }
                            initTimetableView();
                            showTime();
                        }
                    }
                });
    }

    /**
     * 显示弹出菜单
     */
    public void showPopmenu() {
        PopupMenu popup = new PopupMenu(getContext(), moreButton);
        popup.getMenuInflater().inflate(R.menu.popmenu_base_func, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
//                    case R.id.top1:
//                        addSubject();
//                        break;
//                    case R.id.top2:
//                        deleteSubject();
//                        break;
                    case R.id.top4:
                        hideNonThisWeek();
                        break;
                    case R.id.top5:
                        showNonThisWeek();
                        break;
//                    case R.id.top6:
//                        setMaxItem(8);
//                        break;
//                    case R.id.top7:
//                        setMaxItem(10);
//                        break;
//                    case R.id.top8:
//                        setMaxItem(12);
//                        break;
                    case R.id.top9:
                        showTime();
                        break;
                    case R.id.top10:
                        hideTime();
                        break;
//                    case R.id.top11:
//                        showWeekView();
//                        break;
//                    case R.id.top12:
//                        hideWeekView();
//                        break;
//                    case R.id.top13:
//                        setMonthWidth();
//                        break;
//                    case R.id.top16:
//                        resetMonthWidth();
//                        break;
                    case R.id.top14:
                        hideWeekends();
                        break;
                    case R.id.top15:
                        showWeekends();
                        break;

                    case R.id.reimport_timetable:
//                        getFromInternetTimeTable();
                        // TODO 这里需要做重新导入课表 现在  还不知道怎么弄
//                        new ScheduleTask(getContext(), )

                        String stuNum = SPUtil.getValue(getContext(), "username");
                        String password = SPUtil.getValue(getContext(), "password");
                        Log.d("REIMPORT TIMETABLE", "onMenuItemClick: " + stuNum + password);
                        new LoginTask(getContext(), stuNum, password, new LoginTask.LoginCall() {
                            @Override
                            public void success(String cookie) {
                                Log.d("LOGIN SUCCESS", "success: " + cookie);
                                new ScheduleTask(getContext(), cookie, new ScheduleTask.ScheduleTaskFinish() {
                                    @Override
                                    public void finish() {
                                        Log.d("REIMPORT Schedule", "finish: ");
                                        Toast.makeText(getContext(), "重新导入课表成功", Toast.LENGTH_LONG).show();
                                        mTimetableView.updateDateView();
                                    }

                                    @Override
                                    public void fail() {
                                        Toast.makeText(getContext(), "重新导入课表失败", Toast.LENGTH_LONG).show();
                                    }
                                }).execute("");
                            }

                            @Override
                            public void fail() {
                                Log.d("LOGIN SUCCESS", "err: " );
                            }
                        }).execute("");
                        break;

                    default:
                        break;
                }
                return true;
            }
        });

        popup.show();
    }


    /**
     * 删除课程
     * 内部使用集合维护课程数据，操作集合的方法来操作它即可
     * 最后更新一下视图（全局更新）
     */
    protected void deleteSubject() {
        int size = mTimetableView.dataSource().size();
        int pos = (int) (Math.random() * size);
        if (size > 0) {
            mTimetableView.dataSource().remove(pos);
            mTimetableView.updateView();
        }
    }

    /**
     * 添加课程
     * 内部使用集合维护课程数据，操作集合的方法来操作它即可
     * 最后更新一下视图（全局更新）
     */
    protected void addSubject() {
        List<Schedule> dataSource = mTimetableView.dataSource();
        int size = dataSource.size();
        if (size > 0) {
            Schedule schedule = dataSource.get(0);
            dataSource.add(schedule);
            mTimetableView.updateView();
        }
    }

    /**
     * 隐藏非本周课程
     * 修改了内容的显示，所以必须更新全部（性能不高）
     * 建议：在初始化时设置该属性
     * <p>
     * updateView()被调用后，会重新构建课程，课程会回到当前周
     */
    protected void hideNonThisWeek() {
        mTimetableView.isShowNotCurWeek(false).updateView();
    }

    /**
     * 显示非本周课程
     * 修改了内容的显示，所以必须更新全部（性能不高）
     * 建议：在初始化时设置该属性
     */
    protected void showNonThisWeek() {
        mTimetableView.isShowNotCurWeek(true).updateView();
    }

    /**
     * 设置侧边栏最大节次，只影响侧边栏的绘制，对课程内容无影响
     *
     * @param num
     */
    protected void setMaxItem(int num) {
        mTimetableView.maxSlideItem(num).updateSlideView();
    }


    private void initTimetableView() {

//        Log.e("INIT", "initTimetableView: " + subjects.size());

        //设置周次选择属性
        mWeekView.source(mySubjects)
                .curWeek(target)
                .callback(new IWeekView.OnWeekItemClickedListener() {
                    @Override
                    public void onWeekClicked(int week) {
                        int cur = mTimetableView.curWeek();
                        //更新切换后的日期，从当前周cur->切换的周week
                        mTimetableView.onDateBuildListener()
                                .onUpdateDate(cur, week);
                        //课表切换周次
                        mTimetableView.changeWeekOnly(week);
                    }
                })
                .callback(new IWeekView.OnWeekLeftClickedListener() {
                    @Override
                    public void onWeekLeftClicked() {
                        onWeekLeftLayoutClicked();
                    }
                })
                .isShow(false)//设置隐藏，默认显示
                .showView();

        mTimetableView.source(mySubjects)
                .curWeek(target)
                .curTerm("大三下学期")
                .maxSlideItem(10)
                .monthWidthDp(30)
                //透明度
                //日期栏0.1f、侧边栏0.1f，周次选择栏0.6f
                //透明度范围为0->1，0为全透明，1为不透明
                .alpha(0.1f, 0.1f, 0.6f)
                .callback(new ISchedule.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, List<Schedule> scheduleList) {
                        Log.d("TIME TABLE CLICK", "onItemClick: " + scheduleList.size());
                        System.out.print("onItemClick" + scheduleList);
                        Gson gson = new Gson();

                        Intent intent = new Intent(getContext(), SubjectDetailActivity.class);
                        intent.putExtra("subjects", gson.toJson(scheduleList));
                        getContext().startActivity(intent);
                    }
                })
                .callback(new ISchedule.OnItemLongClickListener() {
                    @Override
                    public void onLongClick(View v, int day, int start) {
                        Toast.makeText(getContext(),
                                "长按:周" + day + ",第" + start + "节",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .callback(new ISchedule.OnWeekChangedListener() {
                    @Override
                    public void onWeekChanged(int curWeek) {
//                        titleTextView.setText("第" + curWeek + "周");
                    }
                })
//                //旗标布局点击监听
//                .callback(new ISchedule.OnFlaglayoutClickListener() {
//                    @Override
//                    public void onFlaglayoutClick(int day, int start) {
//                        mTimetableView.hideFlaglayout();
//                        Toast.makeText(BaseFuncActivity.this,
//                                "点击了旗标:周" + (day + 1) + ",第" + start + "节",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                })
                .showView();
    }

    protected void onWeekLeftLayoutClicked() {
        final String items[] = new String[20];
        int itemCount = mWeekView.itemCount();
        for (int i = 0; i < itemCount; i++) {
            items[i] = "第" + (i + 1) + "周";
        }
        target = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("设置当前周");
        builder.setSingleChoiceItems(items, mTimetableView.curWeek() - 1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        target = i;
                    }
                });
        builder.setPositiveButton("设置为当前周", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (target != -1) {
                    mWeekView.curWeek(target + 1).updateView();
                    mTimetableView.changeWeekForce(target + 1);
                    SPUtil.setValue(getContext(), "target_week", target + "");
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }


    /**
     * 隐藏时间
     * 将侧边栏监听置Null后，会默认使用默认的构建方法，即不显示时间
     * 只修改了侧边栏的属性，所以只更新侧边栏即可（性能高），没有必要更新全部（性能低）
     */
    protected void hideTime() {
        mTimetableView.callback((ISchedule.OnSlideBuildListener) null);
        mTimetableView.updateSlideView();
    }

    /**
     * 显示WeekView
     */
    protected void showWeekView() {
        mWeekView.isShow(true);
    }

    /**
     * 隐藏WeekView
     */
    protected void hideWeekView() {
        mWeekView.isShow(false);
    }

    /**
     * 设置月份宽度
     */
    private void setMonthWidth() {
        mTimetableView.monthWidthDp(50).updateView();
    }

    /**
     * 设置月份宽度,默认40dp
     */
    private void resetMonthWidth() {
        mTimetableView.monthWidthDp(40).updateView();
    }

    /**
     * 隐藏周末
     */
    private void hideWeekends() {
        mTimetableView.isShowWeekends(false).updateView();
    }

    /**
     * 显示周末
     */
    private void showWeekends() {
        mTimetableView.isShowWeekends(true).updateView();
    }

    protected void showTime() {
        String[] times = new String[]{
                "8:00", "8:55", "10:15", "11:10",
                "14:00", "14:55", "16:15", "17:10",
                "19:00", "19:55"
        };
        OnSlideBuildAdapter listener = (OnSlideBuildAdapter) mTimetableView.onSlideBuildListener();
        listener.setTimes(times)
                .setTimeTextColor(Color.BLACK);
        mTimetableView.updateSlideView();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_layout:
                //如果周次选择已经显示了，那么将它隐藏，更新课程、日期
                //否则，显示
                if (mWeekView.isShowing()) {
                    mWeekView.isShow(false);
                    titleTextView.setTextColor(getResources().getColor(R.color.app_course_textcolor_blue));
                    int cur = mTimetableView.curWeek();
                    mTimetableView.onDateBuildListener()
                            .onUpdateDate(cur, cur);
                    mTimetableView.changeWeekOnly(cur);
                } else {
                    mWeekView.isShow(true);
                    titleTextView.setTextColor(getResources().getColor(R.color.app_red));
                }
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
