package com.example.alfa.popularmovies;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * Created by Alfa on 4/13/2018.
 */

public class AdroitGridLayout extends GridLayoutManager {
    private int mColumnWidth;
    private boolean mColumnWidthChanged = true;
    public AdroitGridLayout(Context context, int columnWidth) {
        super(context,  1);
        setColumnWidth(checkColumnWidth(context, columnWidth));

    }
    /* Initially set spanCount to 1, will be changed automatically later. */
    public AdroitGridLayout(Context context, int columnWidth, int orientation, boolean reverseLayout) {
        super(context, 1, orientation, reverseLayout);
        setColumnWidth(checkColumnWidth(context, columnWidth));

    }
    private int checkColumnWidth(Context context, int columnWidth){
        if (columnWidth <= 0) { /* Set default columnWidth value (48dp here). It is better to move this constant to static constant on top, but we need context to convert it to dp, so can't really do so. */
            columnWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, context.getResources().getDisplayMetrics());
        }
        return columnWidth;

    }
    private void setColumnWidth(int newWidth){
        if(newWidth>0&&newWidth!=mColumnWidth){
            mColumnWidth=newWidth;
            mColumnWidthChanged=true;
        }

    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(mColumnWidthChanged&&mColumnWidth>0){
            int totalSpace = 0;
            if(getOrientation()==VERTICAL){
                totalSpace=getWidth()-getPaddingRight()-getPaddingLeft();
            }
            if(getOrientation()==HORIZONTAL){
                totalSpace=getHeight()-getPaddingTop()-getPaddingBottom();
            }
            int spanCount=Math.max(1,totalSpace/mColumnWidth);
            setSpanCount(spanCount);
            mColumnWidthChanged=false;
        }
        super.onLayoutChildren(recycler, state);
    }
}
