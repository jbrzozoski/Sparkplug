package org.eclipse.paho.android.sample.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cirruslink.sparkplug.message.model.Metric;
import com.cirruslink.sparkplug.message.model.MetricDataType;
import com.cirruslink.sparkplug.message.model.SparkplugBPayload.SparkplugBPayloadBuilder;
import com.google.zxing.integration.android.IntentIntegrator;

import org.eclipse.paho.android.sample.R;
import org.eclipse.paho.android.sample.components.CustomEditText;
import org.eclipse.paho.android.sample.internal.Connections;

import java.util.Date;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class TutorialFragment extends Fragment implements View.OnClickListener {

    private Connection connection;

    private CustomEditText emulatedKeyEditText;
    private Button scanButton;
    private Button emulateScanButton;
    private TextView formatText;
    private TextView contentText;

    private View rootView = null;

    public TutorialFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Map<String, Connection> connections = Connections.getInstance(this.getActivity())
                .getConnections();
        connection = connections.get(this.getArguments().getString(ActivityConstants.CONNECTION_KEY));

        Log.d(TAG, "FRAGMENT CONNECTION: " + this.getArguments().getString(ActivityConstants.CONNECTION_KEY));
        Log.d(TAG, "NAME:" + connection.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_tutorial, container, false);

        emulatedKeyEditText = (CustomEditText) rootView.findViewById(R.id.emulatedKey);
        scanButton = (Button) rootView.findViewById(R.id.scan_button);
        emulateScanButton = (Button) rootView.findViewById(R.id.emulate_scan_button);

        // Handle the emulated text field
        emulatedKeyEditText.setText(((String)connection.getSparkplugMetrics().get("Scan Code").getValue()).toString());
        emulatedKeyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "STRING CHANGE!!! " + s.toString());

                emulateScanButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

                // Replace the dynamic content with an empty fragment
                Log.d(TAG, "Replacing Fragment with 'Empty'");
                DynamicParametersEmptyFragment newFragment = new DynamicParametersEmptyFragment();

                FragmentTransaction transaction = ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.dynamic_parameters_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        // Set up the 'real' scan button
        scanButton.setOnClickListener(this);

        // Set up the 'emulated' scan button
        emulateScanButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        emulateScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (connection.getSeqLock()) {
                    try {
                        if (emulatedKeyEditText.getText() != null && !emulatedKeyEditText.getText().toString().trim().isEmpty()) {
                            SparkplugBPayloadBuilder payloadBuilder = new SparkplugBPayloadBuilder()
                                    .setTimestamp(new Date())
                                    .setSeq(connection.getSeqNum());

                            Metric scanCodeMetric = new Metric.MetricBuilder("Scan Code", MetricDataType.String, emulatedKeyEditText.getText().toString()).createMetric();
                            payloadBuilder.addMetric(scanCodeMetric);
                            connection.getSparkplugMetrics().put("Scan Code", scanCodeMetric);

                            String topic = "spBv1.0/" + connection.getGroupId() + "/NDATA/" + connection.getEdgeNodeId();
                            ((MainActivity) getActivity()).publish(connection, topic, payloadBuilder.createPayload(), 0, false);

                            // Reset the hashmap and button color
                            Log.d(TAG, "Resetting the publisher");
                            emulateScanButton.getBackground().clearColorFilter();
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "Exception publishing ", e);
                    }
                }
            }
        });

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (rootView.findViewById(R.id.dynamic_parameters_container) != null) {

            /*
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }*/

            // Create a new Fragment to be placed in the activity layout
            DynamicParametersEmptyFragment dynamicParametersEmptyFragment = new DynamicParametersEmptyFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            dynamicParametersEmptyFragment.setArguments(((MainActivity) getActivity()).getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction()
                    .add(R.id.dynamic_parameters_container, dynamicParametersEmptyFragment).commit();
        } else {
            Log.d(TAG, "No dynamic fragment");
        }


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Removing all views to properly refresh");
        ((ViewGroup) rootView).removeAllViews();

        // Clear the current emulated key
        synchronized (connection.getSeqLock()) {
            try {
                if (emulatedKeyEditText.getText() != null && !emulatedKeyEditText.getText().toString().trim().isEmpty()) {
                    SparkplugBPayloadBuilder payloadBuilder = new SparkplugBPayloadBuilder()
                            .setTimestamp(new Date())
                            .setSeq(connection.getSeqNum());

                    Metric scanCodeMetric = new Metric.MetricBuilder("Scan Code", MetricDataType.String, "").createMetric();
                    payloadBuilder.addMetric(scanCodeMetric);
                    connection.getSparkplugMetrics().put("Scan Code", scanCodeMetric);

                    String topic = "spBv1.0/" + connection.getGroupId() + "/NDATA/" + connection.getEdgeNodeId();
                    ((MainActivity) getActivity()).publish(connection, topic, payloadBuilder.createPayload(), 0, false);

                    // Reset the hashmap and button color
                    Log.d(TAG, "Resetting the publisher");
                    emulateScanButton.getBackground().clearColorFilter();
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception publishing ", e);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.scan_button) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(((MainActivity) getActivity()));
            scanIntegrator.initiateScan();
        }
    }
}

