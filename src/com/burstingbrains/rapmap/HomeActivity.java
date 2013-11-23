package com.burstingbrains.rapmap;

import android.app.Activity;
import android.os.Bundle;

import com.burstingbrains.rapmap.trunk.RapMapDDGAdapter;
import com.burstingbrains.rapmap.view.dragdropgrid.DragDropGrid;

public class HomeActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		 DragDropGrid gridview = (DragDropGrid) findViewById(R.id.editmenu_gridview);
	     gridview.setAdapter(new RapMapDDGAdapter(this, gridview));
	}
}
