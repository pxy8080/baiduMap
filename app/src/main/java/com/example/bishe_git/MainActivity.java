package com.example.bishe_git;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.synchronization.SyncCoordinateConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.baidu.mapapi.map.BaiduMap.MAP_TYPE_NORMAL;
import static com.baidu.mapapi.map.BaiduMap.MAP_TYPE_SATELLITE;

public class MainActivity extends AppCompatActivity implements MyFragment.MyListener, Fragment_jingdian.MyListener, Fragment_yanshi.MyListener {
    FragmentManager fragmentManager;
    FragmentTransaction ft;
    static MyFragment myFragment;
    static Fragment_jingdian fragment_jingdian;
    static Fragment_yanshi fragment_yanshi;
    static List_Fragment list_fragment;
    String db_city = null;
    String city;
    String json_city, json_img, json_tem;
    LatLng qidian_latLng = null, en_latLng = null;
    String json_name = "jingdian.json";
    String json_array;
    public MapView mMapView;
    public BaiduMap mBaiduMap;
    int id;
    int jishu = 0;
    int cishu = 0;
    int lukuang = 0;
    public LocationClient mLocationClient;
    public double Latitude = 0;//??????
    public double Longitude = 0;//??????
    public TextView textView_tianqi;
    public ImageView imageView_tianqi;
    ImageView imageView_lukuang;

