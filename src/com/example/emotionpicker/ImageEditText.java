package com.example.emotionpicker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

public class ImageEditText extends EditText {

	public ImageEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ImageEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ImageEditText(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		Spanned span = convertImageTag(text);
		super.setText(span, type);
	}

	@Override
	protected void onTextChanged(CharSequence text, int start,
			int lengthBefore, int lengthAfter) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
	}

	@Override
	public Editable getText() {
		return super.getText();
	}

	private Spanned convertImageTag(CharSequence text) {
		SpannableString span = new SpannableString(text);
		Set<String> keys = ImageUtils.faceMap.keySet();
		Pattern p = Pattern.compile("\\[[^\\[\\]]+\\]");
		Matcher m = p.matcher(text);
		while(m.find()){
			try {
				InputStream input = getContext().getAssets().open("smileys" + File.separator + ImageUtils.faceMap.get(m.group()));
				if(input != null) {
					Bitmap bitmap = BitmapFactory.decodeStream(input);
					bitmap = Bitmap.createScaledBitmap(bitmap, 42, 42, true);
				
					ImageSpan imageSpan = new ImageSpan(getContext(), bitmap);
					span.setSpan(imageSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			} catch (IOException e) {
				Log.e("sms", "Failed to loaded content " + m.group(), e);
			}
		}
		return span;
	}
	
}
