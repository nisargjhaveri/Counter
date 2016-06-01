package in.nisargjhaveri.counter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    public SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    public ViewPager mViewPager;

    public static final String NUMBER_OF_COUNTERS = "number_of_counters";
    public static final int DEFAULT_NUMBER_OF_COUNTERS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences storage = this.getPreferences(MODE_PRIVATE);

        DataStore dbstorage = DataStore.getInstance();
        int numberOfCounters = dbstorage.meta.getInt(NUMBER_OF_COUNTERS, DEFAULT_NUMBER_OF_COUNTERS);

        final SharedPreferences settings = this.getSharedPreferences(SettingsActivity.SHARED_PREFS, MODE_PRIVATE);
        String lang = settings.getString("preferred_locale", "");
        setLocale(lang);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), numberOfCounters);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getBaseContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public void refreshActivity() {
        Intent refresh = new Intent(this, this.getClass());
        startActivity(refresh);
        finish();
    }

    @Override
    public void onRestart() {
        super.onRestart();

        refreshActivity();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    protected void openSettings() {
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int count;
        private int baseId = 0;
        private int maxCount = 0;


        public SectionsPagerAdapter(FragmentManager fm, int c) {
            super(fm);
            count = c;
            updateMaxCount();
        }

        private void updateMaxCount() {
            if (count > maxCount) {
                maxCount = count;
            }
        }

        @Override
        public Fragment getItem(int position) {
            if (position == count) {
                return new AddCounterFragment();
            } else {
                return CounterFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            return count + 1;
        }

        public void addPage() {
            baseId++;
            count++;
            updateMaxCount();
            notifyDataSetChanged();
        }

        public void deletePage() {
            baseId += 2;
            count--;
            updateMaxCount();
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId * (maxCount + 1) + position;
        }
    }
}
