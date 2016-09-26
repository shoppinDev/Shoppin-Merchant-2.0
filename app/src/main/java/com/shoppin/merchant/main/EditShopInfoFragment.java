package com.shoppin.merchant.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.shoppin.merchant.R;
import com.shoppin.merchant.login.EmailValidator;
import com.shoppin.merchant.model.ShopModel;
import com.shoppin.merchant.util.JSONParser;
import com.shoppin.merchant.util.ModuleClass;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditShopInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditShopInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditShopInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    EditText etName,etAddressLine1,etZipCode,etLocationLatitude,etLocationLongitude,etEmailId,etMobile;
    AutoCompleteTextView etCity,etCountry,etState;
    Button btnAddShop;
    ShopModel shopData;
    TextView tvShopHeader;
    private String LOG_TAG = "Auto Search";

    public EditShopInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShopInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditShopInfoFragment newInstance(String param1, String param2) {
        EditShopInfoFragment fragment = new EditShopInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            shopData = (ShopModel) getArguments().getSerializable("shop_data");
        }
    }
    String strCountry="",strState="",strCity="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_info, container, false);

        tvShopHeader = (TextView) view.findViewById(R.id.tvShopHeader);
        tvShopHeader.setText("Edit Shop");

        etAddressLine1 = (EditText) view.findViewById(R.id.input_address_1);
        //etAddressLine2 = (EditText) view.findViewById(R.id.input_address_2);
        etCity = (AutoCompleteTextView) view.findViewById(R.id.input_city);
        etCountry = (AutoCompleteTextView) view.findViewById(R.id.input_country);
        etEmailId = (EditText) view.findViewById(R.id.input_shop_email);
        etLocationLatitude = (EditText) view.findViewById(R.id.input_shop_latitude);
        etLocationLongitude = (EditText) view.findViewById(R.id.input_shop_longitude);
        etMobile = (EditText) view.findViewById(R.id.input_shop_contact);
        etName = (EditText) view.findViewById(R.id.input_name);
        etState = (AutoCompleteTextView) view.findViewById(R.id.input_state);
        etZipCode = (EditText) view.findViewById(R.id.input_zip);

        btnAddShop = (Button) view.findViewById(R.id.btnAddShop);
        btnAddShop.setText("Update Shop");

        final PlacesAutoCompleteAdapter countryAdapter = new PlacesAutoCompleteAdapter(getActivity(), R.layout.spinner_item,"1");
        etCountry.setAdapter(countryAdapter);
        etCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getItemAtPosition(position);
//                Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                strCountry = str;
            }
        });

        etState.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.spinner_item,"2"));
        etState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getItemAtPosition(position);
//                Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                strState = str;

            }
        });

        etCity.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.spinner_item,"3"));
        etCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getItemAtPosition(position);