    @SuppressLint("HandlerLeak")
    final Handler myHandler = new Handler() {
        @Override
        //??????handleMessage??????,??????msg???what????????????????????????????????????
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                textView_tianqi.setText(json_city + "  " + json_tem + "???");
                Bitmap bitmap = BitmapFactory.decodeStream(getClass().getResourceAsStream("/res/drawable/" + json_img + ".png"));
                imageView_tianqi.setImageBitmap(bitmap);
            } else if (msg.what == 0x1234) {
                //??????????????????
                mBaiduMap.setMapType(MAP_TYPE_NORMAL);
            } else if (msg.what == 0x1235) {
                //??????????????????
                mBaiduMap.setMapType(MAP_TYPE_SATELLITE);
            } else if (msg.what == 0x1236) {
                //???????????????
                mBaiduMap.setBaiduHeatMapEnabled(true);
            } else if (msg.what == 0x1237) {
                //???????????????
                mBaiduMap.setBaiduHeatMapEnabled(false);
            } else if (msg.what == 0x1238) {
                //???????????????
                Bitmap bitmap_lukuang = BitmapFactory.decodeStream(getClass().getResourceAsStream("/res/drawable/lukuang.png"));
                imageView_lukuang.setImageBitmap(bitmap_lukuang);
                mBaiduMap.setTrafficEnabled(true);
            } else if (msg.what == 0x1239) {
                //???????????????
                Bitmap bitmap_lukuang = BitmapFactory.decodeStream(getClass().getResourceAsStream("/res/drawable/lukuang_one.png"));
                imageView_lukuang.setImageBitmap(bitmap_lukuang);
                mBaiduMap.setTrafficEnabled(false);
            } else if (msg.what == 0x1240) {
                json_array = "zhongdananfan";
                //???????????????
                AddPoint.options = new ArrayList<>();
                //???????????????
                mBaiduMap.clear();
                //?????????
                addPoints();
                LatLng nanfang = new LatLng(23.638368, 113.685752);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(nanfang);
                mBaiduMap.setMapStatus(msu);
//                ft = fragmentManager.beginTransaction();
//                ft.show(list_fragment);
//                ft.commit();
            } else if (msg.what == 0x1241) {
                // ?????????????????????????????????????????????
                mMapView.setMapCustomStyleEnable(false);
            } else if (msg.what == 0x1242) {
                // ??????json????????????
                final String CUSTOM_FILE_NAME_GRAY = "custom_map_config_gray.json";
                // ??????json????????????
                String customStyleFilePath = getCustomStyleFilePath(MainActivity.this, CUSTOM_FILE_NAME_GRAY);
                // ?????????????????????????????????????????????????????????
                mMapView.setMapCustomStylePath(customStyleFilePath);
                // ?????????????????????????????????????????????
                mMapView.setMapCustomStyleEnable(true);
            } else if (msg.what == 0x1243) {
                // ??????json????????????
                final String CUSTOM_FILE_NAME_GRAY = "custom_map_config_cha.json";
                // ??????json????????????
                String customStyleFilePath = getCustomStyleFilePath(MainActivity.this, CUSTOM_FILE_NAME_GRAY);
                // ?????????????????????????????????????????????????????????
                mMapView.setMapCustomStylePath(customStyleFilePath);
                // ?????????????????????????????????????????????
                mMapView.setMapCustomStyleEnable(true);
            } else if (msg.what == 0x1244) {
                // ??????json????????????
                final String CUSTOM_FILE_NAME_GRAY = "custom_map_config_black.json";
                // ??????json????????????
                String customStyleFilePath = getCustomStyleFilePath(MainActivity.this, CUSTOM_FILE_NAME_GRAY);
                // ?????????????????????????????????????????????????????????
                mMapView.setMapCustomStylePath(customStyleFilePath);
                // ?????????????????????????????????????????????
                mMapView.setMapCustomStyleEnable(true);
            } else if (msg.what == 0x1245) {
                // ??????json????????????
                final String CUSTOM_FILE_NAME_GRAY = "custom_map_config_chuxing.json";
                // ??????json????????????
                String customStyleFilePath = getCustomStyleFilePath(MainActivity.this, CUSTOM_FILE_NAME_GRAY);
                // ?????????????????????????????????????????????????????????
                mMapView.setMapCustomStylePath(customStyleFilePath);
                // ?????????????????????????????????????????????
                mMapView.setMapCustomStyleEnable(true);
            } else if (msg.what == 0x1246) {
                // ??????json????????????
                final String CUSTOM_FILE_NAME_GRAY = "custom_map_config_wuliu.json";
                // ??????json????????????
                String customStyleFilePath = getCustomStyleFilePath(MainActivity.this, CUSTOM_FILE_NAME_GRAY);
                // ?????????????????????????????????????????????????????????
                mMapView.setMapCustomStylePath(customStyleFilePath);
                // ?????????????????????????????????????????????
                mMapView.setMapCustomStyleEnable(true);
            }else if (msg.what == 0x1247) {
                json_array = "huguangyan";
                //???????????????
                AddPoint.options = new ArrayList<>();
                //???????????????
                mBaiduMap.clear();
                //?????????
                addPoints();
                LatLng huguangyan = new LatLng(21.150821,  110.29179);
                MapStatusUpdate msu1 = MapStatusUpdateFactory.zoomTo(16.0f);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(huguangyan);
                mBaiduMap.setMapStatus(msu);
                mBaiduMap.setMapStatus(msu1);
            }else if (msg.what == 0x1248) {
                json_array = "baiyunshan";
                //???????????????
                AddPoint.options = new ArrayList<>();
                //???????????????
                mBaiduMap.clear();
                //?????????
                addPoints();
                LatLng huguangyan = new LatLng(23.191213,  113.306527);
                MapStatusUpdate msu1 = MapStatusUpdateFactory.zoomTo(15.0f);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(huguangyan);
                mBaiduMap.setMapStatus(msu);
                mBaiduMap.setMapStatus(msu1);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //?????????????????????????????????????????????
        init_Map();
        Init_Windows.init_WindowsTitle(MainActivity.this);
        setContentView(R.layout.activity_main);
        textView_tianqi = findViewById(R.id.textview_tianqi);
        imageView_tianqi = findViewById(R.id.image_tianqi);
        imageView_lukuang = findViewById(R.id.imageButton_lukuang);
        Bitmap bitmap_lukuang = BitmapFactory.decodeStream(getClass().getResourceAsStream("/res/drawable/lukuang_one.png"));
        imageView_lukuang.setImageBitmap(bitmap_lukuang);
        Bitmap bitmap = BitmapFactory.decodeStream(getClass().getResourceAsStream("/res/drawable/qing.png"));
        imageView_tianqi.setImageBitmap(bitmap);
        init_Map_init();
        init_search_address();
        init_search_route();
        //??????
        CheckPermission.checkPermission(MainActivity.this);
        init_fragment();
        init_jingdian();
    }

    /**
     * ??????json??????
     */
    private String getCustomStyleFilePath(Context context, String customStyleFileName) {
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        String parentPath = null;
        try {
            inputStream = context.getAssets().open("customConfigdir/" + customStyleFileName);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            parentPath = context.getFilesDir().getAbsolutePath();
            File customStyleFile = new File(parentPath + "/" + customStyleFileName);
            if (customStyleFile.exists()) {
                customStyleFile.delete();
            }
            customStyleFile.createNewFile();

            outputStream = new FileOutputStream(customStyleFile);
            outputStream.write(buffer);
        } catch (IOException e) {
            Log.e("CustomMapDemo", "Copy custom style file failed", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e("CustomMapDemo", "Close stream failed", e);
                return null;
            }
        }
        return parentPath + "/" + customStyleFileName;
    }

