package com.burstingbrains.rapmap.trunk;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.burstingbrains.rapmap.R;

public class RapItemView extends LinearLayout{
    
	private TextView nameTV;
	
    public RapItemView(Context context, String nameString) {
        super(context);
        initView(context);
        nameTV.setText(nameString);
    }
    
    public RapItemView(Context context) {
        super(context);
        initView(context);
    }

    public RapItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

	
    public RapItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
        View.inflate(context, R.layout.view_rapitem, this);
        nameTV = ((TextView) findViewById(R.id.editmenuitem_name));
    }
}