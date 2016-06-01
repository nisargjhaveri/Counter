package in.nisargjhaveri.counter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class AddCounterFragment extends Fragment {

    public AddCounterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_counter, container, false);

        final TextView countView = (TextView) rootView.findViewById(R.id.section_add);
        countView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataStore storage = DataStore.getInstance();

                int counters = storage.meta.getInt(MainActivity.NUMBER_OF_COUNTERS, MainActivity.DEFAULT_NUMBER_OF_COUNTERS) + 1;

                storage.meta.set(MainActivity.NUMBER_OF_COUNTERS, counters);

                ((MainActivity) getActivity()).mSectionsPagerAdapter.addPage();
            }
        });

        final ImageButton settingsButton = (ImageButton) rootView.findViewById(R.id.app_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openSettings();
            }
        });

        return rootView;
    }

}
