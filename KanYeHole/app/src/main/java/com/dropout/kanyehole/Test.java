package com.dropout.kanyehole;

import android.test.AndroidTestCase;

/**
 * Created by Kush on 4/12/2015.
 */
public class Test extends AndroidTestCase {
    public static void main(String args[]) {
        getArc();
    }

    static protected void getArc() {
        Arc arc = null;
        arc.getInstance();

        assertNotNull(arc);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //  MenuActivity = getActivity();
        // mFirstTestText =
        //       (TextView) mFirstTestActivity
        //             .findViewById(R.id.my_first_test_text_view);
    }
}
