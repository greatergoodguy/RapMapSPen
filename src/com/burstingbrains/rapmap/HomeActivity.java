package com.burstingbrains.rapmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.burstingbrains.rapmap.trunk.RapMapDDGAdapter;
import com.burstingbrains.rapmap.trunk.SPen_Example_ImageClip_MiniEditor;
import com.burstingbrains.rapmap.util.ThemeUtils;
import com.burstingbrains.rapmap.view.dragdropgrid.DragDropGrid;

public class HomeActivity extends Activity {
	
	private static final int REQUESTCODE_MINIEDITOR = 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtils.onActivityCreateSetTheme(this);
		setContentView(R.layout.activity_home);

		DragDropGrid gridview = (DragDropGrid) findViewById(R.id.editmenu_gridview);
		gridview.setAdapter(new RapMapDDGAdapter(this, gridview));

		Button changeThemeButton = (Button) findViewById(R.id.editmenu_button_changetheme);
		changeThemeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ThemeUtils.toggleTheme(HomeActivity.this);
			}
		});

		Button miniEditorButton = (Button) findViewById(R.id.editmenu_button_minieditor);
		miniEditorButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, SPen_Example_ImageClip_MiniEditor.class);
				startActivityForResult(intent, REQUESTCODE_MINIEDITOR);
			}
		});
	}
}
