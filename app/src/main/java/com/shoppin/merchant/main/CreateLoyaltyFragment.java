package com.shoppin.merchant.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shoppin.merchant.R;
import com.shoppin.merchant.model.ShopModel;
import com.shoppin.merchant.util.JSONParser;
import com.shoppin.merchant.util.ModuleClass;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jaimin Patel on 20-Jun-16.
 */
public class CreateLoyaltyFragment extends Fragment {

    private static final String ARG_PARAM1 = "";
    private static final String ARG_PARAM2 = "";
    private String mParam1;
    private String mParam2;
    private ListView listShop;
    private FrameLayout frameListContainer;
    private Button btnAddShop;
    public static final int REQUEST_CODE_ADD_SHOP = 1005;
    ArrayList<ShopModel> shopList = new ArrayList<>();
    ShopListAdapter adapter;
    private TextView tvShopEmpty;

    EditText edtLoyaltiName,edtNumberofpin;
    private Button btnCreateLoyalty;

    public CreateLoyaltyFragment(){}
    Handler messageHandler;

    public void setMessageHandler(Handler handler){
        messageHandler = handler;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddDealFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddDealFragment newInstance(String param1, String param2) {
        AddDealFragment fragment = new AddDealFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_createloyalty, container, false);

        listShop = (ListView) view.findViewById(R.id.listShop);
        listShop.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        frameListContainer = (FrameLayout) view.findViewById(R.id.frameLayListContainer);

        btnAddShop = (Button) view.findViewById(R.id.btnAddShop);
        btnAddShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ShopInfoFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                fragment.setTargetFragment(CreateLoyaltyFragment.this,REQUEST_CODE_ADD_SHOP);
                ft.replace(R.id.fragment_container,fragment);
                ft.commit();
            }
        });

        tvShopEmpty = (TextView) view.findViewById(R.id.tvShopEmpty);
        edtLoyaltiName = (EditText) view.findViewById(R.id.input_loyaltiname);
        edtNumberofpin = (EditText) view.findViewById(R.id.input_numberof_pin);


        btnCreateLoyalty = (Button) view.findViewById(R.id.btnAddLoyalty);
        btnCreateLoyalty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(adapter != null) {
                    if (adapter.getCheckedCount() <= 0) {
                        Toast.makeText(getActivity(), "Select at least one shop", Toast.LENGTH_LONG).show();
                        return;
                    }
                }else{
                    Toast.makeText(getActivity(), "Select at least one shop", Toast.LENGTH_LONG).show();
                    return;
                }
                String shopId = adapter.getMultipleSelectedId();
                Log.d("Mulitplee",shopId);

                if(!edtLoyaltiName.getText().toString().equals("")){
                    if(!edtNumberofpin.getText().toString().equals("")){
                        Integer pin = Integer.valueOf(edtNumberofpin.getText().toString());
                        if(pin > 0 && pin <= 12) {
                            if (ModuleClass.isInternetOn) {
                                new AddLoyalty(shopId, edtLoyaltiName.getText().toString(), edtNumberofpin.getText().toString()).execute();
                            }
                        }else {
                            Toast.makeText(getActivity(), "12 maximum limit", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getActivity(), "Number of pins should not be empty", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getActivity(), "Loyalty Name should not be empty", Toast.LENGTH_LONG).show();
                }
            }
        });

        return  view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ADD_SHOP){
            Log.v("Notification","Shop Added successfully");
            if(ModuleClass.isInternetOn){
                new GetShopTask().execute();
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListViewHeightBasedOnItems(listShop);

        if(ModuleClass.isInternetOn){
            new GetShopTask().execute();
        }
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + 40;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }
    }

    class GetShopTask extends AsyncTask<Void,Void,Void> {

        boolean success;
        String responseError;
        ProgressDialog dialog;
        public GetShopTask(){
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            if(success){
                if(shopList.size() > 0){
                    adapter = new ShopListAdapter(getActivity(),android.R.layout.simple_list_item_multiple_choice,shopList);
                    listShop.setAdapter(adapter);
                    ModuleClass.setListViewHeightBasedOnItems(listShop);
                }else{
                    tvShopEmpty.setVisibility(View.VISIBLE);
                }

            }else{
                Toast.makeText(getActivity(),responseError,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> inputArray = new ArrayList<>();
            inputArray.add(new BasicNameValuePair("webmethod", "my_shop"));
            inputArray.add(new BasicNameValuePair("merchantid", ModuleClass.MERCHANT_ID));

            JSONObject responseJSON = new JSONParser().makeHttpRequest(ModuleClass.LIVE_API_PATH+"merchant.php", "GET", inputArray);
            Log.d("Get Shop ", responseJSON.toString());

            if(responseJSON != null && !responseJSON.toString().equals("")) {

                success = true;
                try {
                    JSONArray dataArray = responseJSON.getJSONArray("data");

                    for(int i = 0;i < dataArray.length();i++){
                        JSONObject object = dataArray.getJSONObject(i);

                        ShopModel data = new ShopModel(
                                object.getString("shop_id"),
                                object.getString("merchant_id"),
                                object.getString("shop_name"),
                                object.getString("shop_addres"),
                                object.getString("shop_latitude"),
                                object.getString("shop_longitude"),
                                object.getString("shop_email"),
                                object.getString("shop_mobile"),
                                object.getString("qr_image"),
                                object.getString("shop_add"),
                                object.getString("shop_city"),
                                object.getString("shop_state"),
                                object.getString("shop_zip"),
                                object.getString("shop_country"),
                                object.getString("is_active"),
                                object.getString("added_date")
                        );
                        shopList.add(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
                responseError = "There is some problem in server connection";
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity(), R.style.MyThemeDialog);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(true);
            dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            dialog.show();
        }
    }


    class AddLoyalty extends AsyncTask<Void,Void,Void> {

        String shopId,loyaltiname,numberofpin;
        boolean success;
        String responseError,responseResult;
        ProgressDialog dialog;
        public AddLoyalty(String shopId,String name,String pin){
            this.shopId = shopId;
            this.loyaltiname = name;
            this.numberofpin = pin;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            if(success){
                if(responseResult.equals("0")){
                    Toast.makeText(getActivity(),"Only one loyalty allowed per shop",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getActivity(),"Loyalty successfully created",Toast.LENGTH_LONG).show();

                    if(messageHandler != null) {
                        Message msg = new Message();
                        Bundle b = new Bundle();
                        b.putBoolean("loyalty_created", true);
                        msg.setData(b);
                        messageHandler.sendMessage(msg);
                    }
                }
            }else{
                Toast.makeText(getActivity(),responseError,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> inputArray = new ArrayList<>();
            inputArray.add(new BasicNameValuePair("webmethod", "addloyalty"));
            inputArray.add(new BasicNameValuePair("merchantid", ModuleClass.MERCHANT_ID));
            inputArray.add(new BasicNameValuePair("shop_id", shopId));
            inputArray.add(new BasicNameValuePair("loyalty_name", loyaltiname));
            inputArray.add(new BasicNameValuePair("noofpins", numberofpin));

            JSONObject responseJSON = new JSONParser().makeHttpRequest(ModuleClass.LIVE_API_PATH+"merchant.php", "GET", inputArray);
            Log.d("Add Deal ", responseJSON.toString());

            if(responseJSON != null && !responseJSON.toString().equals("")) {
                success = true;
                try {
                    JSONArray dataArray = responseJSON.getJSONArray("data");
                    responseResult = dataArray.getString(0);
                    Log.v("Notification","Result Data : "+dataArray.getString(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
                responseError = "There is some problem in server connection";
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity(), R.style.MyThemeDialog);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(true);
            dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            dialog.show();
        }
    }

}
