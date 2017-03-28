package com.lasalle.lsmaker_remote.adapters;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lasalle.lsmaker_remote.R;
import com.lasalle.lsmaker_remote.activities.ConnectionActivity;

import java.util.List;
import java.util.Map;

/**
 * The DeviceListAdapter is an adapter to be used with the bluetooth device's ListView.
 *
 * The adapter formats the aspect of every cell in the list to show it's name, address and rssi value.
 *
 * @author Eduard de Torres
 * @version 1.0.0
 */
public class DeviceListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
    private ConnectionActivity activity;
    private List<BluetoothDevice> devices;
    private LayoutInflater inflater;
    private Map<String, Integer> devRssiValues;


    public DeviceListAdapter(ConnectionActivity activity,
                             List<BluetoothDevice> devices,
                             Map<String, Integer> devRssiValues) {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
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
            activity.showListHasResults(true);
        }
    }

    public List<BluetoothDevice> getDevices() {
        return devices;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = getDevices().get(position);
        activity.attemptLogin(device);
    }
}