//                Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                strCity = str;
            }
        });

        btnAddShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etName.getText().toString().equals("")){
                    Toast.makeText(getActivity(),"Please enter a shop name",Toast.LENGTH_LONG).show();
                    return;
                }

                if(etAddressLine1.toString().equals("")){
                    Toast.makeText(getActivity(),"Please enter a shop address",Toast.LENGTH_LONG).show();
                    return;
                }

                if(etMobile.toString().equals("")){
                    Toast.makeText(getActivity(),"Please enter a Contact number",Toast.LENGTH_LONG).show();
                    return;
                }

                if(etEmailId.toString().equals("")){
                    Toast.makeText(getActivity(),"Please enter a Email address",Toast.LENGTH_LONG).show();
                    return;
                }

                if(!isEmailValid(etEmailId.getText().toString())){
                    //tilEmail.setError("Email address is incorrect");
                    Toast.makeText(getActivity(),"Email address is incorrect",Toast.LENGTH_LONG).show();
                    return;
                }

                if(etCity.toString().equals("")){
                    Toast.makeText(getActivity(),"Please enter a City",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!etCity.getText().toString().equals(strCity)){
                    Toast.makeText(getActivity(),"Invalid City",Toast.LENGTH_LONG).show();
                    return;
                }

                if(etState.toString().equals("")){
                    Toast.makeText(getActivity(),"Please enter a state",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!etState.getText().toString().equals(strState)){
                    Toast.makeText(getActivity(),"Invalid State",Toast.LENGTH_LONG).show();
                    return;
                }

                if(etCountry.toString().equals("")){
                    Toast.makeText(getActivity(),"Please enter a Country",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!etCountry.getText().toString().equals(strCountry)){
                    Toast.makeText(getActivity(),"Invalid Country",Toast.LENGTH_LONG).show();
                    return;
                }

                if(etZipCode.toString().equals("")){
                    Toast.makeText(getActivity(),"Please enter a Zip code",Toast.LENGTH_LONG).show();
                    return;
                }

                if(ModuleClass.isInternetOn){
                    new EditShopTask(shopData.getShopId(),
                            etName.getText().toString(),
                            etAddressLine1.getText().toString(),
                            etEmailId.getText().toString(),
                            etMobile.getText().toString(),
                            etLocationLatitude.getText().toString(),
                            etLocationLongitude.getText().toString(),
                            etCountry.getText().toString(),
                            etCity.getText().toString(),
                            etState.getText().toString(),
                            etZipCode.getText().toString()).execute();
                }
            }
        });

        return view;
    }

    private boolean isEmailValid(String paramString) {
        return EmailValidator.isValidEmail(paramString);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(shopData != null){
            initViewWithData(shopData);
        }
    }

    public void initViewWithData(ShopModel data){
        etAddressLine1.setText(data.getShopAddress());
        etCity.setText(data.getShopCity());
        strCity = data.getShopCity();
        strCountry = data.getShopCountry();
        strState = data.getShopState();
        etCountry.setText(data.getShopCountry());
        etEmailId.setText(data.getShopEmail());
        etLocationLatitude.setText(data.getShopLatitude());
        etLocationLongitude.setText(data.getShopLongitude());
        etMobile.setText(data.getShopMobile());
        etName.setText(data.getShopName());
        etState.setText(data.getShopState());
        etZipCode.setText(data.getShopZip());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class EditShopTask extends AsyncTask<Void,Void,Void> {

        String shopId,email,mobileNo,address,country,city,zipCode,state,latitude,longitude,name;
        boolean success;
        String responseError;
        ProgressDialog dialog;
        public EditShopTask(String shopId,String name,String address,String email,String mobileNo,String latitude,String longitude,String country,String city,String state,String zipCode){
            this.shopId = shopId;
            this.email = email;
            this.mobileNo = mobileNo;
            this.address = address;
            this.country = country;
            this.city = city;
            this.zipCode = zipCode;
            this.latitude = latitude;
            this.longitude = longitude;
            this.state = state;
            this.name = name;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            if(success){
                Toast.makeText(getActivity(),"Shop successfully Updated",Toast.LENGTH_LONG).show();
                Fragment fragment = new MyShopFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container,fragment);
                ft.commit();
            }else{
                Toast.makeText(getActivity(),responseError,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> inputArray = new ArrayList<>();
            inputArray.add(new BasicNameValuePair("webmethod", "editshop"));
            inputArray.add(new BasicNameValuePair("shop_id",shopId));
            inputArray.add(new BasicNameValuePair("name", name));
            inputArray.add(new BasicNameValuePair("shop_addres", address));
            inputArray.add(new BasicNameValuePair("shop_latitude", latitude));
            inputArray.add(new BasicNameValuePair("shop_longitude", longitude));
            inputArray.add(new BasicNameValuePair("shop_email", email));
            inputArray.add(new BasicNameValuePair("shop_mobile", mobileNo));
            inputArray.add(new BasicNameValuePair("shop_city", city));
            inputArray.add(new BasicNameValuePair("shop_state", state));
            inputArray.add(new BasicNameValuePair("shop_zip", zipCode));
            inputArray.add(new BasicNameValuePair("shop_country", country));

            JSONObject responseJSON = new JSONParser().makeHttpRequest(ModuleClass.LIVE_API_PATH+"merchant.php", "GET", inputArray);
            Log.d("Edit shop ", responseJSON.toString());

            if(responseJSON != null && !responseJSON.toString().equals("")) {

                success = true;
                try {
                    JSONArray dataArray = responseJSON.getJSONArray("data");
                    String Result = dataArray.getString(0);
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

            dialog = new ProgressDialog(getActivity(),R.style.MyThemeDialog);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(true);
            dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            dialog.show();
        }
    }

    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;
        String url_point;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId,String url) {
            super(context, textViewResourceId);
            this.url_point = url;
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        Log.d(LOG_TAG, "V-"+constraint.toString());
                        resultList = autocomplete(url_point,constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }};
            return filter;
        }
    }

    private ArrayList<String> autocomplete(String urlpoint,String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {

/*	        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
	        sb.append("?key=" + API_KEY);
	        sb.append("&components=country:uk");
	        sb.append("&input=" + URLEncoder.encode(input, "utf8"));
*/
//	    	String urll = "http://eutopicviews.com/api/search_api.php?action=allsearch&name="+URLEncoder.encode(input, "utf8");
            String newurl = "";
            String filtervalue = URLEncoder.encode(input, "utf-8");
            String str1="",str2="",str3="";
            str1 = URLEncoder.encode(etCountry.getText().toString(),"utf-8");
            str2 = URLEncoder.encode(etState.getText().toString(),"utf-8");
            str3 = URLEncoder.encode(etCity.getText().toString(),"utf-8");

            if (urlpoint.equals("1")) {
                newurl = ModuleClass.SERVER_PATH1+"country_list&countryname="+str1;
            }else if (urlpoint.equals("2")) {
                newurl = ModuleClass.SERVER_PATH1+"state_list&countryid="+str1+"&statename="+str2;
            }else if (urlpoint.equals("3")) {
                newurl = ModuleClass.SERVER_PATH1+"city_list&stateid="+str2+"&cityname="+str3;
            }else {
//                newurl = ModuleClass.SERVER_PATH+"country_list&countryname="+str1;
            }

            Log.d("URLL", newurl);

            ///////////////////////


            URL url = new URL(newurl);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("data");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("name"));
                Log.e(LOG_TAG, predsJsonArray.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }


}
