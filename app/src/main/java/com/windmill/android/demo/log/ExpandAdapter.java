package com.windmill.android.demo.log;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.windmill.android.demo.R;

import java.util.List;

/**
 * created by lance on   2021/12/8 : 5:43 下午
 */
public class ExpandAdapter extends BaseAdapter {

    private List<CallBackItem> mData;
    private Context mContext;
    ClipboardManager manager;

    public ExpandAdapter(Context context, List<CallBackItem> data) {
        this.mContext = context;
        this.mData = data;
        manager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public CallBackItem getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.callback_item, parent, false);
            viewHolder = new MyViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) convertView.getTag();
        }

        CallBackItem callItem = mData.get(position);
        viewHolder.callBackText.setText(callItem.getText());
        if (callItem.is_callback()) {
            viewHolder.callBackText.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        } else {
            viewHolder.callBackText.setTextColor(mContext.getResources().getColor(R.color.colorGray));
        }

        if (TextUtils.isEmpty(callItem.getChild_text())) {
            viewHolder.expandImage.setVisibility(View.GONE);
            viewHolder.infoText.setVisibility(View.GONE);
            viewHolder.btnCopy.setVisibility(View.GONE);
        } else {
            viewHolder.infoText.setText(callItem.getChild_text());
            viewHolder.expandImage.setVisibility(View.VISIBLE);
            if (callItem.is_expand()) {
                viewHolder.infoText.setVisibility(View.VISIBLE);
                viewHolder.btnCopy.setVisibility(View.VISIBLE);
                viewHolder.btnCopy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String content = viewHolder.infoText.getText().toString();
                        manager.setPrimaryClip(ClipData.newPlainText("", content));
                    }
                });
                viewHolder.expandImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.image_arrow_down));
            } else {
                viewHolder.infoText.setVisibility(View.GONE);
                viewHolder.btnCopy.setVisibility(View.GONE);
                viewHolder.expandImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.image_arrow_right));
            }
        }

        return convertView;
    }

    private static class MyViewHolder {

        TextView callBackText;
        ImageView expandImage;
        TextView infoText;
        Button btnCopy;

        public MyViewHolder(View convertView) {
            callBackText = (TextView) convertView.findViewById(R.id.tv_log);
            expandImage = (ImageView) convertView.findViewById(R.id.iv_log);
            infoText = (TextView) convertView.findViewById(R.id.child_info);
            btnCopy = (Button) convertView.findViewById(R.id.btn_copy);
        }
    }

}
