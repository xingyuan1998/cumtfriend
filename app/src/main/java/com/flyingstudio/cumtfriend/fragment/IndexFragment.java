package com.flyingstudio.cumtfriend.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyingstudio.cumtfriend.MainActivity;
import com.flyingstudio.cumtfriend.R;
import com.flyingstudio.cumtfriend.adapter.IndexRecAdapter;
import com.flyingstudio.cumtfriend.adapter.SubjectRecAdapter;
import com.flyingstudio.cumtfriend.entity.Index;
import com.flyingstudio.cumtfriend.entity.Subject;
import com.flyingstudio.cumtfriend.net.Constant;
import com.flyingstudio.cumtfriend.utils.SPUtil;
import com.flyingstudio.cumtfriend.utils.UiUtil;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.model.CacheMode;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class IndexFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView indexRecyclerView;
    private RecyclerView kitRecyclerView;
    private RecyclerView subjectRecyclerView;
    private SubjectRecAdapter subjectRecAdapter;
    private IndexRecAdapter indexRecAdapter;
    private IndexRecAdapter kitRecAapter;
    private List<Index> webs = new ArrayList<>();
    private List<Index> kits = new ArrayList<>();

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public IndexFragment() {
        // Required empty public constructor
    }


    public static IndexFragment newInstance(String param1, String param2) {
        IndexFragment fragment = new IndexFragment();
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
        initView();
    }

    private void initView() {
        String week = SPUtil.getValue(getContext(), "target_week");
        int thisWeek = Integer.parseInt(week);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) w = 7;
        Log.d("THIS WEEK", "initView: " + w);


        subjectRecyclerView = getView().findViewById(R.id.subject_rec);
        subjectRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));
        List<Subject> subjects = LitePal.findAll(Subject.class);
        List<Subject> subjectToday = new ArrayList<>();
        for (Subject subject : subjects) {
            if (subject.getWeek_list().contains(thisWeek) && subject.getDay() == w) {
                subjectToday.add(subject);
            }
        }

        TextView subjectBlank = getView().findViewById(R.id.subject_blank);
        if (subjectToday.size() > 0)subjectBlank.setVisibility(View.GONE);

        SubjectRecAdapter adapter = new SubjectRecAdapter(getContext(), subjectToday);
        subjectRecyclerView.setAdapter(adapter);


        indexRecyclerView = getView().findViewById(R.id.index_rec);
        indexRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5, GridLayoutManager.VERTICAL, false));
        kitRecyclerView = getView().findViewById(R.id.kit_rec);
        kitRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5, GridLayoutManager.VERTICAL, false));


        EasyHttp.get(Constant.GET_INDEX)
                .baseUrl(Constant.BASE_URL)
//                    .headers("token", token)
                .cacheTime(24 * 60 * 60 * 1000)
                .cacheKey("index_item")
                .cacheMode(CacheMode.CACHEANDREMOTE)
                .execute(new SimpleCallBack<List<Index>>() {
                    @Override
                    public void onError(ApiException e) {
                        Log.e("GET_INDEX", "onError: " + e.getMessage());
                        Log.d("GET_INDEX", "onError: " + e.getCode());
//                        if (e.getCode() == 4000){
//                            Intent intent = new Intent(getContext(), LoginActivity.class);
//                            startActivity(intent);
//                        }
                    }

                    @Override
                    public void onSuccess(List<Index> indices) {
                        Log.e("GET_INDEX", "onSuccess: " + indices.size());
                        if (indices != null) {
                            for (Index i : indices) {
                                if (i.getType().equals("web")) {
                                    webs.add(i);
                                } else {
                                    kits.add(i);
                                }
                            }
                            Log.d("TAS", "onSuccess: " + webs.size() + kits.size());

                            if (indexRecAdapter == null) {
                                indexRecAdapter = new IndexRecAdapter(webs, getContext());
                                indexRecyclerView.setAdapter(indexRecAdapter);
                            } else {
                                indexRecAdapter.notifyDataSetChanged();
                            }
                            if (kitRecAapter == null) {
                                kitRecAapter = new IndexRecAdapter(kits, getContext());
                                kitRecyclerView.setAdapter(kitRecAapter);
                            }

                            TextView indexBlank = getView().findViewById(R.id.index_blank);
                            TextView kitBlank = getView().findViewById(R.id.kit_blank);
                            if (webs.size() != 0) indexBlank.setVisibility(View.GONE);
                            if (kits.size() != 0) kitBlank.setVisibility(View.GONE);
                        }
                    }
                });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_index, container, false);
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
