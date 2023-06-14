package com.yj.planrun;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.yj.planrun.R;

public class ActiveRecord extends RelativeLayout {
    public ActiveRecord(Context context) {
        super(context);
    }
    private void init(Context context){
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.record_active,this,false);
    }

}
