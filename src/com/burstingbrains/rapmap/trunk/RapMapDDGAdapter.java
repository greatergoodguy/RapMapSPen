package com.burstingbrains.rapmap.trunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.burstingbrains.rapmap.R;
import com.burstingbrains.rapmap.view.RapItemView;
import com.burstingbrains.rapmap.view.dragdropgrid.DragDropGridAdapter;
import com.burstingbrains.rapmap.view.dragdropgrid.DragDropGridView;

public class RapMapDDGAdapter extends DragDropGridAdapter {

    private Context context;
    private DragDropGridView gridview;

    List<RapItemView> itemViews = new ArrayList<RapItemView>();

    public RapMapDDGAdapter(Context context, DragDropGridView gridview) {
        super();
        this.context = context;
        this.gridview = gridview;

        itemViews.add(new RapItemView(context, "Item 1"));
        itemViews.add(new RapItemView(context, "Item 2"));
        itemViews.add(new RapItemView(context, "Item 3"));
        itemViews.add(new RapItemView(context, "Item 4"));
        itemViews.add(new RapItemView(context, "Item 5"));
        itemViews.add(new RapItemView(context, "Item 6"));
        itemViews.add(new RapItemView(context, "Item 7"));
        itemViews.add(new RapItemView(context, "Item 8"));
        itemViews.add(new RapItemView(context, "Item 9"));
        itemViews.add(new RapItemView(context, "Item 10"));
        itemViews.add(new RapItemView(context, "Item 11"));
        itemViews.add(new RapItemView(context, "Item 12"));
        itemViews.add(new RapItemView(context, "Item 13"));
        itemViews.add(new RapItemView(context, "Item 14"));
        itemViews.add(new RapItemView(context, "Item 15"));
        itemViews.add(new RapItemView(context, "Item 16"));
        itemViews.add(new RapItemView(context, "Item 17"));
        itemViews.add(new RapItemView(context, "Item 18"));
        itemViews.add(new RapItemView(context, "Item 19"));
        itemViews.add(new RapItemView(context, "Item 20"));
        itemViews.add(new RapItemView(context, "Item 21"));
        itemViews.add(new RapItemView(context, "Item 22"));
        //itemViews.add(new RapItemView(context, "Item 23"));
        //itemViews.add(new RapItemView(context, "Item 24"));
    }

    @Override
    public View view(int index) {
        return itemViews.get(index);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setViewBackground(LinearLayout layout) {
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            layout.setBackground(context.getResources().getDrawable(R.drawable.list_selector_holo_light));
        }
    }

    @Override
    public int rowCount() {
        return AUTOMATIC;
    }

    @Override
    public int columnCount() {
        return AUTOMATIC;
    }

    @Override
    public void swapItems(int itemIndexA, int itemIndexB) {
    	if(itemIndexA >= itemViews.size())	return;
    	if(itemIndexB >= itemViews.size())	return;
    	
        Collections.swap(itemViews, itemIndexA, itemIndexB);
    }

    // ======================================
    // Base Adapter Methods
    // ======================================
    @Override
    public int getCount() {
        return itemViews.size();
    }

    @Override
    public Object getItem(int position) {        
        return itemViews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return view(position);
    }
}