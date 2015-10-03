package com.mandaptak.android.Views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TypefaceTextView extends TextView {

  private static Typeface mTypeface;

  public TypefaceTextView(final Context context) {
    this(context, null);
  }

  public TypefaceTextView(final Context context, final AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TypefaceTextView(final Context context, final AttributeSet attrs, final int defStyle) {
    super(context, attrs, defStyle);

    if (mTypeface == null) {
      mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/myriad_pro.ttf");
    }
    setTypeface(mTypeface);
  }
}
