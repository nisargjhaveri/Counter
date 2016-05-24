package in.nisargjhaveri.counter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

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

    public static final String NUMBER_OF_COUNTERS = "counter_number_of_counters";
    public static final int DEFAULT_NUMBER_OF_COUNTERS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences storage = this.getPreferences(MODE_PRIVATE);
        int numberOfCounters = storage.getInt(NUMBER_OF_COUNTERS, DEFAULT_NUMBER_OF_COUNTERS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), numberOfCounters);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private String counter_id;
        private String label = "New label";
        private int count = 0;

        protected SharedPreferences.Editor storage_editor;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            int section_number = getArguments().getInt(ARG_SECTION_NUMBER);
            counter_id = String.valueOf(section_number);

            final SharedPreferences storage = getActivity().getPreferences(MODE_PRIVATE);
            storage_editor = storage.edit();

            label = storage.getString("counter_label_" + counter_id, label);
            count = storage.getInt("counter_count_" + counter_id, count);

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            });

            final TextView labelView = (TextView) rootView.findViewById(R.id.section_label);
            labelView.setText(label);
            labelView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    storage_editor.putString("counter_label_" + counter_id, labelView.getText().toString());
                    storage_editor.apply();
                }
            });

            final TextView countView = (TextView) rootView.findViewById(R.id.section_count);
            countView.setText(String.valueOf(count));
            countView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count++;
                    countView.setText(String.valueOf(count));
                    storage_editor.putInt("counter_count_" + counter_id, count);
                    storage_editor.apply();
                }
            });

            return rootView;
        }
    }

    public static class AddCounterFragment extends Fragment {

        public AddCounterFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_counter, container, false);

            final TextView countView = (TextView) rootView.findViewById(R.id.section_add);
            countView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final SharedPreferences storage = getActivity().getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor storage_editor = storage.edit();

                    int counters = storage.getInt(MainActivity.NUMBER_OF_COUNTERS, MainActivity.DEFAULT_NUMBER_OF_COUNTERS) + 1;

                    storage_editor.putInt(MainActivity.NUMBER_OF_COUNTERS, counters);
                    storage_editor.apply();

                    ((MainActivity) getActivity()).mSectionsPagerAdapter.addPage();
                }
            });

            return rootView;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int count;
        private int baseId = 0;

        public SectionsPagerAdapter(FragmentManager fm, int c) {
            super(fm);
            count = c;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("Hey!", String.valueOf(position));
            if (position == count) {
                return new AddCounterFragment();
            } else {
                return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            return count + 1;
        }

        public void addPage() {
            baseId++;
            count++;
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId * count + position;
        }

    }
}
