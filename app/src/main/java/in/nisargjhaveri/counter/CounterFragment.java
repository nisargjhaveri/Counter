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
import java.util.UUID;

public class CounterFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private String counter_id;
    private String label;
    private int bgcolor;
    private int count = 0;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_counter, container, false);

        int section_number = getArguments().getInt(ARG_SECTION_NUMBER);

        final SharedPreferences storage = getActivity().getPreferences(Context.MODE_PRIVATE);
        storage_editor = storage.edit();

        counter_id = storage.getString("counter_uuid_" + section_number, null);
        if (counter_id == null) {
            counter_id = UUID.randomUUID().toString();
            storage_editor.putString("counter_uuid_" + section_number, counter_id);
            storage_editor.apply();
        }

        Random rand = new Random();
        bgcolor = storage.getInt("counter_bgcolor_" + counter_id, -1);
        if (bgcolor == -1) {
            bgcolor = Color.rgb(
                    155 + rand.nextInt(100),
                    155 + rand.nextInt(100),
                    155 + rand.nextInt(100)
            );
            storage_editor.putInt("counter_bgcolor_" + counter_id, bgcolor);
            storage_editor.apply();
        }

        label = storage.getString("counter_label_" + counter_id, getResources().getString(R.string.default_label));
        count = storage.getInt("counter_count_" + counter_id, count);

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

        final TextView decreaseButton = (TextView) rootView.findViewById(R.id.decrement_counter);
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                countView.setText(String.valueOf(count));
                storage_editor.putInt("counter_count_" + counter_id, count);
                storage_editor.apply();
            }
        });

        final ImageButton resetButton = (ImageButton) rootView.findViewById(R.id.reset_counter);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                countView.setText(String.valueOf(count));
                storage_editor.putInt("counter_count_" + counter_id, count);
                storage_editor.apply();
            }
        });

        final ImageButton deleteButton = (ImageButton) rootView.findViewById(R.id.delete_counter);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataStore dbstorage = DataStore.getInstance();

                int counters = dbstorage.meta.getInt(MainActivity.NUMBER_OF_COUNTERS, MainActivity.DEFAULT_NUMBER_OF_COUNTERS) - 1;

                dbstorage.meta.set(MainActivity.NUMBER_OF_COUNTERS, counters);

                int current_position = ((MainActivity) getActivity()).mViewPager.getCurrentItem();

                for (int i = current_position + 1; i < ((MainActivity) getActivity()).mSectionsPagerAdapter.getCount(); i++) {
                    storage_editor.putString("counter_uuid_" + i, storage.getString("counter_uuid_" + (i + 1), null));
                }
                storage_editor.apply();

                ((MainActivity) getActivity()).mSectionsPagerAdapter.deletePage();
            }
        });

        return rootView;
    }
}
