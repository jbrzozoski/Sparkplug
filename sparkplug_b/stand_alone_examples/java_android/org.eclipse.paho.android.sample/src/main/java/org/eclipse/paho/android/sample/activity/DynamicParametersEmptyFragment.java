package org.eclipse.paho.android.sample.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eclipse.paho.android.sample.R;

import static android.content.ContentValues.TAG;

public class DynamicParametersEmptyFragment extends Fragment {

    private View rootView = null;

    public DynamicParametersEmptyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dynamic_parameters_empty, container, false);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "DynamicParametersEmptyFragment - Removing all views to properly refresh");
        ((ViewGroup) rootView).removeAllViews();
    }
}
