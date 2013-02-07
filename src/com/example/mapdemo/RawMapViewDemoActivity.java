/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * This shows how to create a simple activity with a raw MapView and add a marker to it. This
 * requires forwarding all the important lifecycle methods onto MapView.
 */
public class RawMapViewDemoActivity extends android.support.v4.app.FragmentActivity implements LocationListener {
    private MapView mMapView;
    private GoogleMap mMap;
    private LocationManager mLocMan;
    private String provider;
    private Marker currentMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raw_mapview_demo);

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        try {
			MapsInitializer.initialize(this);
		} catch (GooglePlayServicesNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        setUpMapIfNeeded();

        // 位置情報を取得するリスナの処理
       mLocMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
       // ベストを探す。あまり意味ないかな…
       Criteria criteria = new Criteria();
       provider = mLocMan.getBestProvider(criteria, true);
       // 最後の座標位置を知っていたら、それを利用する予定。
       Location location = mLocMan.getLastKnownLocation(provider);
       Log.v("Location", "Provider::" + provider);
       if (location != null){
    	   Log.v("Location", "Provider" + provider);
       }
       else {
    	   Log.v("Location", "location is null");
       }
       // GPSが室内でうまく入らないので、ここではネットワークに変更
       mLocMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

    }

    @Override
    protected void onResume() {
    	Log.v("Location", "onResume");
    	super.onResume();
        mMapView.onResume();

        setUpMapIfNeeded();
        mLocMan.requestLocationUpdates(provider, 0, 0, this);
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((MapView) findViewById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
    	// 地図のマップ
    	// 地図にマーカーを打つ。およションとして、場所を指定する。0,0だと赤道のどこか
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        // 東京を表示させるために、カメラの位置をいどうする。
    	CameraPosition cp = new CameraPosition.Builder()
		.target(new LatLng(35.6, 139.6)) // ここが座標
		.zoom(12) // ズーム
		.build();
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp)); // そこへカメラの位置を移動する
		// 新しくマーカを作って、東京近辺にマーカを打つ
		currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(35.6, 139.6)).title("Marker"));
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
        mLocMan.removeUpdates(this);
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

	@Override
	// 人が移動した情報はここへ来るので、この中で問い合わせればいいはず。
	// まじめに作るとintentとか考えないといけないのは面倒？うーむ…
	public void onLocationChanged(Location location) {
		Log.v("Location", "onLocationChanged");
		// 位置情報が分かればここに来るよ
		// 半透明のメッセージを出す
		Toast.makeText(getBaseContext(), "移動先： " + location.toString(), Toast.LENGTH_LONG).show();
		// 位置情報を取得して地図を移動。前と同じ。
		CameraPosition cp = new CameraPosition.Builder()
		.target(new LatLng(location.getLatitude(), location.getLongitude()))
		.zoom(12)
		.build();
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
		// マーカの場所を変える
		currentMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));


	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Log.v("Location", "onProviderDisabled:" + provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Log.v("Location", "onProviderEnabled:" + provider);

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}

