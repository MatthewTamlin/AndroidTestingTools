/*
 * Copyright 2016 Matthew Tamlin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.matthewtamlin.android_testing_tools.testing;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.matthewtamlin.android_testing_tools.library.harnesses.ControlsOverViewTestHarness;

import static android.widget.LinearLayout.VERTICAL;

/**
 * A test harness which displays three TextViews in a LinearLayout.
 */
public class ThreeTextViewActivity extends ControlsOverViewTestHarness {
	/**
	 * The text displayed in the text view returned by {@link #getTextView1()}.
	 */
	public static final String TEXT_1 = "text 1";

	/**
	 * The text displayed in the text view returned by {@link #getTextView2()}.
	 */
	public static final String TEXT_2 = "text 2";

	/**
	 * The text displayed in the text view returned by {@link #getTextView3()}.
	 */
	public static final String TEXT_3 = "text 3";

	/**
	 * Hosts the TextViews.
	 */
	private LinearLayout testView;

	/**
	 * Displays {@link #TEXT_1).
	 */
	private TextView textView1;

	/**
	 * Displays {@link #TEXT_2).
	 */
	private TextView textView2;

	/**
	 * Displays {@link #TEXT_3).
	 */
	private TextView textView3;

	@Override
	public Object getTestView() {
		if (testView == null) {
			testView = new LinearLayout(this);
			testView.setOrientation(VERTICAL);

			testView.addView(getTextView1());
			testView.addView(getTextView2());
			testView.addView(getTextView3());
		}

		return testView;
	}

	/**
	 * @return the TextView which displays {@link #TEXT_1}.
	 */
	public TextView getTextView1() {
		if (textView1 == null) {
			textView1 = new TextView(this);
			textView1.setText(TEXT_1);
		}

		return textView1;
	}

	/**
	 * @return the TextView which displays {@link #TEXT_2}.
	 */
	public TextView getTextView2() {
		if (textView2 == null) {
			textView2 = new TextView(this);
			textView2.setText(TEXT_2);
		}

		return textView2;
	}

	/**
	 * @return the TextView which displays {@link #TEXT_3}.
	 */
	public TextView getTextView3() {
		if (textView3 == null) {
			textView3 = new TextView(this);
			textView3.setText(TEXT_3);
		}

		return textView3;
	}
}