package sintef.android.gravity.wizard;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;
import android.view.Display;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.InjectView;
import sintef.android.gravity.R;

/**
 * Created by samyboy89 on 26/02/15.
 */
public class WizardNotification extends WizardTemplate {

    @InjectView(R.id.wizard_notification_explanation_text)      public TextView mExplanationText;
    @InjectView(R.id.wizard_notification_explanation_image)     public ImageView mExplanationImage;

    public static WizardNotification newInstance(int position) {
        return newInstance(WizardNotification.class, R.layout.wizard_notification, position);
    }

    @Override
    public void init() {

        Display display = getActivity().getWindowManager().getDefaultDisplay();

        // Get height and width of the image and height of the text line
        mExplanationImage.measure(display.getWidth(), display.getHeight());
        int height = mExplanationImage.getMeasuredHeight();
        int width = mExplanationImage.getMeasuredWidth();
        float textLineHeight = mExplanationText.getPaint().getTextSize();

        // Set the span according to the number of lines and width of the image
        int lines = (int)Math.round(height / textLineHeight);

        // For an html text you can use this line: SpannableStringBuilder ss = (SpannableStringBuilder)Html.fromHtml(text);
        SpannableString ss = new SpannableString(getString(R.string.wizard_notification_explanation_text));
        ss.setSpan(new MyLeadingMarginSpan2(lines, width), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mExplanationText.setText(ss);

        // Align the text with the image by removing the rule that the text is to the right of the image
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mExplanationText.getLayoutParams();
        int[]rules = params.getRules();
        rules[RelativeLayout.RIGHT_OF] = 0;

    }

    public class MyLeadingMarginSpan2 implements LeadingMarginSpan.LeadingMarginSpan2 {
        private int margin;
        private int lines;

        public MyLeadingMarginSpan2(int lines, int margin) {
            this.margin = margin;
            this.lines = lines;
        }

        @Override
        public int getLeadingMargin(boolean first) {
            return first ? margin : 0;
        }

        @Override
        public int getLeadingMarginLineCount() {
            return lines;
        }

        @Override
        public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
                                      int top, int baseline, int bottom, CharSequence text,
                                      int start, int end, boolean first, Layout layout) {}
    }
}
