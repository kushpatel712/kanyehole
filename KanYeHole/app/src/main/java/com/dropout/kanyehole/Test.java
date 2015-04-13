package com.dropout.kanyehole;

import android.test.AndroidTestCase;

/**
 * Created by Kush on 4/12/2015.
 */
public class Test extends AndroidTestCase{
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MenuActivity = getActivity();
        mFirstTestText =
                (TextView) mFirstTestActivity
                        .findViewById(R.id.my_first_test_text_view);
    }
}
