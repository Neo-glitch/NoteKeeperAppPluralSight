package com.neo.notekeeperpluralsight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;

import java.util.List;

/**
 * FullyCustom view class for showing dots tabs in noteActivity that reps the num of modules for a course completed(colored)
 * todo comeback to fully understand this class
 */
public class ModuleStatusView extends View {
    public static final int EDIT_MODE_MODULE_COUNT = 7;
    public static final int INVALID_INDEX = -1;
    public static final int SHAPE_CIRCLE = 0;
    public static final float DEFAULT_OUTLINE_WIDTH_DP = 2F;


    // my var
    private boolean[] mModuleStatus;    // boolean array for modules and colored is completed
    private float mOutlineWidth;
    private float mShapeSize;
    private float mSpacing;
    private Rect[] mModuleRectangles;
    private int mOutlineColor;
    private Paint mPaintOutline;
    private int mFillColor;
    private Paint mPaintFill;
    private float mRadius;
    private int mMaxHorizontalModules;
    private int mShape;
    private ModuleStatusAccessibilityHelper mAccessibilityHelper;


    public boolean[] getModuleStatus() {
        return mModuleStatus;
    }

    public void setModuleStatus(boolean[] moduleStatus) {
        mModuleStatus = moduleStatus;
    }

    public ModuleStatusView(Context context) {
        super(context);
        init(null, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * where customView initialization work occurs before onDraw is called
     * i.e once an instance of customView class is initially created
     *
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle) {
        if (isInEditMode()) {  // true when used in designer
            setUpEditModeValues();
        }

        setFocusable(true);                                                     // enables View to be accessible when using accessibility sys
        mAccessibilityHelper = new ModuleStatusAccessibilityHelper(this);
        ViewCompat.setAccessibilityDelegate(this, mAccessibilityHelper);    // indicates class providing accessibility info for customView

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();                // gets current dev dm for getting info abt dev display
        float displayDensity = dm.density;                                                  // gets the scaling factor for every dev independent pixel
        float defaultOutlineWidthPixels = displayDensity * DEFAULT_OUTLINE_WIDTH_DP;       // value here is independentPixels passed as constant * device pixel scaling factor = physical pixels


        // Load attributes. TypedArray for getting actual value set. values derived should be assigned to var before .recycle()
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ModuleStatusView, defStyle, 0);

        mOutlineColor = a.getColor(R.styleable.ModuleStatusView_outlineColor, Color.BLACK);
        mShape = a.getInt(R.styleable.ModuleStatusView_shape, SHAPE_CIRCLE);
        // width of the circle line stroke
        mOutlineWidth = a.getDimension(R.styleable.ModuleStatusView_outlineWidth, defaultOutlineWidthPixels);


        a.recycle();

        mShapeSize = 144f;      // shape size of th rect housing the circle
        mSpacing = 30f;         // spacing btw each individual shape
        mRadius = (mShapeSize - mOutlineWidth) / 2;         // logic make sures circle and outline stays in specified rect


        mPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintOutline.setStyle(Paint.Style.STROKE);
        mPaintOutline.setStrokeWidth(mOutlineWidth);
        mPaintOutline.setColor(mOutlineColor);

        // color of the circle representing completed module and it's paint obj
        mFillColor = getContext().getResources().getColor(R.color.pluralsight_orange);
        mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(mFillColor);
    }

