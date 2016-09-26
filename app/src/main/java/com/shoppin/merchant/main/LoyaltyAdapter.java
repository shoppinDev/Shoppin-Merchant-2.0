package com.shoppin.merchant.main;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shoppin.merchant.R;
import com.shoppin.merchant.model.LoyaltyDataModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jaimin Patel on 20-Jun-16.
 */
public class LoyaltyAdapter extends ArrayAdapter<LoyaltyDataModel> {

    LayoutInflater inflater;
    HashMap<String,Boolean> checkBoxMap = new HashMap<String,Boolean>();
    ArrayList<LoyaltyDataModel> dealModelList = new ArrayList<>();
    Activity mContext;
    FragmentManager fm;

    public LoyaltyAdapter(Activity context, int resource, ArrayList<LoyaltyDataModel> dealList, FragmentManager fm) {
        super(context, resource, dealList);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dealModelList = dealList;
        this.fm = fm;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.list_item_loyalty, parent, false);
        }

        final LoyaltyDataModel data = getItem(position);

        TextView tvLoyaltyname = (TextView) view.findViewById(R.id.tvLoyaltyName);
        TextView tvShopname = (TextView) view.findViewById(R.id.lblShopname);
        TextView tvNopin = (TextView) view.findViewById(R.id.tvnoofpin);

/*
        Button btnEditDeal = (Button) view.findViewById(R.id.btnEditDeal);
        btnEditDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new EditDealFragment();
                Bundle args = new Bundle();
                args.putSerializable("deal_data",data);
                fragment.setArguments(args);
                FragmentTransaction ft = fm.beginTransaction();
                ft.addToBackStack("");
                ft.replace(R.id.fragment_container,fragment);
                ft.commit();
            }
        });
*/


        tvLoyaltyname.setText(data.getLoyaltyname());
        tvShopname.setText(data.getShopId());
        tvNopin.setText(data.getPincount());

        return view;
    }

    @Override
    public LoyaltyDataModel getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    public HashMap getCheckedHashMap(){
        return checkBoxMap;
    }

    public int getCheckedCount(){
        int count = 0;
        Log.v("Notification","Shop list size : "+dealModelList.size());
        Log.v("Notification"," Checkbox map size : "+checkBoxMap.size());
        for(int i = 0 ; i < dealModelList.size(); i++ ){
            if(checkBoxMap.get(dealModelList.get(i).getShopId()))
                count++;
        }
        return count;
    }

}
