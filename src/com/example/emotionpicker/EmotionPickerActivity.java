package com.example.emotionpicker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

public class EmotionPickerActivity extends Activity {
	private View btnEmotionView;
	private EditText editor;

	private View emotionLayout;
	private GridView emotionGridView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emotion_picker);
		ImageUtils.initEmotion(this);

		initView();
		registerListener();
		bindView();
	}

	private void initView() {

		editor = (EditText) findViewById(R.id.ac_comment_editor);
		btnEmotionView = findViewById(R.id.comment_btn_emotion);

		emotionLayout = findViewById(R.id.layout_emotion);
		emotionGridView = (GridView) findViewById(R.id.grid_emotion_thumb);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				&& emotionLayout.getVisibility() == View.VISIBLE) {
			emotionLayout.setVisibility(View.GONE);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	private void registerListener() {
		editor.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (emotionLayout.getVisibility() == View.VISIBLE) {
					emotionLayout.setVisibility(View.GONE);
					return true;
				}
				return false;
			}
		});
		btnEmotionView.setOnClickListener(clickListener);

		emotionGridView.setAdapter(new ImageAdapter(this));
		emotionGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CharSequence emotionTag = (CharSequence) view.getTag();
				int start = editor.getSelectionStart();
				int end = editor.getSelectionEnd();
				int startPos = Math.min(start, end);
				int endPos = startPos + emotionTag.length();

				editor.getText().replace(Math.min(start, end),
						Math.max(start, end), emotionTag);

				// convert emotion tag to thumb
				InputStream input;
				try {
					input = getAssets().open(
							"smileys" + File.separator
									+ ImageUtils.faceMap.get(emotionTag));
					Bitmap bitmap = BitmapFactory.decodeStream(input);
					bitmap = Bitmap.createScaledBitmap(bitmap, 42, 42, true);

					ImageSpan imageSpan = new ImageSpan(
							EmotionPickerActivity.this, bitmap);
					editor.getText().setSpan(imageSpan, startPos, endPos,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void togleSoftInput(boolean flag) {
		InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (flag) {
			manager.showSoftInput(editor, 0);
		} else {
			manager.hideSoftInputFromWindow(editor.getWindowToken(), 0);
		}
	}

	private void bindView() {
		editor.setText("[悲伤][闭嘴]");
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == btnEmotionView) {
				if (emotionLayout.getVisibility() == View.VISIBLE)
					emotionLayout.setVisibility(View.GONE);
				else {
					togleSoftInput(false);
					emotionLayout.setVisibility(View.VISIBLE);
				}
			} 
		}
	};


	class ImageAdapter extends BaseAdapter {

		private Context mContext;
		private String[] thumbArray;

		public ImageAdapter(Context context) {
			mContext = context;
			thumbArray = ImageUtils.faceMap.keySet().toArray(new String[] {});
		}

		@Override
		public int getCount() {
			return thumbArray.length;
		}

		@Override
		public Object getItem(int position) {
			return thumbArray[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(60, 60));
				imageView.setPadding(8, 8, 8, 8);
			} else {
				imageView = (ImageView) convertView;
			}
			String source = ImageUtils.faceMap.get(thumbArray[position]);
			InputStream input;
			try {
				input = mContext.getAssets().open(
						"smileys" + File.separator + source);
				Bitmap bitmap = BitmapFactory.decodeStream(input);
				imageView.setImageBitmap(bitmap);
				imageView.setTag(thumbArray[position]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return imageView;
		}
	}
}
