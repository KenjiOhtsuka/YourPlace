package com.improve_future.yourplace.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.improve_future.yourplace.Fragment.DecideDestinationFragment;
import com.improve_future.yourplace.Fragment.DestinationDetailFragment;
import com.improve_future.yourplace.Library.MyUncaughtExceptionHandler;
import com.improve_future.yourplace.NavigationDrawerFragment;
import com.improve_future.yourplace.R;
import com.improve_future.yourplace.Utility;
import com.improve_future.yourplace.common.Constant;
import com.improve_future.yourplace.common.Model.Spot;
import com.improve_future.yourplace.common.Model.Station;

import java.util.ArrayList;
import java.util.Locale;


public class TopActivity extends Activity implements ActionBar.TabListener,
//        CheckFeelingFragment.OnFragmentInteractionListener,
        DecideDestinationFragment.OnFragmentInteractionListener,
        DestinationDetailFragment.OnFragmentInteractionListener {
//extends ActionBarActivity
        //implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private static ProgressDialog progressDialog;
    private DecideDestinationFragment decideDestinationFragment;
    private DestinationDetailFragment destinationDetailFragment;
    private GoogleApiClient mApiClient;
//    private CheckFeelingFragment checkFeelingFragment;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;


    /**
     * Used to store the last screen title. For use in {@link #//restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(this));
        setContentView(R.layout.activity_top);
        // set progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        mNavigationDrawerFragment = (NavigationDrawerFragment)
//                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
//        // Set up the drawer.
//        mNavigationDrawerFragment.setUp(
//                R.id.navigation_drawer,
//                (DrawerLayout) findViewById(R.id.drawer_layout));
        if (PreferenceManager.getDefaultSharedPreferences(this).
                getBoolean("report_bugs_automatically", true)) {
            MyUncaughtExceptionHandler.showBugReportDialogIfExist();
        }
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final ActionBar actionBar = getActionBar();
            if (actionBar.getSelectedNavigationIndex() == 1) {
                actionBar.setSelectedNavigationItem(0);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        sendMessage("/test", "");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();
        mApiClient.connect();
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
        final Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_manual:
                intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.manual_url)));
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    private void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes()).await();
                }

                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        }).start();
    }

    @Override
    public void sendToWear(final Station station, final ArrayList<Spot> spots) {
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // generate data to send
                                PutDataMapRequest putDataMapRequest =
                                        PutDataMapRequest.create(Constant.DESTINATION_PATH);
                                final DataMap stationDataMap = new DataMap();
                                stationDataMap.putString("name", station.getName());
                                stationDataMap.putString("code", station.getCode());
                                stationDataMap.putString("lineCodeName", station.getLineCodeName());
                                stationDataMap.putDouble("latitude", station.getLatitude());
                                stationDataMap.putDouble("longitude", station.getLongitude());
                                stationDataMap.putString("codeName", station.getCodeName());

                                DataMap dataMap = putDataMapRequest.getDataMap();
                                dataMap.putDataMap("station", stationDataMap);

                                ArrayList<DataMap> spotDataMapArrayList = new ArrayList<DataMap>();
                                for (Spot spot : spots) {
                                    final DataMap spotDataMap = new DataMap();
                                    spotDataMap.putString("name", spot.getName());
                                    spotDataMap.putDouble("latitude", spot.getLatitude());
                                    spotDataMap.putDouble("longitude", spot.getLongitude());
                                    spotDataMapArrayList.add(spotDataMap);
                                }

                                dataMap.putDataMapArrayList("spots", spotDataMapArrayList);

                                DataApi.DataItemResult result = Wearable.DataApi.putDataItem(
                                        mApiClient, putDataMapRequest.asPutDataRequest())
                                        .await();
                            }
                        }).start();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();
        mApiClient.connect();
    }

    @Override
    public void showInputForm() {

    }

//    @Override
//    public void onNavigationDrawerItemSelected(int position) {
//        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
//    }

//    public void onSectionAttached(int number) {
//        switch (number) {
//            case 1:
//                mTitle = getString(R.string.title_section1);
//                break;
//            case 2:
//                mTitle = getString(R.string.title_section2);
//                break;
//            case 3:
//                mTitle = getString(R.string.title_section3);
//                break;
//        }
//    }

//    public void restoreActionBar() {
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(mTitle);
//    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.top, menu);
//            restoreActionBar();
//            return true;
//        }
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch(position) {
                case 0:
                    if (decideDestinationFragment == null) {
                        decideDestinationFragment = DecideDestinationFragment.newInstance();
                    }
                    return decideDestinationFragment;
//                case 1:
//                    if (checkFeelingFragment == null) {
//                        checkFeelingFragment = CheckFeelingFragment.newInstance();
//                    }
//                    return checkFeelingFragment;
//                case 2:
                case 1:
                    if (destinationDetailFragment == null) {
                        destinationDetailFragment = DestinationDetailFragment.newInstance();
                    }
                    return destinationDetailFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
//            return 3;
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.where).toUpperCase(l);
//                case 1:
//                    return getString(R.string.feeling).toUpperCase(l);
//                case 2:
                case 1:
                    return getString(R.string.there).toUpperCase(l);
            }
            return null;
        }
    }

    public void setDestination(final String stationCodeName) {
//        getActionBar().setSelectedNavigationItem(2);
        getActionBar().setSelectedNavigationItem(1);
//        DestinationDetailFragment destinationDetailFragment =
//                (DestinationDetailFragment)(getFragmentManager().findFragmentByTag
//                        ("fragment_destination_detail"));
//        if (destinationDetailFragment != null) {
        destinationDetailFragment.setDestination(stationCodeName);
//        }
        //initGoogleApiClient();
    }

    /**
     * Show progress dialog
     * @param titleId       Title of progress dialog
     * @param messageId     Message of progress dialog
     * @param cancelable    Cancelable of progress dialog
     * @param iconId
     */
    public void showProgressDialog(
            final int titleId, final int messageId, final Boolean cancelable, final int iconId) {
        //this.progressDialog.setIcon(iconId);
        this.progressDialog.setTitle(titleId);
        this.progressDialog.setMessage(getString(messageId));
        this.progressDialog.setCancelable(cancelable);
        this.progressDialog.show();
    }

    /**
     * Show progress dialog
     * @param titleId       Title of progress dialog
     * @param messageId     Message of progress dialog
     * @param cancelable    Cancelable of progress dialog
     */
    public void showProgressDialog(
            final int titleId, final int messageId, final Boolean cancelable) {
        this.showProgressDialog(titleId, messageId, cancelable, 0);
    }

    /**
     * Dismiss progress dialog
     */
    public void dismissProgressDialog() {
        if (this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    /**
     * Show Simple Alert Dialog which has one button showing "OK"
     * @param titleId
     * @param messageId
     */
    public void showSimpleAlert(final int titleId, final int messageId) {
        this.showSimpleAlert(titleId, messageId, 0);
    }

    public void showSimpleAlert(final int titleId, final int messageId, final int icon) {
        Utility.showSimpleAlert(this, titleId, messageId, icon);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mApiClient != null) {
            mApiClient.disconnect();
        }
    }
}
