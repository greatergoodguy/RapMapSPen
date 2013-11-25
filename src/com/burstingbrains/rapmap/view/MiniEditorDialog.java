package com.burstingbrains.rapmap.view;

import android.app.Dialog;
import android.content.Context;

import com.burstingbrains.rapmap.R;

public class MiniEditorDialog extends Dialog {

	public MiniEditorDialog(Context context) {
		super(context);
		setContentView(R.layout.sdk_example_mini_edit);
	}

}
