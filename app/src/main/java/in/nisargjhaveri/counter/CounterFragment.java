package in.nisargjhaveri.counter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;

public class CounterFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private String counter_id;
    private String label;
    private int bgcolor;
    private int count;

    protected SharedPreferences.Editor storage_editor;

    public CounterFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CounterFragment newInstance(int sectionNumber) {
        CounterFragment fragment = new CounterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_counter, container, false);

        int section_number = getArguments().getInt(ARG_SECTION_NUMBER);

        final DataStore storage = DataStore.getInstance();

        DataStore.Counter newCounter = storage.counters.getCounterAt(section_number - 1);

        if (newCounter == null) {
            Random rand = new Random();
            bgcolor = Color.rgb(
                    155 + rand.nextInt(100),
                    155 + rand.nextInt(100),
                    155 + rand.nextInt(100)
            );

            newCounter = storage.counters.createCounter(null, 0, bgcolor);
        }

        final DataStore.Counter counter = newCounter;

        bgcolor = counter.getBgColor();
        label = counter.getLabel(getResources().getString(R.string.default_label));
        count = counter.getCount();

        rootView.setBackgroundColor(bgcolor);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                counter.setLabel(labelView.getText().toString());
            }
        });

        final TextView countView = (TextView) rootView.findViewById(R.id.section_count);
        countView.setText(String.valueOf(count));
        countView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                countView.setText(String.valueOf(count));
                counter.setCount(count);
            }
        });

        final TextView decreaseButton = (TextView) rootView.findViewById(R.id.decrement_counter);
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                countView.setText(String.valueOf(count));
                counter.setCount(count);
            }
        });

        final ImageButton resetButton = (ImageButton) rootView.findViewById(R.id.reset_counter);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                countView.setText(String.valueOf(count));
                counter.setCount(count);
            }
        });

        final ImageButton deleteButton = (ImageButton) rootView.findViewById(R.id.delete_counter);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int counters = storage.meta.getInt(MainActivity.NUMBER_OF_COUNTERS, MainActivity.DEFAULT_NUMBER_OF_COUNTERS) - 1;
                storage.meta.set(MainActivity.NUMBER_OF_COUNTERS, counters);

                storage.counters.removeCounter(counter.getId());

                ((MainActivity) getActivity()).mSectionsPagerAdapter.deletePage();
            }
        });

        return rootView;
    }
}