    //???????????????
    private void init_jingdian() {
        Intent intent = getIntent();
        int jingdian_id = intent.getIntExtra("jingdian_id", -1);
        Log.d("liu", String.valueOf(jingdian_id));
        if (jingdian_id > -1) {
            String jingdian_name = intent.getStringExtra("jingdian");
            Log.d("liu", jingdian_name);
            if (jingdian_name.equals("zhongdananfan")) {
                myHandler.sendEmptyMessage(0x1240);
            } else if (jingdian_name.equals("huguangyan")) {
                //do something
                myHandler.sendEmptyMessage(0x1247);
            } else if (jingdian_name.equals("baiyunshan")) {
                //do something
                myHandler.sendEmptyMessage(0x1248);
            }
        }
    }

    @Override
    public void sendValue(String value) {
        if (value.equals("??????")) {
            myHandler.sendEmptyMessage(0x1234);
        } else if (value.equals("??????")) {
            myHandler.sendEmptyMessage(0x1235);
        } else if (value.equals("?????????")) {
            myHandler.sendEmptyMessage(0x1236);
        } else if (value.equals("?????????")) {
            myHandler.sendEmptyMessage(0x1237);
        }
    }

    //????????????
    @Override
    public void sendValue_jingdian(String value) {
        if (value.equals("????????????")) {
            myHandler.sendEmptyMessage(0x1240);
        }else if (value.equals("?????????")){
            myHandler.sendEmptyMessage(0x1247);
        }else if (value.equals("?????????")){
            myHandler.sendEmptyMessage(0x1248);
        }
    }

    //???????????????
    @Override
    public void sendValue_yanshi(String value) {
        if (value.equals("??????")) {
            myHandler.sendEmptyMessage(0x1241);
        } else if (value.equals("??????")) {
//            Message message = new Message();
//            message.what=11111;
//            message.obj=333;
//            myHandler.sendMessage(message);
            myHandler.sendEmptyMessage(0x1242);
        } else if (value.equals("??????")) {
            myHandler.sendEmptyMessage(0x1243);

        } else if (value.equals("??????")) {
            myHandler.sendEmptyMessage(0x1244);
        } else if (value.equals("??????")) {
            myHandler.sendEmptyMessage(0x1245);
        } else if (value.equals("??????")) {
            myHandler.sendEmptyMessage(0x1246);
        }
    }

    //??????
    public void lukuang(View view) {
        lukuang++;
        if (lukuang % 2 == 1) {
            myHandler.sendEmptyMessage(0x1238);
        } else {
            myHandler.sendEmptyMessage(0x1239);
        }
    }

    //???????????????
    private void init_tianqi() {
        //??????????????????
        if (db_city != null && !(db_city.equals(city + "???"))) {
            city = db_city.substring(0, db_city.length() - 1);
            final HttpUtils httpUtils = new HttpUtils();
            httpUtils.SendGetRequest(city);
            final Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                public void run() {
                    cishu++;
                    if (httpUtils.success) {
                        JSONObject json_tianqi = httpUtils.json_tianqi;
                        try {
                            json_city = json_tianqi.getString("city");
                            json_img = json_tianqi.getString("wea_img");
                            json_tem = json_tianqi.getString("tem");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        myHandler.sendEmptyMessage(0x123);
                        timer.cancel();
                        cishu = 0;
                    }
                    if (cishu > 50) {
                        timer.cancel();
                    }
//timer.cancel(); ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? run ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                }
            };
            timer.schedule(task, 50, 50); //??????????????????0.05??????????????????TimerTask????????????????????????????????????????????????0.05?????????
        }
    }

    private void init_fragment() {
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        myFragment = new MyFragment();
        fragment_jingdian = new Fragment_jingdian();
        fragment_yanshi = new Fragment_yanshi();
        list_fragment=new List_Fragment();
        ft.add(R.id.main,list_fragment);
        ft.add(R.id.main, myFragment);
        ft.add(R.id.main, fragment_jingdian);
        ft.add(R.id.main, fragment_yanshi);
        ft.hide(myFragment);
        ft.hide(fragment_jingdian);
        ft.hide(fragment_yanshi);
        ft.hide(list_fragment);
        ft.commit();
    }


    public void tuceng(View view) {
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        ft.show(myFragment);
        ft.commit();
    }

    public void jingdian(View view) {
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        ft.show(fragment_jingdian);
        ft.commit();
    }

    public void yanshi(View view) {
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        ft.show(fragment_yanshi);
        ft.commit();
    }

    private void init_search_route() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("daohan", 0);
        if (id > 0) {
            if (SearchRoute_Activity.latLngs.size() > 1) {
                qidian_latLng = SearchRoute_Activity.latLngs.get(1);
                en_latLng = SearchRoute_Activity.latLngs.get(0);
                Route route = new Route();
                route.Route(mBaiduMap, id, qidian_latLng, en_latLng);
                //     ????????????
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(qidian_latLng);
                mBaiduMap.setMapStatus(msu);
            }
        }
    }

