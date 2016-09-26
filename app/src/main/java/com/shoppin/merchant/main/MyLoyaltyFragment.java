package com.shoppin.merchant.main;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shoppin.merchant.R;
import com.shoppin.merchant.model.LoyaltyDataModel;
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
public class MyLoyaltyFragment extends Fragment {

    private ListView listMyDeal;
    TextView tvEmptyDeal;
    LoyaltyAdapter adapter;
    private Handler messageHandler;

    public MyLoyaltyFragment(){}
    ArrayList<LoyaltyDataModel> loyaltyArrayList = new ArrayList<>();
    public void setMessageHandler(Handler handler){
        messageHandler = handler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_loyalty, container, false);

        listMyDeal = (ListView) view.findViewById(R.id.listMyLoyalty);

        tvEmptyDeal = (TextView) view.findViewById(R.id.tvEmptyLoyalty);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(ModuleClass.isInternetOn){
            new GetDealTask().execute();
        }
    }

    class GetDealTask extends AsyncTask<Void,Void,Void> {

        boolean success;
        String responseError;
        ProgressDialog dialog;
        public GetDealTask(){

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            if(success){
                if(loyaltyArrayList.size() > 0){
                    adapter = new LoyaltyAdapter(getActivity(),android.R.layout.simple_list_item_multiple_choice,loyaltyArrayList,getFragmentManager());
                    
                    listMyDeal.setAdapter(adapter);
                }else{
                    tvEmptyDeal.setVisibility(View.VISIBLE);
                }

            }else{
                Toast.makeText(getActivity(),responseError,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> inputArray = new ArrayList<>();
            inputArray.add(new BasicNameValuePair("webmethod", "my_loyalty"));
            inputArray.add(new BasicNameValuePair("id", ModuleClass.MERCHANT_ID));

            JSONObject responseJSON = new JSONParser().makeHttpRequest(ModuleClass.LIVE_API_PATH+"merchant.php", "GET", inputArray);
            Log.d("Get My Deal ", responseJSON.toString());

            if(responseJSON != null && !responseJSON.toString().equals("")) {

                success = true;
                try {
                    JSONArray dataArray = responseJSON.getJSONArray("data");

                    for(int i = 0;i < dataArray.length();i++){
                        JSONObject object = dataArray.getJSONObject(i);
                        LoyaltyDataModel data = new LoyaltyDataModel(object.getString("loyalty_id"),object.getString("merchant_id"),
                                object.getString("shops"),object.getString("loyalty_name"),object.getString("no_of_pins"));                        


                        loyaltyArrayList.add(data);
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

}
