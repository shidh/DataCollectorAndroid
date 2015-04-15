package de.mpg.mpdl.www.datacollector.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by allen on 15/04/15.
 */

/**
 * A dummy fragment representing a section of the app, but that simply displays dummy text.
 */
public class CollectionFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_collection, container, false);
            Bundle args = getArguments();
            System.out.println("text View ID: "+android.R.id.text1);
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    getString(R.string.dummy_section_text, args.getInt(ARG_SECTION_NUMBER)));

            return rootView;
        }
}