    private void init_search_address() {
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        double latitude_address = intent.getDoubleExtra("latitude_address", -1);//??????
        double longitude_address = intent.getDoubleExtra("longitude_address", -1);//??????
        if (id > -1) {
            LatLng latLng = new LatLng(latitude_address, longitude_address);
            mBaiduMap.addOverlay(AddPoint.addLatLng(latLng, -2));
//                mBaiduMap.setMapType(MapType);
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.setMapStatus(msu);
        }
    }

    public void tianqi(View view) {
        //??????TianQi_Activity
        Intent intent = new Intent(MainActivity.this, TianQi_Activity.class);
        intent.putExtra("city", city);
        startActivity(intent);
    }

    private void init_Map_init() {
        // ???????????????
        mMapView = findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
        mBaiduMap.setMapStatus(msu);

        //        ????????????logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        //??????markers?????????
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = marker.getExtraInfo();
                int id = bundle.getInt("id");
                if (id > -1) {
                    Intent JingDian = new Intent(MainActivity.this, Jingdian_Activity.class);
                    JingDian.putExtra("id", id);
                    JingDian.putExtra("json_name", json_name);
                    JingDian.putExtra("json_array", json_array);
                    startActivity(JingDian);
                }
//                Toast.makeText(MainActivity.this, "Marker:" + id, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        //?????????????????????????????????
        mMapView.getMap().setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Windows_size windows_size = new Windows_size();
                int width = windows_size.windows_width * 35 / 40;
                int heiht = windows_size.windows_height * 5 / 9;
                mMapView.setZoomControlsPosition(new Point(width, heiht));
            }
        });
    }

    private void init_Map() {
        mLocationClient = new LocationClient((getApplicationContext()));
        mLocationClient.registerNotifyLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    public void search(View view) {
        //??????SearchAddress_Activity??????????????????
        Intent intent = new Intent(MainActivity.this, SearchAddress_Activity.class);
        startActivity(intent);
    }

    public void dingwei(View view) {
        requestLocation();
    }

    private void requestLocation() {
        mLocationClient.start();
        initLocation();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //??????????????????
        option.setCoorType("BD09LL");
        option.setScanSpan(500);
        //??????????????????????????????????????????????????????
        option.setIsNeedAddress(true);
        //??????????????????????????????????????????????????????
        option.setIsNeedLocationDescribe(true);
//      ??????????????????gps????????????
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
        initView();
    }

    private void initView() {
        mBaiduMap.clear();
        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                if (Latitude != 0 && Longitude != 0) {
                    //        ?????????
                    LatLng latLng = new LatLng(Latitude, Longitude);
                    // ???GPS?????????????????????GPS???????????????????????????
                    SyncCoordinateConverter converter = new SyncCoordinateConverter();
                    converter.from(SyncCoordinateConverter.CoordType.COMMON);
                    // sourceLatLng???????????????
                    converter.coord(latLng);
                    LatLng desLatLng = converter.convert();
                    //??????OverlayOptions???????????????????????????Marker
                    AddPoint.addLatLng(desLatLng, -1);
                    mBaiduMap.addOverlay(AddPoint.addLatLng(desLatLng, -1));
                    //     ????????????
                    MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(desLatLng);
                    mBaiduMap.setMapStatus(msu);
                    init_tianqi();
//        ???????????????????????????
//        mBaiduMap.setMapType(MapType);
                    timer.cancel();
                }
//timer.cancel(); ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? run ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            }
        };
        timer.schedule(task, 100, 50); //??????????????????0.05??????????????????TimerTask????????????????????????????????????????????????0.05?????????
    }

    //??????
    public void addPoints() {
        AddJson addJson = new AddJson(json_name, json_array);
        AddPoint addPoint = new AddPoint();
        //????????????????????????
        mBaiduMap.addOverlays(addPoint.options);
    }

    public void luxian(View view) {
        Intent intent = new Intent(MainActivity.this, SearchRoute_Activity.class);
        startActivity(intent);
    }


    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Latitude = bdLocation.getLatitude();
            Longitude = bdLocation.getLongitude();
            db_city = bdLocation.getDistrict();
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
//                Toast.makeText(MainActivity.this, "?????????GPS??????", Toast.LENGTH_SHORT).show();
            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
//                Toast.makeText(MainActivity.this, "??????????????????????????????GPS", Toast.LENGTH_SHORT).show();
            } else {
                //??????????????????????????????????????????????????????????????????????????????BDLocation???????????????
                int errorCode = bdLocation.getLocType();

                if (errorCode == 66 || errorCode == 67) {
                    Toast.makeText(MainActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                } else if (errorCode == 62) {
                    Toast.makeText(MainActivity.this, "??????????????????,???????????????????????????", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "?????????" + errorCode, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }
}
