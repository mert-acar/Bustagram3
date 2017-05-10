package cs102.mevzu06.bustagram2.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ScrollingView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import cs102.mevzu06.bustagram2.Activities.MainActivity;
import cs102.mevzu06.bustagram2.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;

import static android.content.Context.LOCATION_SERVICE;


/**
 * Created by Mert Acar on 7.05.2017.
 */

public class WhereIsMyBus extends Fragment implements OnMapReadyCallback
{
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    ListView listView;
    public WhereIsMyBus() {}
    LocationManager locationManager;
    LocationListener locationListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map, container,false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) mView.findViewById (R.id.map);
        if (mMapView != null)
            mMapView.onCreate(null);
            mMapView.onResume();;
            mMapView.getMapAsync(this);

        listView = (ListView) mView.findViewById(R.id.listOnline);
        final String[] listItems = {"TMD", "SMD", "Ring"};

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, Arrays.asList(listItems));
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "You've clicked: " + listItems[position], Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) mView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setVisibility(View.VISIBLE);
            }
        });
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                /**
                 * BURAYA DATABASE e YAZMA METODU GİDECEK
                 */
                ((MainActivity)getActivity()).createNotification("You are at: " + location.getLatitude() + ", " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void trackUsers() {
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET}, 27);
            return;
        }

        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker (new MarkerOptions().position(new LatLng(39.917563,32.823518)).title("Bahçeli")).setVisible(true);
        googleMap.addMarker (new MarkerOptions().position(new LatLng(39.909977,32.857625)).title("Tunus")).setVisible(true);
        googleMap.addMarker (new MarkerOptions().position(new LatLng(39.908574,32.776625)).title("Kentpark")).setVisible(true);
        googleMap.addMarker (new MarkerOptions().position(new LatLng(39.911822,32.809670)).title("Armada")).setVisible(true);
        CameraPosition bilkent = CameraPosition.builder().target(new LatLng(39.915721, 32.841662)).zoom(13).bearing(0).build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(bilkent));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                listView.setVisibility(View.GONE);
            }
        });
    }


}
