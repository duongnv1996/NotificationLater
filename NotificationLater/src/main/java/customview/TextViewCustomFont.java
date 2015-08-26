package customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.kimcy929.notificationlater.R;

/**
 * Created by kimcy on 20/08/2015.
 */
public class TextViewCustomFont extends TextView {
    public TextViewCustomFont(Context context) {
        super(context);
        init(null);
    }

    public TextViewCustomFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TextViewCustomFont(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs,
                    R.styleable.TextViewCustomFont, 0, 0);
            try {
                String fontName = a.getString(R.styleable.TextViewCustomFont_font_name);
                if (!TextUtils.isEmpty(fontName))
                    setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName));
            } catch (Exception e) {
                Log.e(getClass().getName(), "Error set font name");
            } finally {
                a.recycle();
            }

        }
    }
}
