package mw.albumpodrozniczy;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by mstowska on 5/16/2016.
 */
public class GrideViewElement extends ImageView
{
    public GrideViewElement(Context context)
    {
        super(context);
    }

    public GrideViewElement(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public GrideViewElement(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }
}