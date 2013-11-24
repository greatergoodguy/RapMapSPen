package com.burstingbrains.rapmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.burstingbrains.rapmap.trunk.RapMapDDGAdapter;
import com.burstingbrains.rapmap.trunk.SPen_Example_ImageClip_MiniEditor;
import com.burstingbrains.rapmap.util.ThemeUtils;
import com.burstingbrains.rapmap.view.dragdropgrid.DragDropGridView;

public class HomeActivity extends Activity {

	private enum RapMapMode{ PLAY, EDIT };
	
	private static final int REQUESTCODE_MINIEDITOR = 1000;
	
	RapMapMode mode = RapMapMode.EDIT;
	
	DragDropGridView dragDropGV;
	TextView rapMapModeTV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtils.onActivityCreateSetTheme(this);
		setContentView(R.layout.activity_home);
		
		rapMapModeTV = (TextView) findViewById(R.id.home_header_tv);

		dragDropGV = (DragDropGridView) findViewById(R.id.home_gridview);
		dragDropGV.setAdapter(new RapMapDDGAdapter(this, dragDropGV));

		Button changeThemeButton = (Button) findViewById(R.id.home_button_changetheme);
		changeThemeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ThemeUtils.toggleTheme(HomeActivity.this);
			}
		});

		Button miniEditorButton = (Button) findViewById(R.id.home_button_minieditor);
		miniEditorButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, SPen_Example_ImageClip_MiniEditor.class);
				startActivityForResult(intent, REQUESTCODE_MINIEDITOR);
			}
		});
		
		Button scratchboardButton = (Button) findViewById(R.id.home_button_scratchboard);
		scratchboardButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, SPen_Example_ScratchBoard.class);				
				startActivity(intent);
			}
		});
		
		Button toggleModeButton = (Button) findViewById(R.id.home_button_togglemode);
		toggleModeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isModePlay()) {
					setModeEdit();
				}
				else if(isModeEdit()) {
					setModePlay();
				}
			}
		});
		
		setModeEdit();
	}
	
	public void setModePlay() {
		mode = RapMapMode.PLAY;
		
		rapMapModeTV.setText("PLAY MODE");
		dragDropGV.setDraggable(false);
		
	}
	
	public void setModeEdit() {
		mode = RapMapMode.EDIT;
		
		rapMapModeTV.setText("EDIT MODE");
		dragDropGV.setDraggable(true);
	}
	
	public boolean isModePlay() {
		return mode == RapMapMode.PLAY;
	}
	
	public boolean isModeEdit() {
		return mode == RapMapMode.EDIT;
	}
}
