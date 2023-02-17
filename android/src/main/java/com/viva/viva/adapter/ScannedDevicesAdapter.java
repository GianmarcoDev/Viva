package com.viva.viva.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omronhealthcare.OmronConnectivityLibrary.OmronLibrary.Model.OmronPeripheral;

import java.util.ArrayList;

/**
 * Created by Omron HealthCare Inc
 */

public class ScannedDevicesAdapter {

    private final Context context;

    private ArrayList<OmronPeripheral> mPeripheralList;
    public ScannedDevicesAdapter(Context context, ArrayList<OmronPeripheral> peripheralList) {
        this.context = context;
        mPeripheralList = peripheralList;
    }

    public void setPeripheralList(ArrayList<OmronPeripheral> peripheralList) {

        mPeripheralList = peripheralList;
    }


    public int getCount() {
        return mPeripheralList.size();
    }


    public OmronPeripheral getItem(int position) {
        return mPeripheralList.get(position);
    }


    public long getItemId(int position) {
        return position;
    }


}