    //// methods override to implement the accessibility features.. start////
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        // calls same method in accessibilityHelperClass for added features
        mAccessibilityHelper.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // ret true if handled by accessibilityHelper class else ret false and call superclass method
        return mAccessibilityHelper.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);

    }

    @Override
    protected boolean dispatchHoverEvent(MotionEvent event) {
        return mAccessibilityHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event);
    }
    //// methods override to implement the accessibility features ..end////

    private void setUpEditModeValues() {   // just setup the stuff so as to show preview in designer
        boolean[] exampleModuleValues = new boolean[EDIT_MODE_MODULE_COUNT];
        int middle = EDIT_MODE_MODULE_COUNT / 2;
        for (int i = 0; i < middle; i++) {
            exampleModuleValues[i] = true;
        }
        setModuleStatus(exampleModuleValues);
    }


    /**
     * pos each of the rectangles needed to house the circle when time to draw circle
     */
    private void setUpModuleRectangles(int width) {
        int availableWidth = width - getPaddingLeft() - getPaddingRight();      // gets the width for drawing after view size changes # subbing padding doh
        int horizontalModulesThatCanFit = (int) (availableWidth / (mShapeSize + mSpacing));
        int maxHorizontalModules = Math.min(mModuleStatus.length, horizontalModulesThatCanFit);

        // same size as the modules array for a course
        mModuleRectangles = new Rect[mModuleStatus.length];
        for (int moduleIndex = 0; moduleIndex < mModuleRectangles.length; moduleIndex++) {
            int columns = moduleIndex % maxHorizontalModules;  // if mIdx = 3 and mMax = 4 then col 3 since remainder of 3 / 4 is 3.
            int row = moduleIndex / maxHorizontalModules;      // if moduleIdx = 3 and mMax = 4 then row is 0

            int x = getPaddingLeft() + (int) (columns * (mShapeSize + mSpacing));                     // holds pos of the left edge of rect of each module, for module 0 pos is 0 + padding
            int y = getPaddingTop() + (int) (row * (mShapeSize + mSpacing));                          // holds pos of top edge of each module and gets y from padding in customLayout
            // creates the small rect for housing the circle
            mModuleRectangles[moduleIndex] = new Rect(x, y, x + (int) mShapeSize, y + (int) (mShapeSize));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   // width space and height space available by layout is the param
        int desiredWidth = 0;
        int desiredHeight = 0;

        int specWidth = MeasureSpec.getSize(widthMeasureSpec);              // gets size of width available in layout
        int availableWidth = specWidth - getPaddingLeft() - getPaddingRight();   // width to work with in layout after removing padding dpx

        int horizontalModulesThatCanFit = availableWidth / (int) (mShapeSize + mSpacing);   // logic to get number of circles that can fit the available width
        // if enough width to fit all modules, then maxModule should be the BOOL array length else return other
        mMaxHorizontalModules = Math.min(horizontalModulesThatCanFit, mModuleStatus.length);

        // -mSpacing inorder to avoid the space after the last Item
        desiredWidth = (int) ((mMaxHorizontalModules * (mShapeSize + mSpacing)) - mSpacing);
        desiredWidth += getPaddingLeft() + getPaddingRight();      //adds the padding also

        int rows = ((mModuleStatus.length - 1) / mMaxHorizontalModules) + 1;      // the required number of rows to draw all modules circle
        desiredHeight = (int) (rows * (mShapeSize + mSpacing) - mSpacing);          // -mSpacing inorder to avoid the space after last row
        desiredHeight += getPaddingTop() + getPaddingBottom();

        int width = resolveSizeAndState(desiredWidth, widthMeasureSpec, 0);
        int height = resolveSizeAndState(desiredHeight, heightMeasureSpec, 0);

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {      // called when View size changes for a reason
        setUpModuleRectangles(w);
    }

    @Override
    protected void onDraw(Canvas canvas) {      // where actual drawing takes place
        super.onDraw(canvas);

        // pos each of the circle to draw in right place(each rect in the moduleRect array)
        for (int moduleIndex = 0; moduleIndex < mModuleRectangles.length; moduleIndex++) {
            if (mShape == SHAPE_CIRCLE) {
                // pos of x and y cords of each circle center point from center of rect
                float x = mModuleRectangles[moduleIndex].centerX();
                float y = mModuleRectangles[moduleIndex].centerY();

                if (mModuleStatus[moduleIndex]) {  // if status of element is true
                    canvas.drawCircle(x, y, mRadius, mPaintFill);         // draws filledIn circle
                }
                canvas.drawCircle(x, y, mRadius, mPaintOutline);
            } else {
                drawSquare(canvas, moduleIndex);
            }

        }
    }

    /**
     * method called if user specifies shape to be square in the designer
     *
     * @param moduleIndex
     */
    private void drawSquare(Canvas canvas, int moduleIndex) {
        Rect moduleRectangle = mModuleRectangles[moduleIndex];

        if (mModuleStatus[moduleIndex]) {
            canvas.drawRect(moduleRectangle, mPaintFill);
        }

        canvas.drawRect(moduleRectangle.left + (mOutlineWidth / 2),
                moduleRectangle.top + (mOutlineWidth / 2),
                moduleRectangle.right + (mOutlineWidth / 2),
                moduleRectangle.bottom + (mOutlineWidth / 2),
                mPaintOutline);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                int moduleIndex = findItemAtPoint(event.getX(), event.getY());
                onModuleSelected(moduleIndex);       // updates the value of the selected item
                return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * changes the value of a selected item at the index passed
     */
    private void onModuleSelected(int moduleIndex) {
        if (moduleIndex == INVALID_INDEX) {    // checks if user touches outside selected item
            return;
        }
        mModuleStatus[moduleIndex] = !mModuleStatus[moduleIndex];     // if prev value was true now false
        invalidate();                                                 // calls onDraw() again since data is changed
        mAccessibilityHelper.invalidateVirtualView(moduleIndex);      // notifies accessibility sys that state of VirtualView with id has changed
        mAccessibilityHelper.sendEventForVirtualView(moduleIndex, AccessibilityEvent.TYPE_VIEW_CLICKED);
    }

    /**
     * gets the module item or rect(housing circle) at the cord passed(x, y)
     */
    private int findItemAtPoint(float x, float y) {
        int moduleIndex = INVALID_INDEX;
        for (int i = 0; i < mModuleRectangles.length; i++) {
            if (mModuleRectangles[i].contains((int) x, (int) y)) {   // ret true if x & y cords passed are within the specified rect[] item
                moduleIndex = i;
                break;
            }
        }
        return moduleIndex;
    }

    ////////  Accessibility helper class for enabling accessibility support for the customView class virtualViews(circle) /////////////
    private class ModuleStatusAccessibilityHelper extends ExploreByTouchHelper {

        /**
         * Constructs a new helper that can expose a virtual view hierarchy for the
         * specified host view.
         *
         * @param host view whose virtual view hierarchy is exposed by this helper
         */
        public ModuleStatusAccessibilityHelper(@NonNull View host) {    // view passed is the customView
            super(host);
        }

        @Override
        protected int getVirtualViewAt(float x, float y) {         // gets the virtual view at passed cords when user interacts with it
            int moduleIndex = findItemAtPoint(x, y);
            return moduleIndex == INVALID_INDEX ? ExploreByTouchHelper.INVALID_ID : moduleIndex;
        }

        @Override
        protected void getVisibleVirtualViews(List<Integer> virtualViewIds) {     // assigns Id values to the items of list of virtualViews items
            if (mModuleRectangles == null) {    // avoid having to assign null ids to the VirtualViewId's list
                return;
            }
            for (int moduleIndex = 0; moduleIndex < mModuleRectangles.length; moduleIndex++) {
                virtualViewIds.add(moduleIndex);       // adds the indexes for each of the virtualViews
            }
        }

        @Override
        protected void onPopulateNodeForVirtualView(int virtualViewId, @NonNull AccessibilityNodeInfoCompat node) {     // gives info abt virtualView id passed and called for every VirtualView
            node.setFocusable(true);                                     // makes it possible for virtualViews to be accessible with dpad
            node.setBoundsInParent(mModuleRectangles[virtualViewId]);    // sets the bounds of the virtualView Rect in focus, needed for highlighting of virtualView
            node.setContentDescription("Module " + (virtualViewId + 1));       // in real life, we could have queried db to get module name and used it

            node.setCheckable(true);
            node.setChecked(mModuleStatus[virtualViewId]);               // see whether node is checked base on value in boolean[]
            node.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK);    // enables clicking action to be done on the VirtualView

        }

        @Override
        protected boolean onPerformActionForVirtualView(int virtualViewId, int action, @Nullable Bundle arguments) {  // handles the action enabled for the VirtualView in focus
            switch (action){
                case AccessibilityNodeInfoCompat.ACTION_CLICK:
                    onModuleSelected(virtualViewId);         // calls CustomViewClass method
                    return true;
            }
            return false;
        }
    }

}
