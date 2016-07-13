package com.lasalle.lsmaker_remote.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lasalle.lsmaker_remote.R;
import com.lasalle.lsmaker_remote.activities.ConnectionActivity;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class DeviceListAdapter extends BaseAdapter {
    private Context context;
    private List<BluetoothDevice> devices;
    private LayoutInflater inflater;
    private Map<String, Integer> devRssiValues;

    public DeviceListAdapter(Context context,
                             List<BluetoothDevice> devices,
                             Map<String, Integer> devRssiValues) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.devices = devices;
        this.devRssiValues = devRssiValues;
    }

    @Override
    public int getCount() {
        if (devices != null)
            return devices.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup vg;

        if (convertView != null) {
            vg = (ViewGroup) convertView;
        } else {
            vg = (ViewGroup) inflater.inflate(R.layout.device_list_item, null);
        }

        BluetoothDevice device = devices.get(position);
        final TextView tvAddress = ((TextView) vg.findViewById(R.id.device_list_item_address));
        final TextView tvName = ((TextView) vg.findViewById(R.id.device_list_item_name));
        final TextView tvRssi = (TextView) vg.findViewById(R.id.device_list_item_rssi);

        tvRssi.setVisibility(View.VISIBLE);
        byte rssival = (byte) devRssiValues.get(device.getAddress()).intValue();
        if (rssival != 0) {
            StringBuilder text =new StringBuilder("Rssi = ").append(String.valueOf(rssival));
            tvRssi.setText(text);
        }

        tvName.setText(device.getName());
        tvAddress.setText(device.getAddress());

        return vg;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (devices != null && devices.size() > 0) {
            ((ConnectionActivity) context).showListHasResults(true);
        }
    }

    public List<BluetoothDevice> getDevices() {
        return devices;
    }
}
