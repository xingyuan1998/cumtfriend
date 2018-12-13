package com.flyingstudio.cumtfriend.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyingstudio.cumtfriend.MainActivity;
import com.flyingstudio.cumtfriend.R;
import com.flyingstudio.cumtfriend.adapter.IndexRecAdapter;
import com.flyingstudio.cumtfriend.entity.Index;
import com.flyingstudio.cumtfriend.net.Constant;
import com.flyingstudio.cumtfriend.utils.SPUtil;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.model.CacheMode;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.util.List;


public class IndexFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView indexRecyclerView;
    private IndexRecAdapter indexRecAdapter;
    private List<Index> indices;

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


        Toolbar toolbar = getView().findViewById(R.id.toolbar);
        MainActivity activity = (MainActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.setTitle("快速导航");

        indexRecyclerView = getView().findViewById(R.id.index_rec);
        indexRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5, GridLayoutManager.VERTICAL, false));
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
                            if (indexRecAdapter == null) {
                                indexRecAdapter = new IndexRecAdapter(indices, getContext());
                                indexRecyclerView.setAdapter(indexRecAdapter);
                            } else {
                                indexRecAdapter.notifyDataSetChanged();
                            }
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
