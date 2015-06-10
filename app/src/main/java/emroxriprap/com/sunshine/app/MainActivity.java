package emroxriprap.com.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback{
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;
    private String mLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.weather_detail_container) != null){
            //detail container is present in view...must be tablet
            mTwoPane = true;
            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container,new DetailFragment(),
                                DETAILFRAGMENT_TAG).commit();
            }
        }else{
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        ForecastFragment forecastFragment = ((ForecastFragment)getSupportFragmentManager().
                         findFragmentById(R.id.fragment_forecast));
        forecastFragment.setmUseTodayLayout(!mTwoPane);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        //updat the location in our second pane using the fragmentmanager
        if (location != null && !location.equals(mLocation)){
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().
                    findFragmentById(R.id.fragment_forecast);
            if (ff != null){
                ff.onLocationChanged();
            }
            DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (df != null){
                df.onLocationChanged(location);
            }
            mLocation = location;
        }
    }
    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane){
            //in two-pane mode, show the detail view in this acivity by
            //adding or replacing the detail fragment using a fragmenttransaction
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI,contentUri);


            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container,fragment,DETAILFRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this,DetailActivity.class).setData(contentUri);
            startActivity(intent);
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
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_map){
            openPreferredLocationInMap();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String location = sharedPreferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_default_value));
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",location).build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }else{
            Log.d(LOG_TAG, "Couldn't call "+ location + ", no app");
        }
    }


}



