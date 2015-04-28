/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

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
