package customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.kimcy929.notificationlater.R;

/**
 * Created by vanchung on 19/07/2015.
 */

public class TintImageView extends ImageView {
    private int tintColor = 0xffffff;

    public TintImageView(Context context) {
        super(context);
    }

    public TintImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TintImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TintImageView, defStyleAttr, 0);
        try {
            tintColor = a.getColor(R.styleable.TintImageView_tintColor, 0xffffff);
            setColorFilter(tintColor);
        } finally {
            a.recycle();
        }
    }
}
