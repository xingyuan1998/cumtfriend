package com.flyingstudio.cumtfriend.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyingstudio.cumtfriend.R;
import com.flyingstudio.cumtfriend.adapter.ExamRecAdapter;
import com.flyingstudio.cumtfriend.adapter.RecordRecAdapter;
import com.flyingstudio.cumtfriend.entity.Exam;
import com.flyingstudio.cumtfriend.entity.ExamData;
import com.flyingstudio.cumtfriend.entity.Record;
import com.flyingstudio.cumtfriend.entity.RecordData;
import com.flyingstudio.cumtfriend.net.Constant;
import com.flyingstudio.cumtfriend.utils.ACache;
import com.flyingstudio.cumtfriend.utils.SPUtil;
import com.flyingstudio.cumtfriend.view.LoginActivity;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.model.CacheMode;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.litepal.LitePal;

import java.util.List;


public class InfoFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<Exam> exams;
    private RecyclerView examRecyclerView;
    private RecyclerView recordRecyclerView;
    private ExamRecAdapter examAdapter;
    private RecordRecAdapter recordRecAdapter;
    private List<Record> records;
    private String token;
    private TextView recordBlank;
    private TextView examBlank;
    private ACache cache;

    public InfoFragment() {
        // Required empty public constructor
    }


    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        token = SPUtil.getValue(getContext(), "token");
        if (TextUtils.isEmpty(token)) Log.d("TOKEN IS NONE", "onActivityCreated: ");
        initView();
    }

    private void initView() {
        cache = ACache.get(getContext());
        examRecyclerView = getView().findViewById(R.id.exam_rec);
        examRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));
        recordRecyclerView = getView().findViewById(R.id.record_rec);
        recordRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));
        recordBlank = getView().findViewById(R.id.record_blank);
        examBlank = getView().findViewById(R.id.exam_blank);
        getData();

//        getExams();
//        getRecords();
    }

    private void getData() {
        String exam_is_validate = cache.getAsString("exam");
        String record_is_validate = cache.getAsString("record");
        if (!TextUtils.isEmpty(exam_is_validate)) {
            exams = LitePal.findAll(Exam.class);
            if (exams.size() != 0) examBlank.setVisibility(View.GONE);
            if (exams != null) {
                if (examAdapter == null) {
                    examAdapter = new ExamRecAdapter(exams, getContext());
                    examRecyclerView.setAdapter(examAdapter);
                } else {
                    examAdapter.notifyDataSetChanged();
                }
            }
        } else {
            getExamFormInternet();
        }

        if (!TextUtils.isEmpty(record_is_validate)) {
            records = LitePal.findAll(Record.class);
            if (records.size() != 0)
                recordBlank.setVisibility(View.GONE);
            if (recordRecAdapter == null) {
                recordRecAdapter = new RecordRecAdapter(records, getContext());
                recordRecyclerView.setAdapter(recordRecAdapter);
            } else {
                recordRecAdapter.notifyDataSetChanged();
            }
        } else getRecordFormInternet();
    }

    private void getRecordFormInternet() {

        EasyHttp.get(Constant.GET_RECORDS)
                .baseUrl(Constant.BASE_URL)
                .headers("token", token)
                .cacheTime(24 * 60 * 60 * 1000)
                .cacheKey("record_item")
                .cacheMode(CacheMode.CACHEANDREMOTE)
                .execute(new SimpleCallBack<RecordData>() {
                    @Override
                    public void onError(ApiException e) {
                        Log.e("GET_RECORD", "onError: " + e.getMessage());
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                        if (e.getCode() == 4000) {
//                            Intent intent = new Intent(getContext(), LoginActivity.class);
//                            startActivity(intent);
//                        }
                    }

                    @Override
                    public void onSuccess(RecordData recordData) {
                        if (recordData.getRecords().size() != 0)
                            recordBlank.setVisibility(View.GONE);
                        if (recordRecAdapter == null) {

                            recordRecAdapter = new RecordRecAdapter(recordData.getRecords(), getContext());
                            recordRecyclerView.setAdapter(recordRecAdapter);
                        } else {
                            recordRecAdapter.notifyDataSetChanged();
                        }
                        LitePal.deleteAll(Record.class);
                        for (Record record : recordData.getRecords()) {
                            record.save();
                        }
                        cache.put("record", "true", 24 * 60 * 60);
                    }
                });
    }

    private void getExamFormInternet() {
        EasyHttp.get(Constant.GET_EXAMS)
                .baseUrl(Constant.BASE_URL)
                .headers("token", token)
                .execute(new SimpleCallBack<ExamData>() {
                    @Override
                    public void onError(ApiException e) {
                        Log.e("GET_EXAM", "onError: " + e.getMessage());
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(ExamData examData) {
//                        Log.d("GET_EXAM", "onSuccess: " + examData.getExams().size());

                        if (examData.getExams().size() != 0) examBlank.setVisibility(View.GONE);
                        if (examData != null) {

                            exams = examData.getExams();
                            if (examAdapter == null) {
                                examAdapter = new ExamRecAdapter(exams, getContext());
                                examRecyclerView.setAdapter(examAdapter);
                            } else {
                                examAdapter.notifyDataSetChanged();
                            }
                            cache.put("exam", "true", 24 * 60 * 60);
                            LitePal.deleteAll(Exam.class);
                            for (Exam exam:exams) {
                                exam.save();
                            }
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
