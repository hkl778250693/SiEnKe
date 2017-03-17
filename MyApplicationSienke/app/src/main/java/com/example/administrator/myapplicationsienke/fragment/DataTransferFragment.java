package com.example.administrator.myapplicationsienke.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.activity.DownloadActivity;
import com.example.administrator.myapplicationsienke.activity.UploadActivity;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class DataTransferFragment extends Fragment {
    private View view;
    private TextView upload,download;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data_transfer,null);
        //绑定控件ID
        bindView();
        return view;
    }

    //绑定控件
    public void bindView(){
        upload = (TextView) view.findViewById(R.id.upload);
        download = (TextView) view.findViewById(R.id.download);

        //点击事件
        upload.setOnClickListener(clickListener);
        download.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.upload:
                    Intent intent=new Intent(getActivity(), UploadActivity.class);
                    startActivity(intent);
                    break;
                case R.id.download:
                    Intent intent1=new Intent(getActivity(), DownloadActivity.class);
                    startActivity(intent1);
                    break;
            }
        }
    };
}
