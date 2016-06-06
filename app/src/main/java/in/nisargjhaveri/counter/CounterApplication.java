package in.nisargjhaveri.counter;

import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CounterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize DataStore
        DataStore.init(getApplicationContext());

        // Set Fonts
        setDefaultFonts();
    }

    private void setDefaultFonts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        // Works with Android API 21 to 23
        // Not sure after that as it uses hidden APIs that might get removed or changed in future
        Typeface normal = getTypefaceFromAssets(getAssets(), new String[]{"fonts/NotoSansDevanagari-Regular.ttf", "fonts/NotoSansGujarati-Regular.ttf"});
        Typeface bold = getTypefaceFromAssets(getAssets(), new String[]{"fonts/NotoSansDevanagari-Bold.ttf", "fonts/NotoSansGujarati-Bold.ttf"});
        Typeface italic = getTypefaceFromAssets(getAssets(), new String[]{});
        Typeface boldItalic = getTypefaceFromAssets(getAssets(), new String[]{});

        try {
            Method setDefaultTypeface = Typeface.class.getDeclaredMethod("setDefault", Typeface.class);
            setDefaultTypeface.setAccessible(true);
            setDefaultTypeface.invoke(null, normal);

            Field defaultField = Typeface.class.getDeclaredField("DEFAULT");
            defaultField.setAccessible(true);
            defaultField.set(null, normal);

            Field defaultBoldField = Typeface.class.getDeclaredField("DEFAULT_BOLD");
            defaultBoldField.setAccessible(true);
            defaultBoldField.set(null, bold);

            Field sDefaults = Typeface.class.getDeclaredField("sDefaults");
            sDefaults.setAccessible(true);
            sDefaults.set(null, new Typeface[]{normal, bold, italic, boldItalic});

            Field sDefaultTypeface = Typeface.class.getDeclaredField("sDefaultTypeface");
            sDefaultTypeface.setAccessible(true);
            sDefaultTypeface.set(null, normal);

            Field sansSerifDefaultField = Typeface.class.getDeclaredField("SANS_SERIF");
            sansSerifDefaultField.setAccessible(true);
            sansSerifDefaultField.set(null, normal);

            Map<String, Typeface> newMap = new HashMap<String, Typeface>();
            newMap.put("sans-serif", normal);
            final Field staticField = Typeface.class.getDeclaredField("sSystemFontMap");
            staticField.setAccessible(true);
            staticField.set(null, newMap);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static Typeface getTypefaceFromAssets(AssetManager mgr, String[] fontFileList) {
        Class FontFamily = null;
        Method addFontFromAsset;

        try {
            FontFamily = Class.forName("android.graphics.FontFamily");
            addFontFromAsset = FontFamily.getDeclaredMethod("addFontFromAsset", AssetManager.class, String.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }

        Object families = Array.newInstance(FontFamily, fontFileList.length);

        Object newFontFamily = null;
        for (int i = 0; i < fontFileList.length; i++) {
            try {
                newFontFamily = FontFamily.newInstance();
                addFontFromAsset.invoke(newFontFamily, mgr, fontFileList[i]);
                Array.set(families, i, newFontFamily);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        Method createFromFamiliesWithDefault = null;
        Typeface tf = null;
        try {
            createFromFamiliesWithDefault = Typeface.class.getDeclaredMethod("createFromFamiliesWithDefault", Class.forName("[Landroid.graphics.FontFamily;"));
            tf = (Typeface) createFromFamiliesWithDefault.invoke(null, families);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return tf;
    }

}
