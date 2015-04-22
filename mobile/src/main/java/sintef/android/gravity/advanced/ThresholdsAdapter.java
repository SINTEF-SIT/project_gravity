/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package sintef.android.gravity.advanced;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import sintef.android.controller.utils.PreferencesHelper;
import sintef.android.gravity.R;

/**
 * Created by samyboy89 on 14/04/15.
 */
public class ThresholdsAdapter extends BaseAdapter {

    private List<ThresholdItem> mItems;

    public ThresholdsAdapter(List<ThresholdItem> items) {
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public ThresholdItem getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_threshold_item, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ThresholdItem thresholdItem = getItem(i);

        if (thresholdItem != null) {
            viewHolder.bindView(thresholdItem);
        }

        return view;
    }

    class ViewHolder {

        private TextView mHeader;
        private EditText mEdit;
        private Button mReset;
        private Button mSave;

        public ViewHolder(View v) {
            mHeader = (TextView) v.findViewById(R.id.header);
            mEdit = (EditText) v.findViewById(R.id.edit);
            mReset = (Button) v.findViewById(R.id.reset);
            mSave = (Button) v.findViewById(R.id.save);
        }

        public void bindView(final ThresholdItem item) {
            mHeader.setText(item.name);
            mEdit.setText(Float.toString(PreferencesHelper.getFloat(item.key, (float) item.defValue)));
            mReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PreferencesHelper.putFloat(item.key, (float) item.defValue);
                    notifyDataSetChanged();
                }
            });
            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PreferencesHelper.putFloat(item.key, Float.valueOf(mEdit.getText().toString()));
                    notifyDataSetChanged();
                }
            });
        }
    }
}
