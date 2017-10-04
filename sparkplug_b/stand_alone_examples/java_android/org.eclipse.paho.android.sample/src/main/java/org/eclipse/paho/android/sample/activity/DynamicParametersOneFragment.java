package org.eclipse.paho.android.sample.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.cirruslink.sparkplug.message.model.Metric;
import com.cirruslink.sparkplug.message.model.MetricDataType;
import com.cirruslink.sparkplug.message.model.SparkplugBPayload.SparkplugBPayloadBuilder;

import org.eclipse.paho.android.sample.R;
import org.eclipse.paho.android.sample.components.CustomEditText;
import org.eclipse.paho.android.sample.internal.Connections;

import java.util.Date;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static org.eclipse.paho.android.sample.R.id.dynamic_one_analogOne;
import static org.eclipse.paho.android.sample.R.id.dynamic_one_booleanOne;

public class DynamicParametersOneFragment extends Fragment {

    private Connection connection;

    private CustomEditText analogOneEditText;
    private Switch booleanOneSwitch;
    private Button submitButton;

    private View rootView = null;

    public DynamicParametersOneFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_dynamic_parameters_one, container, false);

        analogOneEditText = (CustomEditText) rootView.findViewById(dynamic_one_analogOne);
        booleanOneSwitch = (Switch) rootView.findViewById(dynamic_one_booleanOne);

        Log.d(TAG, "Setting I/O param values");
        analogOneEditText.setText("0.0");
        booleanOneSwitch.setChecked(false);

        analogOneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "ANALOG CHANGE!!! " + s.toString());
                submitButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            }
        });

        booleanOneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "BOOLEAN CHANGE!!! " + isChecked);
                submitButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            }
        });

        submitButton = (Button) rootView.findViewById(R.id.dynamic_one_io_submit_button);
        submitButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (connection.getSeqLock()) {
                    try {
                        SparkplugBPayloadBuilder payloadBuilder = new SparkplugBPayloadBuilder()
                                    .setTimestamp(new Date())
                                    .setSeq(connection.getSeqNum());
                        payloadBuilder.addMetric(new Metric.MetricBuilder("Predefined 1 Analog 1", MetricDataType.Double, getAnalogOne()).createMetric());
                        payloadBuilder.addMetric(new Metric.MetricBuilder("Predefined 1 Boolean 1", MetricDataType.Boolean, getBooleanOne()).createMetric());

                        String topic = "spBv1.0/" + connection.getGroupId() + "/DBIRTH/" + connection.getEdgeNodeId() + "/1";
                        ((MainActivity) getActivity()).publish(connection, topic, payloadBuilder.createPayload(), 0, false);

                        // Reset the hashmap and button color
                        Log.d(TAG, "Resetting the publisher");
                        submitButton.getBackground().clearColorFilter();
                    } catch (Exception e) {
                        Log.d(TAG, "Exception publishing ", e);
                    }
                }
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "DynamicParametersOneFragment - Removing all views to properly refresh");
        ((ViewGroup) rootView).removeAllViews();
    }

    public void setAnalogOne(double value) {
        Log.d(TAG, "PublishFragment - setting text " + value);
        analogOneEditText.setText(Double.toString(value));
    }

    public double getAnalogOne() {
        return Double.parseDouble(analogOneEditText.getText().toString());
    }

    public void setBooleanOne(boolean value) {
        Log.d(TAG, "PublishFragment - setting boolean " + value);
        booleanOneSwitch.setChecked(value);
    }

    public boolean getBooleanOne() {
        return booleanOneSwitch.isChecked();
    }
}
