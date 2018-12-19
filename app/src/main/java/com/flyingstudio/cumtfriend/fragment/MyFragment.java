package com.flyingstudio.cumtfriend.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.flyingstudio.cumtfriend.MainActivity;
import com.flyingstudio.cumtfriend.R;
import com.flyingstudio.cumtfriend.entity.Exam;
import com.flyingstudio.cumtfriend.entity.Record;
import com.flyingstudio.cumtfriend.entity.Subject;
import com.flyingstudio.cumtfriend.entity.User;
import com.flyingstudio.cumtfriend.net.LoginTask;
import com.flyingstudio.cumtfriend.net.UserInfoTask;
import com.flyingstudio.cumtfriend.utils.SPUtil;
import com.flyingstudio.cumtfriend.utils.UiUtil;
import com.flyingstudio.cumtfriend.view.LoginActivity;

import org.litepal.LitePal;

import java.util.List;

public class MyFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyFragment() {
    }


    public static MyFragment newInstance(String param1, String param2) {
        MyFragment fragment = new MyFragment();
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
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();

    }

    private void initView() {

        List<User> users = LitePal.findAll(User.class);
        TextView name = getView().findViewById(R.id.username);
        TextView gender = getView().findViewById(R.id.gender);
        TextView school = getView().findViewById(R.id.school);
        TextView major = getView().findViewById(R.id.major);
        TextView stuNum = getView().findViewById(R.id.stuNum);
        if (users.size() != 0) {
            User user = users.get(0);
            name.setText(user.getName());
            gender.setText(user.getGender());
            school.setText(user.getSchool());
            major.setText(user.getMajor());
            stuNum.setText(user.getStuNum());
        } else {
            String username = SPUtil.getValue(getContext(), "username");
            String password = SPUtil.getValue(getContext(), "passsword");
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "获取用户信息失败", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                new LoginTask(getContext(), username, password, new LoginTask.LoginCall() {
                    @Override
                    public void success(String s) {
                        new UserInfoTask(getContext(), s, username).execute();
                    }

                    @Override
                    public void finish() {

                    }

                    @Override
                    public void fail() {

                    }
                }).execute();
            }

        }


        Button exit = getView().findViewById(R.id.exit);
        exit.setOnClickListener(view -> {
            // 删除所有的数据库数据
            LitePal.deleteAll(User.class);
            LitePal.deleteAll(Record.class);
            LitePal.deleteAll(Exam.class);
            LitePal.deleteAll(Subject.class);
            SPUtil.setValue(getContext(), "JSESSIONID", null);
            SPUtil.setValue(getContext(), "username", null);
            SPUtil.setValue(getContext(), "password", null);
            // 退出
            Toast.makeText(getContext(), "退出成功", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);

        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
