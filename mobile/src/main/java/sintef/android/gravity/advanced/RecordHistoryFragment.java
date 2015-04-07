package sintef.android.gravity.advanced;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import sintef.android.controller.utils.PreferencesHelper;
import sintef.android.gravity.R;
import sintef.android.gravity.wizard.FloatingHintEditText;

/**
 * Created by samyboy89 on 23/02/15.
 */
public class RecordHistoryFragment extends Fragment {

    private RecordHistoryAdapter mAdapter;
    private List<RecordData> mData;

    @InjectView(R.id.list)                      ListView mListView;
    @InjectView(R.id.server_ip)                 FloatingHintEditText mServerIp;
    @InjectView(R.id.server_port)               FloatingHintEditText mServerPort;

    private static final String SERVER_IP = "server_ip";
    private static final String SERVER_PORT = "server_port";

    private static final String DEFAULT_SERVER_IP = "projectgravity.no-ip.org";
    private static final String DEFAULT_SERVER_PORT = "8765";

    public static final String RECORD_HISTORY = "record_history";
    public static final String SPLIT = "%";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() == null) return;

        ButterKnife.inject(this, getView());

        mServerIp.setText(PreferencesHelper.getString(SERVER_IP, DEFAULT_SERVER_IP));
        mServerPort.setText(PreferencesHelper.getString(SERVER_PORT, DEFAULT_SERVER_PORT));

        mData = getJSONData();
        Collections.reverse(mData);
        mAdapter = new RecordHistoryAdapter();
        mListView.setAdapter(mAdapter);
    }

    public void notifyOnDataSetChanged() {
        mData = getJSONData();
        Collections.reverse(mData);
        mAdapter.notifyDataSetChanged();
    }


    public List<RecordData> getJSONData() {
        List<String> records = PreferencesHelper.getStringArrayPref(RECORD_HISTORY);
        List<RecordData> recordDatas = new ArrayList<>();
        for (String unparsed : records) {
            String[] parsed = unparsed.split(SPLIT);
            recordDatas.add(new RecordData(parsed[0], parsed[1]));
        }
        return recordDatas;
    }

    public static void saveJSONDataToDisk(String id, String jsonData) {
        List<String> previous_records = PreferencesHelper.getStringArrayPref(RECORD_HISTORY);
        if (previous_records.size() > 9) {
            previous_records.remove(0);
        }

        previous_records.add(id + SPLIT + jsonData);

        PreferencesHelper.setStringArrayPref(RECORD_HISTORY, previous_records);
    }

    public class RecordHistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public RecordData getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_record_history_item, viewGroup, false);
                holder = new ViewHolder((ViewGroup) convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            RecordData data = getItem(i);
            if (data != null) {
                holder.bindData(data, i);
            }

            return convertView;
        }

        public class ViewHolder {

            public ViewGroup mRoot;
            public TextView mTitleView;
            public Button mSendButton;

            public ViewHolder(ViewGroup view) {
                mRoot = view;
                mTitleView = (TextView) view.findViewById(R.id.record_title);
                mSendButton = (Button) view.findViewById(R.id.send_button);
            }

            public void bindData(final RecordData data, int position) {
                mTitleView.setText(String.valueOf(position+1) + ": " + data.mId);
                mSendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {

                                final String ip = mServerIp.getText().toString();
                                final String port = mServerPort.getText().toString();
                                PreferencesHelper.putString(SERVER_IP, ip);
                                PreferencesHelper.putString(SERVER_PORT, port);

                                try {
                                    Socket socket = new Socket(ip, Integer.valueOf(port));
                                    DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
                                    DOS.writeBytes(data.mJSONData);
                                    socket.close();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(), "Sensor data sent to server " + ip + ":" + port, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(), "Sensor data failed to send to " + ip + ":" + port , Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                return null;
                            }
                        }.execute();
                    }
                });
            }

        }
    }

    public class RecordData {
        public String mId;
        public String mJSONData;

        public RecordData(String mId, String mJSONData) {
            this.mId = mId;
            this.mJSONData = mJSONData;
        }
    }

}
