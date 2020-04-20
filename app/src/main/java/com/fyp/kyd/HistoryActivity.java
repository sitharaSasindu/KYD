package com.fyp.kyd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.fyp.qr.MyApplication;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "histry";
    private VerificationView verificationView;
    private ProgressBar progressBar;
    private LinearLayout h1, h2, h3, h4;
    private TextView h111, h222, h333, h444;
    private ImageView h11, h22, h33, h44;
    private DividerView h1111, h2222, h3333, h4444;
    private TextView hashtxt;
    private ImageView iconsuccess;
    public static boolean reqresponse = false;
    MyRecyclerViewAdapter adapter;
    private List<MyListData> movieList = new ArrayList<>();
    private List<MyListData> movieList2 = new ArrayList<>();
    private List<MyListData> movieList3 = new ArrayList<>();
    private List<MyListData> movieList4 = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    private RecyclerView recyclerView3;
    private RecyclerView recyclerView4;
    private MyRecyclerViewAdapter mAdapter;
    private MyRecyclerViewAdapter mAdapter2;
    private MyRecyclerViewAdapter mAdapter3;
    private ImageView imgPoster, imgPoster2, imgPoster3, imgPoster4;
    private static String stellarHash1;

    public static String getStellarHash1() {
        return stellarHash1;
    }

    public static String getStellarHash2() {
        return stellarHash2;
    }

    public static String getStellarHash3() {
        return stellarHash3;
    }

    public static String getStellarHash4() {
        return stellarHash4;
    }

    public static void setStellarHash1(String stellarHash1) {
        HistoryActivity.stellarHash1 = stellarHash1;
    }

    public static void setStellarHash2(String stellarHash2) {
        HistoryActivity.stellarHash2 = stellarHash2;
    }

    public static void setStellarHash3(String stellarHash3) {
        HistoryActivity.stellarHash3 = stellarHash3;
    }

    public static void setStellarHash4(String stellarHash4) {
        HistoryActivity.stellarHash4 = stellarHash4;
    }

    private static String stellarHash2;
    private static String stellarHash3;
    private static String stellarHash4;

    public static void setA(int a) {
        HistoryActivity.a = a;
    }

    public static void setB(int b) {
        HistoryActivity.b = b;
    }

    public static void setC(int c) {
        HistoryActivity.c = c;
    }

    public static void setD(int d) {
        HistoryActivity.d = d;
    }

    public static int getA() {
        return a;
    }

    public static int getB() {
        return b;
    }

    public static int getC() {
        return c;
    }

    public static int getD() {
        return d;
    }

    public static int a, b, c, d = 0;

    public static Boolean getCheckA() {
        return checkA;
    }

    public static Boolean getCheckB() {
        return checkB;
    }

    public static Boolean getCheckC() {
        return checkC;
    }

    public static Boolean getCheckD() {
        return checkD;
    }

    public static void setCheckA(Boolean checkA) {
        HistoryActivity.checkA = checkA;
    }

    public static void setCheckB(Boolean checkB) {
        HistoryActivity.checkB = checkB;
    }

    public static void setCheckC(Boolean checkC) {
        HistoryActivity.checkC = checkC;
    }

    public static void setCheckD(Boolean checkD) {
        HistoryActivity.checkD = checkD;
    }

    public static Boolean checkA, checkB, checkC, checkD= false;
    private MyRecyclerViewAdapter mAdapter4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        verificationView = findViewById(R.id.layout_verify);
        progressBar = findViewById(R.id.progressBar2);
        h1 = findViewById(R.id.h1);
        h11 = findViewById(R.id.h11);
        h1111 = findViewById(R.id.h1111);
        h2 = findViewById(R.id.h2);
        h22 = findViewById(R.id.h22);
        h2222 = findViewById(R.id.h2222);
        h3 = findViewById(R.id.h3);
        h33 = findViewById(R.id.h33);
        h3333 = findViewById(R.id.h3333);
        h4 = findViewById(R.id.h4);
        h44 = findViewById(R.id.h44);
