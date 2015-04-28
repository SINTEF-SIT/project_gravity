package sintef.android.gravity;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectViews;

public class About extends Activity {

    @InjectViews({R.id.about_link_github, R.id.about_link_wiki, R.id.about_link_libraries})      TextView[] mLinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        ButterKnife.inject(this);

        for (TextView view : mLinks) {
            view.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
