/**
 * Copyright 2012
 *
 * Nicolas Desjardins
 * https://github.com/mrKlar
 *
 * Facilite solutions
 * http://www.facilitesolutions.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.burstingbrains.rapmap.view.dragdropgrid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class DragDropGridView extends ViewGroup implements OnTouchListener {

    private static int ANIMATION_DURATION = 250;

    private DragDropGridAdapter adapter;
    private OnClickListener onClickListener = null;

    private SparseIntArray newPositions = new SparseIntArray();

    private int dragged = -1;
    private int columnWidthSize;
    private int rowHeightSize;
    private int biggestChildWidth;
    private int biggestChildHeight;
    private int computedColumnCount;
    private int computedRowCount;
    private int initialX;
    private int initialY;
    private boolean movingView;
    private int lastTarget = -1;

    private int lastTouchX;
    private int lastTouchY;

    public DragDropGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DragDropGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragDropGridView(Context context) {
        super(context);
        init();
    }

    public DragDropGridView(Context context, AttributeSet attrs, int defStyle, DragDropGridAdapter adapter) {
        super(context, attrs, defStyle);
        this.adapter = adapter;
        init();
    }

    public DragDropGridView(Context context, AttributeSet attrs, DragDropGridAdapter adapter) {
        super(context, attrs);
        this.adapter = adapter;
        init();
    }

    public DragDropGridView(Context context, DragDropGridAdapter adapter) {
        super(context);
        this.adapter = adapter;
        init();
    }

    private void init() {
        if (isInEditMode() && adapter == null) {
            useEditModeAdapter();
        }

        setDraggable(true);
    }

    private void useEditModeAdapter() {
        adapter = new DragDropGridAdapter() {

            @Override
            public View view(int index) {
                return null;
            }

            @Override
            public void swapItems(int itemIndexA, int itemIndexB) {}

            @Override
            public int rowCount() {
                return AUTOMATIC;
            }

            @Override
            public int columnCount() {
                return 0;
            }

            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return null;
            }
        };       
    }

    public void setAdapter(DragDropGridAdapter adapter) {
        this.adapter = adapter;
        addChildViews();
    }

    public void setOnClickListener(OnClickListener l) {
        onClickListener = l;
    }

    private void addChildViews() {
        for (int item = 0; item < adapter.getCount(); item++) {
            addView(adapter.view(item));
        }
    }

    private void cancelAnimations() {
        for (int i=0; i < getItemViewCount(); i++) {
            View child = getChildAt(i);
            child.clearAnimation();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        return onTouch(null, event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v == null)
            return false;
        
        int eventRawX = (int) event.getRawX();
        int eventRawY = (int) event.getRawY();
        
        int eventX = Math.max(0, (int) event.getX());
        int eventY = Math.max(0, (int) event.getY());
        
        eventX = Math.min(v.getRight() - v.getLeft() - 1, eventX);
        
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touchDown(v, eventRawX, eventRawY);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(v, eventX, eventY);
                break;
            case MotionEvent.ACTION_UP:
                touchUp(v, eventX, eventY);
                break;
        }
        if (aViewIsDragged())
            return true;
        return false;
    }

    private void touchDown(View v, int eventRawX, int eventRawY) {
        initialX = eventRawX;
        initialY = eventRawY;

        lastTouchX = eventRawX;
        lastTouchY = eventRawY;
        
        if(positionForView(v) != -1) {
            requestDisallowInterceptTouchEvent(true);

            movingView = true;
            dragged = positionForView(v);

            bringDraggedToFront();
            animateDragged();
        }
    }

    private void touchMove(View v, int eventX, int eventY) {
        if (movingView && aViewIsDragged()) {
            lastTouchX = eventX;
            lastTouchY = eventY;

            ensureThereIsNoArtifact();

            moveDraggedView(lastTouchX, lastTouchY);
            manageSwapPosition(lastTouchX, lastTouchY);
        }
    }
    
    private void touchUp(View v, int eventX, int eventY) {
        if(!aViewIsDragged()) {
            if(onClickListener != null) {
                View clickedView = getChildAt(getTargetAtCoor(eventX, eventY));
                if(clickedView != null){
                    onClickListener.onClick(clickedView);
                }
            }
        } else {
            cancelAnimations();

            reorderChildren();

            movingView = false;
            dragged = -1;
            lastTarget = -1;
            
            requestDisallowInterceptTouchEvent(false);
        }
    }
     
    private void ensureThereIsNoArtifact() {
        invalidate();
    }

    private void moveDraggedView(int x, int y) {
        View childAt = getDraggedView();		

        int width = childAt.getMeasuredWidth();
        int height = childAt.getMeasuredHeight();

        int l = x - (1 * width / 2);
        int t = y - (1 * height / 2);

        childAt.layout(l, t, l + width, t + height);
    }

    private void manageSwapPosition(int x, int y) {
        int target = getTargetAtCoor(x, y);
        if (childHasMoved(target) && target != lastTarget) {
            animateGap(target);
            lastTarget = target;
        }
    }

    private void removeItemChildren(List<View> children) {
        for (View child : children) {
            removeView(child);
        }
    }

    private void animateGap(int targetLocationInGrid) {
        int viewAtPosition = currentViewAtPosition(targetLocationInGrid);

        if (viewAtPosition == dragged) {
            return;
        }

        View targetView = getChildView(viewAtPosition);

        //	      Log.e("animateGap target", ((TextView)targetView.findViewWithTag("text")).getText().toString());

        Point oldXY = getCoorForIndex(viewAtPosition);
        Point newXY = getCoorForIndex(newPositions.get(dragged, dragged));

        Point oldOffset = computeTranslationStartDeltaRelativeToRealViewPosition(targetLocationInGrid, viewAtPosition, oldXY);
        Point newOffset = computeTranslationEndDeltaRelativeToRealViewPosition(oldXY, newXY);

        animateMoveToNewPosition(targetView, oldOffset, newOffset);
        saveNewPositions(targetLocationInGrid, viewAtPosition);
    }

    private Point computeTranslationEndDeltaRelativeToRealViewPosition(Point oldXY, Point newXY) {
        return new Point(newXY.x - oldXY.x, newXY.y - oldXY.y);
    }

    private Point computeTranslationStartDeltaRelativeToRealViewPosition(int targetLocation, int viewAtPosition, Point oldXY) {
        Point oldOffset;
        if (viewWasAlreadyMoved(targetLocation, viewAtPosition)) {
            Point targetLocationPoint = getCoorForIndex(targetLocation);
            oldOffset = computeTranslationEndDeltaRelativeToRealViewPosition(oldXY, targetLocationPoint);
        } else {
            oldOffset = new Point(0,0);
        }
        return oldOffset;
    }

    private void saveNewPositions(int targetLocation, int viewAtPosition) {
        newPositions.put(viewAtPosition, newPositions.get(dragged, dragged));
        newPositions.put(dragged, targetLocation);
        tellAdapterToSwapDraggedWithTarget(newPositions.get(dragged, dragged), newPositions.get(viewAtPosition, viewAtPosition));
    }

    private boolean viewWasAlreadyMoved(int targetLocation, int viewAtPosition) {
        return viewAtPosition != targetLocation;
    }

    private void animateMoveToNewPosition(View targetView, Point oldOffset, Point newOffset) {
        Animation translateAnim = createTranslateAnimation(oldOffset, newOffset);
        targetView.clearAnimation();
        targetView.startAnimation(translateAnim);
    }

    private TranslateAnimation createTranslateAnimation(Point oldOffset, Point newOffset) {
        TranslateAnimation translate = new TranslateAnimation(Animation.ABSOLUTE, oldOffset.x,
                Animation.ABSOLUTE, newOffset.x,
                Animation.ABSOLUTE, oldOffset.y,
                Animation.ABSOLUTE, newOffset.y);
        translate.setDuration(ANIMATION_DURATION);
        translate.setFillEnabled(true);
        translate.setFillAfter(true);
        translate.setInterpolator(new AccelerateDecelerateInterpolator());
        return translate;
    }

    private int currentViewAtPosition(int targetLocation) {
        int viewAtPosition = targetLocation;
        for (int i = 0; i < newPositions.size(); i++) {
            int value = newPositions.valueAt(i);
            if (value == targetLocation) {
                viewAtPosition = newPositions.keyAt(i);
                break;
            }
        }
        return viewAtPosition;
    }

    private Point getCoorForIndex(int index) {
        int row = index / computedColumnCount;
        int col = index - (row * computedColumnCount);

        int x = columnWidthSize * col;
        int y = rowHeightSize * row;

        return new Point(x, y);
    }

    private int getTargetAtCoor(int x, int y) {
        int col = getColumnOfCoordinate(x);
        int row = getRowOfCoordinate(y);
        
        int calculatedIndex = col + (row * computedColumnCount);
        int upperLimit = adapter.getCount() - 1;
        
        int actualIndex = Math.min(calculatedIndex, upperLimit);

        return actualIndex;
    }

    private int getColumnOfCoordinate(int x) {
        int col = 0;
        for (int i = 1; i <= computedColumnCount; i++) {
            int colRightBorder = (i * columnWidthSize);
            if (x < colRightBorder) {
                break;
            }
            col++;
        }
        return col;
    }

    private int getRowOfCoordinate(int y) {
        int row = 0;
        for (int i = 1; i <= computedRowCount; i++) {
            if (y < i * rowHeightSize) {
                break;
            }
            row++;
        }
        return row;
    }

    private void reorderChildren() {
        List<View> children = cleanUnorderedChildren();
        addReorderedChildrenToParent(children);
        requestLayout();
    }

    private List<View> cleanUnorderedChildren() {
        List<View> children = saveChildren();
        removeItemChildren(children);
        return children;
    }

    private void addReorderedChildrenToParent(List<View> children) {
        List<View> reorderedViews = reeorderView(children);
        newPositions.clear();

        for (View view : reorderedViews) {
            if (view != null)
                addView(view);
        }
    }

    private List<View> saveChildren() {
        List<View> children = new ArrayList<View>();
        for (int i = 0; i < getItemViewCount(); i++) {
            View child;
            if (i == dragged) {
                child = getDraggedView();
            } else {
                child = getChildView(i);
            }

            child.clearAnimation();
            children.add(child);
        }
        return children;
    }

    private List<View> reeorderView(List<View> children) {
        View[] views = new View[children.size()];

        for (int i = 0; i < children.size(); i++) {
            int position = newPositions.get(i, -1);
            
            if (position >= views.length)
            	continue;
            
            if (childHasMoved(position)) {
                views[position] = children.get(i);
            } 
            else {
                views[i] = children.get(i);
            }
        }
        return new ArrayList<View>(Arrays.asList(views));
    }

    private boolean childHasMoved(int position) {
        return position != -1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        Display display = wm.getDefaultDisplay();

        widthSize = acknowledgeWidthSize(widthMode, widthSize, display);
        heightSize = acknowledgeHeightSize(heightMode, heightSize, display);

        adaptChildrenMeasuresToViewSize(widthSize, heightSize);
        searchBiggestChildMeasures();
        computeGridMatrixSize(widthSize, heightSize);
        computeColumnsAndRowsSizes(widthSize, heightSize);

        setMeasuredDimension(widthSize, (int) (heightSize * 1.5f));
    }

    private void computeColumnsAndRowsSizes(int widthSize, int heightSize) {
        columnWidthSize = widthSize / computedColumnCount;
        rowHeightSize = heightSize / computedRowCount;
    }

    private void computeGridMatrixSize(int widthSize, int heightSize) {
        if (adapter.columnCount() != -1 && adapter.rowCount() != -1) {
            computedColumnCount = adapter.columnCount();
            computedRowCount = adapter.rowCount();
        } else {
            if (biggestChildWidth > 0 && biggestChildHeight > 0) {
                computedColumnCount = widthSize / biggestChildWidth;
                computedRowCount = heightSize / biggestChildHeight;
            }
        }

        if (computedColumnCount == 0) {
            computedColumnCount = 1;
        }

        if (computedRowCount == 0) {
            computedRowCount = 1;
        }
    }


    private void searchBiggestChildMeasures() {
        biggestChildWidth = 0;
        biggestChildHeight = 0;
        for (int index = 0; index < getItemViewCount(); index++) {
            View child = getChildAt(index);

            if (biggestChildHeight < child.getMeasuredHeight()) {
                biggestChildHeight = child.getMeasuredHeight();
            }

            if (biggestChildWidth < child.getMeasuredWidth()) {
                biggestChildWidth = child.getMeasuredWidth();
            }
        }
    }

    private int getItemViewCount() {
        return getChildCount();
    }

    private void adaptChildrenMeasuresToViewSize(int widthSize, int heightSize) {
        if (adapter.columnCount() != DragDropGridAdapter.AUTOMATIC && adapter.rowCount() != DragDropGridAdapter.AUTOMATIC) {
            int desiredGridItemWidth = widthSize / adapter.columnCount();
            int desiredGridItemHeight = heightSize / adapter.rowCount();
            measureChildren(MeasureSpec.makeMeasureSpec(desiredGridItemWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(desiredGridItemHeight, MeasureSpec.AT_MOST));
        } else {
            measureChildren(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }
    }

    @SuppressWarnings("deprecation")
    private int acknowledgeHeightSize(int heightMode, int heightSize, Display display) {
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = display.getHeight();
        }
        return heightSize;
    }

    @SuppressWarnings("deprecation")
    private int acknowledgeWidthSize(int widthMode, int widthSize, Display display) {
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            widthSize = display.getWidth();
        }
        return widthSize;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutPage();

        if (weWereMovingDraggedBetweenPages()) {
            bringDraggedToFront();
        }
    }

    private boolean weWereMovingDraggedBetweenPages() {
        return dragged != -1;
    }

    private void layoutPage() {
        int col = 0;
        int row = 0;
        for (int childIndex = 0; childIndex < adapter.getCount(); childIndex++) {
            layoutAChild(col, row, childIndex);
            col++;
            if (col == computedColumnCount) {
                col = 0;
                row++;
            }
        }
    }

    private void layoutAChild(int col, int row, int childIndex) {
        View child = getChildAt(childIndex);
        
        //rowHeightSize  = child.getHeight();
        //columnWidthSize = child.getWidth();
        int left = 0;
        int top = 0;
        
        left = (col * columnWidthSize) + ((columnWidthSize - child.getMeasuredWidth()) / 2);
        top = (row * rowHeightSize) + ((rowHeightSize - child.getMeasuredHeight()) / 2);
        
        child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
    }

    private void bringDraggedToFront() {
        View draggedView = getChildAt(dragged);
        draggedView.bringToFront();	    	    
    }

    private View getDraggedView() {
        return getChildAt(getChildCount() - 1);
    }

    private void animateDragged() {

        ScaleAnimation scale = new ScaleAnimation(1f, 1.4f, 1f, 1.4f, biggestChildWidth / 2 , biggestChildHeight / 2);
        scale.setDuration(200);
        scale.setFillAfter(true);
        scale.setFillEnabled(true);

        if (aViewIsDragged()) {
            View draggedView = getDraggedView();

            draggedView.clearAnimation();
            draggedView.startAnimation(scale);
        }
    }

    private boolean aViewIsDragged() {
        return weWereMovingDraggedBetweenPages();
    }

    private int positionForView(View v) {
        for (int index = 0; index < getItemViewCount(); index++) {
            View child = getChildView(index);
            if (isPointInsideView(initialX, initialY, child)) {
                return index;
            }
        }
        return -1;
    }

    private View getChildView(int index) {
        if (weWereMovingDraggedBetweenPages()) {
            if (index >= dragged) {
                return getChildAt(index - 1);
            }
        } 

        return getChildAt(index);

    }

    private boolean isPointInsideView(float x, float y, View view) {        
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        if (pointIsInsideViewBounds(x, y, view, viewX, viewY)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean pointIsInsideViewBounds(float x, float y, View view, int viewX, int viewY) {
        return (x > viewX && x < (viewX + view.getWidth())) && (y > viewY && y < (viewY + view.getHeight()));
    }

    private void tellAdapterToSwapDraggedWithTarget(int dragged, int target) {
        adapter.swapItems(dragged, target);
    }
    
    public void setDraggable(boolean isDraggable) {
    	if(isDraggable){
    		setOnTouchListener(this);
    		setClickableForChildren(false);
    	}
    	else{
    		setOnTouchListener(null);
    		setClickableForChildren(true);
    	}
    }
    
    private void setClickableForChildren(boolean isClickable) {
    	
    	for(int i=0; i<getChildCount(); i++) {
			View childView = getChildAt(i);
			childView.setClickable(isClickable);
		}
    }
}