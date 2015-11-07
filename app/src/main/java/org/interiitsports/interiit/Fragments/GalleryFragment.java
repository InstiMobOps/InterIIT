package org.interiitsports.interiit.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.interiitsports.interiit.Adapters.GalleryAdapter;
import org.interiitsports.interiit.Adapters.NewPauseOnScrollListener;
import org.interiitsports.interiit.Objects.GridImage;
import org.interiitsports.interiit.R;
import org.interiitsports.interiit.Utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {
    RecyclerView mRecyclerView;
    GalleryAdapter mAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);
         mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.contentView);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getImages();
            }
        });
        // Inflate the layout for this fragment
        mRecyclerView = (RecyclerView) v.findViewById(R.id.hot_fragment_recycler);
        mRecyclerView.setHasFixedSize(true);

        // The number of Columns
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setOnScrollListener(new NewPauseOnScrollListener(ImageLoader.getInstance(), true, true));
        getImages();

        return v;
    }

    public ArrayList<GridImage> getImages() {
        String url ="http://students2.iitm.ac.in/studentsapp/test/testimages.php";
        final ArrayList<GridImage> images =new ArrayList<GridImage>();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        mSwipeRefreshLayout.setRefreshing(true);
        final JSONArray[] jsonArray = {null};


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

               if(response!=null){
                   try {
                       jsonArray[0] =response.getJSONArray("images");
                   } catch (JSONException e) {
                       e.printStackTrace();

                   }
                   Log.d("Log", response.toString());
                   Utils.saveprefString("imgrespons", response.toString(),getActivity());
               }else{
                   try {
                       JSONObject jsonObject=new JSONObject(Utils.getprefString("imgrespons",getActivity()));
                       jsonArray[0] =jsonObject.getJSONArray("images");
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }

               }
                for (int i =0;i< jsonArray[0].length();i++){
                    JSONObject jo = null;
                    try {
                        jo = jsonArray[0].getJSONObject(i);
                        images.add(new GridImage(jo.getString("flag"),jo.getString("country")));
                        Log.d("Log", jo.getString("flag"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                mAdapter = new GalleryAdapter(getActivity(), images);
                mRecyclerView.setAdapter(mAdapter);
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                // VolleyLog.d(TAG, "Error: " + error.getMessage());


                    try {
                        JSONObject jsonObject=new JSONObject(Utils.getprefString("imgrespons",getActivity()));
                        jsonArray[0] =jsonObject.getJSONArray("images");
                        for (int i =0;i< jsonArray[0].length();i++) {
                            JSONObject jo = null;
                            jo = jsonArray[0].getJSONObject(i);
                            images.add(new GridImage(jo.getString("flag"), jo.getString("country")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


            }
        });
        queue.add(jsonObjReq);


        return images;
    }
}
