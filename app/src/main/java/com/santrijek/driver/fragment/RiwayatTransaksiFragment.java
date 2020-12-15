package com.santrijek.driver.fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import com.santrijek.driver.MainActivity;
import com.santrijek.driver.R;
import com.santrijek.driver.adapter.ItemListener;
import com.santrijek.driver.adapter.RiwayatTransaksiAdapter;
import com.santrijek.driver.database.DBHandler;
import com.santrijek.driver.database.Queries;
import com.santrijek.driver.model.RiwayatTransaksi;
import com.santrijek.driver.model.Transaksi;
import com.santrijek.driver.network.HTTPHelper;
import com.santrijek.driver.network.Log;
import com.santrijek.driver.network.NetworkActionResult;
//import driver.pacekurir.drivermangjek.preference.UserPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;


public class RiwayatTransaksiFragment extends Fragment{
    private static final String TAG = RiwayatTransaksiFragment.class.getSimpleName();
    private View rootView;
    MainActivity activity;
    ArrayList<RiwayatTransaksi> arrRiwayat;
    ArrayList<Transaksi> arrTransaksi;
    private ItemListener.OnItemTouchListener onItemTouchListener;
    private RecyclerView reviRiwayat;
    private RiwayatTransaksiAdapter riwayatAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipe;
    Queries que;
    int maxRetry = 4;
    private HorizontalCalendar horizontalCalendar;
    public RiwayatTransaksiFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.riwayat_transaksi_fragments, container, false);

        activity = (MainActivity) getActivity();
//        activity.getSupportActionBar().setTitle("Riwayat Transaksi");

        que = new Queries(new DBHandler(activity));
        reviRiwayat = (RecyclerView) rootView.findViewById(R.id.reviRiwayat);
        reviRiwayat.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(activity);

        arrRiwayat = que.getAllRiwayatTransaksi();
//        initData();
        initListener();
        updateListView();

        swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeFeedback);
        swipe.setRefreshing(false);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        swipe.post(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });


        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        /* end after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);
        horizontalCalendar = new HorizontalCalendar.Builder(rootView, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .configure()
                .formatTopText("MMM")
                .formatMiddleText("dd")
                .formatBottomText("EEE")
                .textSize(10f, 20f, 10f)
                .showTopText(true)
                .showBottomText(true)
                .textColor(Color.LTGRAY, Color.YELLOW)
                .end()
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                Toast.makeText(getContext(), DateFormat.format("EEE, MMM d, yyyy", date) + " is selected!", Toast.LENGTH_SHORT).show();


            }

        });


        return rootView;
    }

    private void initData(){
        final ProgressDialog sl = showLoading();
        JSONObject jFeed = new JSONObject();
        try {
            jFeed.put("id", que.getDriver().id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPHelper.getInstance(activity).getRiwayatTransaksi(jFeed, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                arrRiwayat = HTTPHelper.getInstance(activity).parseRiwayatTransaksi(obj);
                que.truncate(DBHandler.TABLE_RIWAYAT_TRANSAKSI);
                que.insertRiwayatTransaksi(arrRiwayat);
                swipe.setRefreshing(false);
                updateListView();
                sl.dismiss();
                maxRetry = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if(maxRetry == 0){
                    sl.dismiss();
                    Toast.makeText(activity, "Koneksi bermasalah..", Toast.LENGTH_SHORT).show();
                    maxRetry = 4;
                    swipe.setRefreshing(false);
                }else{
                    initData();
                    maxRetry--;
                    Log.d("Try_ke_feedback", String.valueOf(maxRetry));
                    sl.dismiss();
                }
            }
        });
    }

    private void initListener() {
        onItemTouchListener = new ItemListener.OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
            }

            @Override
            public void onButton1Click(View view, int position) {
            }

            @Override
            public void onButton2Click(View view, int position) {
            }
        };
    }

    private void updateListView(){
        reviRiwayat.setLayoutManager(mLayoutManager);
        arrRiwayat = que.getAllRiwayatTransaksi();
        riwayatAdapter = new RiwayatTransaksiAdapter(arrRiwayat, onItemTouchListener, activity);
        reviRiwayat.setAdapter(riwayatAdapter);
        reviRiwayat.setVerticalScrollbarPosition(arrRiwayat.size()-1);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        que.closeDatabase();
    }

    private ProgressDialog showLoading(){
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading...", true);
        return ad;
    }

}
