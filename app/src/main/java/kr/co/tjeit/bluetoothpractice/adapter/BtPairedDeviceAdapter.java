package kr.co.tjeit.bluetoothpractice.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import kr.co.tjeit.bluetoothpractice.R;
import kr.co.tjeit.bluetoothpractice.data.BTDevice;

/**
 * Created by the on 2017-09-13.
 */

public class BtPairedDeviceAdapter extends ArrayAdapter<BTDevice> {
    Context mContext;
    List<BTDevice> mList;
    LayoutInflater inf;

    public BtPairedDeviceAdapter(Context context, List<BTDevice> list) {
        super(context, R.layout.btdevice_list_item, list);

        mContext = context;
        mList = list;
        inf = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            row = inf.inflate(R.layout.btdevice_list_item, null);
        }

        return row;
    }
}
