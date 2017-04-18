/*
 * Copyright 2012 Terlici Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.terlici.dragndroplist;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erhannis.unstableart.R;

public class DragNDropCursorAdapter extends CursorAdapter implements DragNDropAdapter {
	public static enum RowType {
		BEGIN, END, NODE
	}

	int mPosition[];
	int mHandler;
	int mLayout;
	String[] mFromText;
	int[] mToText;
	String mFromRowType;
	String mFromLevel;

	protected LayoutInflater mCursorInflater;

	public DragNDropCursorAdapter(Context context, int layout, Cursor cursor, String[] fromText, int[] toText, String fromRowType, String fromLevel, int handler) {
		super(context, cursor, 0);

		mCursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mLayout = layout;
		mFromText = fromText;
		mToText = toText;
		mFromRowType = fromRowType;
		mFromLevel = fromLevel;
		mHandler = handler;
		setup();
	}
	
	@Override
	public Cursor swapCursor(Cursor c) {
		Cursor cursor = super.swapCursor(c);
		
		mPosition = null;
		setup();
		
		return cursor;
	}
	
	private void setup() {
		Cursor c = getCursor();
		
		if (c == null || !c.moveToFirst()) return;
		
		mPosition = new int[c.getCount()];
		
		for (int i = 0; i < mPosition.length; ++i) mPosition[i] = i;
	}
	
	@Override
	public View getDropDownView(int position, View view, ViewGroup group) {
		return super.getDropDownView(mPosition[position], view, group);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mCursorInflater.inflate(mLayout, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		for (int i = 0; i < mFromText.length; i++) {
			TextView textViewTitle = (TextView) view.findViewById(mToText[i]);
			String title = cursor.getString(cursor.getColumnIndex(mFromText[i]));
			textViewTitle.setText(title);
		}
		View handler = view.findViewById(mHandler);
		TextView tvSpace = (TextView)view.findViewById(R.id.tvSpace);
		if (mFromRowType != null) {
			String type = cursor.getString(cursor.getColumnIndex(mFromRowType));
			switch (RowType.valueOf(type)) {
				case BEGIN:
					//TODO Could be weird
					handler.setVisibility(View.VISIBLE);
					break;
				case END:
					// Invisible?
					handler.setVisibility(View.GONE);
					break;
				case NODE:
					handler.setVisibility(View.VISIBLE);
					break;
			}
		}
		if (mFromLevel != null) {
			int level = cursor.getInt(cursor.getColumnIndex(mFromLevel));
			//tvSpace.setWidth(level * 8);
			StringBuilder sb = new StringBuilder();
			if (level > 0) {
				sb.append("+-");
			}
			for (int i = 1; i < level; i++) {
				sb.append("--");
			}
			tvSpace.setText(sb.toString());
		}
	}

	@Override
	public Object getItem(int position) {
		return super.getItem(mPosition[position]);
	}
	
	@Override
	public int getItemViewType(int position) {
		return super.getItemViewType(mPosition[position]);
	}
	
	@Override
	public long getItemId(int position) {
		return super.getItemId(mPosition[position]);
	}
	
	@Override
	public View getView(int position, View view, ViewGroup group) {
		return super.getView(mPosition[position], view, group);
	}
	
	@Override
	public boolean isEnabled(int position) {
		return super.isEnabled(mPosition[position]);
	}

	@Override
	public void onItemDrag(DragNDropListView parent, View view, int position, long id) {

	}

	@Override
	public void onItemDrop(DragNDropListView parent, View view, int startPosition, int endPosition, long id) {
		int position = mPosition[startPosition];
		
		if (startPosition < endPosition)
			for(int i = startPosition; i < endPosition; ++i)
				mPosition[i] = mPosition[i + 1];
		else if (endPosition < startPosition)
			for(int i = startPosition; i > endPosition; --i)
				mPosition[i] = mPosition[i - 1];
		
		mPosition[endPosition] = position;
	}

	@Override
	public int getDragHandler() {
		return mHandler;
	}
	
}
