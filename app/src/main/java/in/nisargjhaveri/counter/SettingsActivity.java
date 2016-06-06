package in.nisargjhaveri.counter;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    final public static String KEY_PREFERRED_LOCALE = "preferred_locale";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final DataStore storage = DataStore.getInstance();

        String lang = storage.meta.getString(KEY_PREFERRED_LOCALE, "");
        setLocale(lang);

        List<String> listLocales = new ArrayList<String>();
        listLocales.add("en");
        listLocales.add("gu");
        listLocales.add("hi");

        LinearLayout localeListView = (LinearLayout) findViewById(R.id.select_locale);
        for (String locale: listLocales) {
            View localeListItem = getLocaleItemView(locale);
            localeListView.addView(localeListItem);
        }
    }

    public void storeLocalePreference(String lang) {
        final DataStore storage = DataStore.getInstance();
        storage.meta.set(KEY_PREFERRED_LOCALE, lang);
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

    public View getLocaleItemView(final String lang) {
        View v = LayoutInflater.from(this).inflate(R.layout.locale_item, null, false);

        Locale current_locale = getResources().getConfiguration().locale;
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

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale(lang);
                storeLocalePreference(lang);
                refreshActivity();
            }
        });

        return v;
    }
}
