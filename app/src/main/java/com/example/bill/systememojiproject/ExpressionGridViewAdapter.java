package com.example.bill.systememojiproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 卫彪 on 2017/12/25.
 */
public class ExpressionGridViewAdapter extends BaseAdapter {

    private static final int ITEM_LAYOUT_TYPE_COUNT = 2;
    private static final int TYPE_TEXT = 0;
    private static final int TYPE_IMAGE = 1;
    private LayoutInflater inflater;
    private Context context;
    private List<EmojiEntity> emojiList;

    public ExpressionGridViewAdapter(Context context, List<EmojiEntity> emojiList) {
        this.context = context;
        this.emojiList = emojiList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return emojiList.size();
    }

    @Override
    public EmojiEntity getItem(int position) {
        return emojiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_LAYOUT_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 20) {
            return TYPE_IMAGE;
        } else
            return TYPE_TEXT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int layoutType = getItemViewType(position);
        if (TYPE_TEXT == layoutType) {
            TextHolder viewHolder;
            if (convertView == null) {
                viewHolder = new TextHolder();
                convertView = inflater.inflate(R.layout.adapter_row_expression, parent, false);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_expression);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (TextHolder) convertView.getTag();
            }
            viewHolder.textView.setText(getItem(position).code);
        } else if (TYPE_IMAGE == layoutType) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.adapter_row_expression_img, parent, false);
            }
        }

        return convertView;
    }

    private class TextHolder {
        TextView textView;
    }


}