//        h4444 = findViewById(R.id.h4444);
        imgPoster = findViewById(R.id.h11);
        imgPoster2 = findViewById(R.id.h22);
        imgPoster3 = findViewById(R.id.h33);
        imgPoster4 = findViewById(R.id.h44);

        verificationView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        Intent intent = getIntent();
        String pkgid = intent.getStringExtra("pkgid");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView2 = (RecyclerView) findViewById(R.id.recycler_view2);
        recyclerView3 = (RecyclerView) findViewById(R.id.recycler_view3);
        recyclerView4 = (RecyclerView) findViewById(R.id.recycler_view4);

        mAdapter = new MyRecyclerViewAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mAdapter2 = new MyRecyclerViewAdapter(movieList2);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        recyclerView2.setLayoutManager(mLayoutManager2);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setAdapter(mAdapter2);

        mAdapter3 = new MyRecyclerViewAdapter(movieList3);
        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(getApplicationContext());
        recyclerView3.setLayoutManager(mLayoutManager3);
        recyclerView3.setItemAnimator(new DefaultItemAnimator());
        recyclerView3.setAdapter(mAdapter3);

        mAdapter4 = new MyRecyclerViewAdapter(movieList4);
        RecyclerView.LayoutManager mLayoutManager4 = new LinearLayoutManager(getApplicationContext());
        recyclerView4.setLayoutManager(mLayoutManager4);
        recyclerView4.setItemAnimator(new DefaultItemAnimator());
        recyclerView4.setAdapter(mAdapter4);

        setCheckA(false);
        setCheckB(false);
        setCheckC(false);
        setCheckD(false);

        checkHistory(pkgid);
    }

    public void verifyDetails() {

        final ProgressDialog progressDialog = new ProgressDialog(HistoryActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Verifying Details...");
        progressDialog.show();

        showchecked();
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {

                progressDialog.dismiss();
            }
        }, 3000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.buton2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.historycheck) {
            verifyDetails();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showchecked() {
            if (getA() == 1) {
                checkHash(stellarHash1, 1);
            }
            if (getB() == 1) {
                checkHash(stellarHash2, 2);
            }
            if (getC() == 1) {
                checkHash(stellarHash3, 3);
            }
            if (getD() == 1) {
                checkHash(stellarHash4,4);
            }
    }

    public void checkHash(String hash, final int id) {
        reqresponse = false;

        String tag_string_req = "string_req";

        String url = "https://horizon-testnet.stellar.org/transactions/" + hash + "/operations";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (id == 1) {
                    imgPoster.setBackgroundResource(0);
                    imgPoster.setImageDrawable(null);
                    imgPoster.setBackgroundResource(R.drawable.sw);
                } else if (id == 2) {
                    imgPoster2.setBackgroundResource(0);
                    imgPoster2.setImageDrawable(null);
                    imgPoster2.setBackgroundResource(R.drawable.sw);
                } else if (id == 3) {
                    imgPoster3.setBackgroundResource(0);
                    imgPoster3.setImageDrawable(null);
                    imgPoster3.setBackgroundResource(R.drawable.sw);
                } else if (id == 4) {
                    imgPoster4.setBackgroundResource(0);
                    imgPoster4.setImageDrawable(null);
                    imgPoster4.setBackgroundResource(R.drawable.sw);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());

                if (id == 1) {
                    imgPoster.setBackgroundResource(0);
                    imgPoster.setImageDrawable(null);
                    imgPoster.setBackgroundResource(R.drawable.i8);
                } else if (id == 2) {
                    imgPoster2.setBackgroundResource(0);
                    imgPoster2.setImageDrawable(null);
                    imgPoster2.setBackgroundResource(R.drawable.i8);
                } else if (id == 3) {
                    imgPoster3.setBackgroundResource(0);
                    imgPoster3.setImageDrawable(null);
                    imgPoster3.setBackgroundResource(R.drawable.i8);
                } else if (id == 4) {
                    imgPoster4.setBackgroundResource(0);
                    imgPoster4.setImageDrawable(null);
                    imgPoster4.setBackgroundResource(R.drawable.i8);
                }
            }
        });


        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


    public void checkHistory(String PackageID) {
        String tag_string_req = "string_req";

        String url = "http://34.69.253.149:8080/api/query/history/" + PackageID;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "Ticket response: " + response.toString());
                        String value = null;
                        try {
                            value = (String) response.get("response");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // check for success status
                        if (!response.has("error")) {

                            try {
                                System.out.println("Try1" + response);
                                JSONArray students = new JSONArray(value);
                                addData(students);


                            } catch (JSONException e) {
                                System.out.println("errrrror");
                                e.printStackTrace();
                            }
                        } else {
                            // no drugPackage found
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
            }
        });
// Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(jsonObjReq, tag_string_req);
    }


    public void addData(JSONArray students) throws JSONException {

        for (int i = 0; i < students.length(); i++) {
            JSONObject student = students.getJSONObject(i);
            System.out.println(student);
            String productid = student.getString("ProductID");
            String productname = student.getString("ProductName");
            String packageid = student.getString("PackageID");
            String expiredate = student.getString("ExpireDate");
            String quantity = student.getString("Quantity");
            String manudate = student.getString("ManufacturedDate");
            String timestamp = student.getString("Timestamp");
            String manufacturer = student.getString("Manufacturer");
            String ownerid = student.getString("OwnerID");
            String temparature = student.getString("Temparature");
            String owner = student.getString("Owner");
            String stellarhash = student.getString("StellarHash");

            if (i == 0) {
                MyListData movie = new MyListData(productid, "ProductID");
                movieList.add(movie);
                movie = new MyListData(productname, "ProductName");
                movieList.add(movie);
                movie = new MyListData(packageid, "PackageID");
                movieList.add(movie);
                movie = new MyListData(expiredate, "ExpireDate");
                movieList.add(movie);
                movie = new MyListData(quantity, "Quantity");
                movieList.add(movie);
                movie = new MyListData(manudate, "ManufacturedDate");
                movieList.add(movie);
                movie = new MyListData(timestamp, "Timestamp");
                movieList.add(movie);
                movie = new MyListData(manufacturer, "Manufacturer");
                movieList.add(movie);
                movie = new MyListData(stellarhash, "StellarHash");
                movieList.add(movie);
                movie = new MyListData(temparature, "Temparature");
                movieList.add(movie);
                movie = new MyListData(owner, "Owner");
                movieList.add(movie);
                movie = new MyListData(ownerid, "OwnerID");
                movieList.add(movie);

                setA(1);
                setStellarHash1(stellarhash);
            }
            if (i == 1) {
                MyListData movie = new MyListData(productid, "ProductID");
                movieList2.add(movie);
                movie = new MyListData(productname, "ProductName");
                movieList2.add(movie);
                movie = new MyListData(packageid, "PackageID");
                movieList2.add(movie);
                movie = new MyListData(expiredate, "ExpireDate");
                movieList2.add(movie);
                movie = new MyListData(quantity, "Quantity");
                movieList2.add(movie);
                movie = new MyListData(manudate, "ManufacturedDate");
                movieList2.add(movie);
                movie = new MyListData(timestamp, "Timestamp");
                movieList2.add(movie);
                movie = new MyListData(manufacturer, "Manufacturer");
                movieList2.add(movie);
                movie = new MyListData(stellarhash, "StellarHash");
                movieList2.add(movie);
                movie = new MyListData(temparature, "Temparature");
                movieList2.add(movie);
                movie = new MyListData(owner, "Owner");
                movieList2.add(movie);
                movie = new MyListData(ownerid, "OwnerID");
                movieList2.add(movie);

                setB(1);
             setStellarHash2(stellarhash);
            }
            if (i == 2) {

                MyListData movie = new MyListData(productid, "ProductID");
                movieList3.add(movie);
                movie = new MyListData(productname, "ProductName");
                movieList3.add(movie);
                movie = new MyListData(packageid, "PackageID");
                movieList3.add(movie);
                movie = new MyListData(expiredate, "ExpireDate");
                movieList3.add(movie);
                movie = new MyListData(quantity, "Quantity");
                movieList3.add(movie);
                movie = new MyListData(manudate, "ManufacturedDate");
                movieList3.add(movie);
                movie = new MyListData(timestamp, "Timestamp");
                movieList3.add(movie);
                movie = new MyListData(manufacturer, "Manufacturer");
                movieList3.add(movie);
                movie = new MyListData(stellarhash, "StellarHash");
                movieList3.add(movie);
                movie = new MyListData(temparature, "Temparature");
                movieList3.add(movie);
                movie = new MyListData(owner, "Owner");
                movieList3.add(movie);
                movie = new MyListData(ownerid, "OwnerID");
                movieList3.add(movie);

                setC(1);
setStellarHash3(stellarhash);
            }
            if (i == 3) {
                MyListData movie = new MyListData(productid, "ProductID");
                movieList4.add(movie);
                movie = new MyListData(productname, "ProductName");
                movieList4.add(movie);
                movie = new MyListData(packageid, "PackageID");
                movieList4.add(movie);
                movie = new MyListData(expiredate, "ExpireDate");
                movieList4.add(movie);
                movie = new MyListData(quantity, "Quantity");
                movieList4.add(movie);
                movie = new MyListData(manudate, "ManufacturedDate");
                movieList4.add(movie);
                movie = new MyListData(timestamp, "Timestamp");
                movieList4.add(movie);
                movie = new MyListData(manufacturer, "Manufacturer");
                movieList4.add(movie);
                movie = new MyListData(stellarhash, "StellarHash");
                movieList4.add(movie);
                movie = new MyListData(temparature, "Temparature");
                movieList4.add(movie);
                movie = new MyListData(owner, "Owner");
                movieList4.add(movie);
                movie = new MyListData(ownerid, "OwnerID");
                movieList4.add(movie);

                setD(1);
                setStellarHash4(stellarhash);
            }
        }

        mAdapter.notifyDataSetChanged();
        mAdapter3.notifyDataSetChanged();
        mAdapter2.notifyDataSetChanged();
        mAdapter4.notifyDataSetChanged();

        if (students.length() == 1) {
            h3.setVisibility(View.GONE);
            h3333.setVisibility(View.GONE);
            h1.setVisibility(View.VISIBLE);
            h1111.setVisibility(View.GONE);
            h2.setVisibility(View.GONE);
            h2222.setVisibility(View.GONE);
            h4.setVisibility(View.GONE);
        } else if (students.length() == 2) {
            h3.setVisibility(View.GONE);
            h3333.setVisibility(View.GONE);
            h1.setVisibility(View.VISIBLE);
            h1111.setVisibility(View.VISIBLE);
            h2.setVisibility(View.VISIBLE);
            h2222.setVisibility(View.GONE);
            h4.setVisibility(View.GONE);
        } else if (students.length() == 3) {

            h1.setVisibility(View.VISIBLE);
            h1111.setVisibility(View.VISIBLE);
            h2.setVisibility(View.VISIBLE);
            h2222.setVisibility(View.VISIBLE);
            h3.setVisibility(View.VISIBLE);
            h3333.setVisibility(View.GONE);
            h4.setVisibility(View.GONE);
        } else if (students.length() == 4) {

            h1.setVisibility(View.VISIBLE);
            h1111.setVisibility(View.VISIBLE);
            h2.setVisibility(View.VISIBLE);
            h2222.setVisibility(View.VISIBLE);
            h3.setVisibility(View.VISIBLE);
            h3333.setVisibility(View.VISIBLE);
            h4.setVisibility(View.VISIBLE);
        }
    }

}
