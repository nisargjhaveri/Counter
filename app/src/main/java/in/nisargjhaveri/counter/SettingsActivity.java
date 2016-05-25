package in.nisargjhaveri.counter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    final public static String SHARED_PREFS = "counter_shared_preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final SharedPreferences storage = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        String lang = storage.getString("preferred_locale", "");
        setLocale(lang);

        List<String> listLocales = new ArrayList<String>();
        listLocales.add("en");
        listLocales.add("gu");
        listLocales.add("hi");

        Locale current = getResources().getConfiguration().locale;

        ArrayAdapter adapter = new LocaleListAdapter(this, listLocales, current);

        ListView listView = (ListView) findViewById(R.id.select_locale);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String lang = parent.getAdapter().getItem(position).toString();
                setLocale(lang);
                storeLocalePreference(lang);
                refreshActivity();
            }
        });
    }

    public void storeLocalePreference(String lang) {
        final SharedPreferences storage = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor storage_editor = storage.edit();
        storage_editor.putString("preferred_locale", lang);
        storage_editor.apply();
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

    public static class LocaleListAdapter extends ArrayAdapter<String> {

        Locale current_locale;

        public LocaleListAdapter(Context context,List<String> objects, Locale c) {
            super(context, 0, objects);
            current_locale = c;
        }

        public View getView(int position, View v, ViewGroup parent) {
            String lang = getItem(position);

            if (v == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.locale_item, parent, false);
            }

            Locale l = new Locale(lang);

            TextView locale_native_name = (TextView) v.findViewById(R.id.locale_native_name);
            locale_native_name.setText(l.getDisplayName(l));

            TextView locale_name = (TextView) v.findViewById(R.id.locale_name);
            locale_name.setText(l.getDisplayName(current_locale));

            TextView locale_selected = (TextView) v.findViewById(R.id.locale_selected);
            if (l.getLanguage().equals(current_locale.getLanguage())) {
                locale_selected.setText("✓");
            } else {
                locale_selected.setText("");
            }

            return v;
        }
    }
}
