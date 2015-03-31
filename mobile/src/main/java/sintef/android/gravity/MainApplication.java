package sintef.android.gravity;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

/**
 * Created by samyboy89 on 30/03/15.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Locale locale = getResources().getConfiguration().locale;
        if (locale.getLanguage().equals("no") || locale.getLanguage().equals("nb") || locale.getLanguage().equals("nn")){
            locale = new Locale("no","NO");
            Configuration config = new Configuration();
            config.locale = locale;
            Resources res = getBaseContext().getResources();
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
    }
}
