package cs102.mevzu06.bustagram2.Activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import cs102.mevzu06.bustagram2.Fragments.STARS;
import cs102.mevzu06.bustagram2.Fragments.WhereIsMyBus;
import cs102.mevzu06.bustagram2.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, STARS.OnFragmentInteractionListener {

    private Handler mHandler;
    LocationManager locationManager;
    LocationListener locationListener;
    NfcAdapter nfcAdapter;
    TextView tagContent;
    ToggleButton tb;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int notification_id;
    private RemoteViews remoteViews;
    private Context context;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Custom Notification
        context = this;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        remoteViews.setImageViewResource(R.id.notif_icon, R.drawable.ic_stat_directions_bus);
        remoteViews.setTextViewText(R.id.notif_text, "Are you at Kentpark?");

        Intent yes_button_intent = new Intent("yes_clicked");
        notification_id = (int) System.currentTimeMillis();
        yes_button_intent.putExtra("id", notification_id);
        Intent no_button_intent = new Intent("no_clicked");
        no_button_intent.putExtra("id", notification_id);

        PendingIntent p_yes_button_intent = PendingIntent.getBroadcast(context, 1, yes_button_intent, 0);
        PendingIntent p_no_button_intent = PendingIntent.getBroadcast(context, 2, no_button_intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.yesButton, p_yes_button_intent);
        remoteViews.setOnClickPendingIntent(R.id.noButton, p_no_button_intent);


        // Drawer
        mHandler = new Handler();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //NFC Things
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about_us) {
            startActivity(new Intent(this, AboutUs.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        String theTitle = "";

        if (id == R.id.nav_where_is_my_bus) {
            fragment = new WhereIsMyBus();
            theTitle = "Where Is My Bus?";
        } else if (id == R.id.nav_offline_schedule) {
            fragment = new STARS();
            theTitle = "STARS Schedules";
        } /*else if (id == R.id.nav_helpOption) {
            fragment = new HelpFragment();
            theTitle = "Help";
        } else if (id == R.id.nav_settingsOption) {
            fragment = new SettingsFragment();
            theTitle = "Settings";
        }*/

        final Fragment finalFragment = fragment;
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.content_frame, finalFragment);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        mHandler.post(mPendingRunnable);

        // An alternative way without the animation and the handler.
        /*if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }*/

        if (!theTitle.equals(""))
            setToolbarTitle(theTitle);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    protected void onResume() {
        super.onResume();
        enableForegroundDispatchSystem();
    }

    protected void onPause() {
        super.onPause();
        disableForegroundDispatchSystem();
    }

    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilter = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);

    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Intent notification_intent = new Intent(this, MainActivity.class);
            PendingIntent p_notification_intent = PendingIntent.getActivity(this,0,notification_intent,0);
            builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.id.notif_icon).setAutoCancel(true).setCustomBigContentView(remoteViews).setContentIntent(p_notification_intent);
            notificationManager.notify(notification_id,builder.build());
            Toast.makeText(this,"NFC LOL",Toast.LENGTH_LONG).show();
        }
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }
}

