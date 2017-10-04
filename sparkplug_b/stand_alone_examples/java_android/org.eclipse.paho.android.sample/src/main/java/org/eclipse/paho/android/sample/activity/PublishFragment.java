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
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static org.eclipse.paho.android.sample.R.id.analogFour;
import static org.eclipse.paho.android.sample.R.id.analogOne;
import static org.eclipse.paho.android.sample.R.id.analogThree;
import static org.eclipse.paho.android.sample.R.id.analogTwo;
import static org.eclipse.paho.android.sample.R.id.booleanFour;
import static org.eclipse.paho.android.sample.R.id.booleanOne;
import static org.eclipse.paho.android.sample.R.id.booleanThree;
import static org.eclipse.paho.android.sample.R.id.booleanTwo;


public class PublishFragment extends Fragment {

    private Connection connection;

    private CustomEditText analogOneEditText;
    private CustomEditText analogTwoEditText;
    private CustomEditText analogThreeEditText;
    private CustomEditText analogFourEditText;
    private Switch booleanOneSwitch;
    private Switch booleanTwoSwitch;
    private Switch booleanThreeSwitch;
    private Switch booleanFourSwitch;
    private Button ioSubmitButton;

    private Map<String, Metric> queuedMetrics = new HashMap<String, Metric>();

    private View rootView = null;

    public PublishFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_publish, container, false);

        analogOneEditText = (CustomEditText) rootView.findViewById(analogOne);
        analogTwoEditText = (CustomEditText) rootView.findViewById(analogTwo);
        analogThreeEditText = (CustomEditText) rootView.findViewById(analogThree);
        analogFourEditText = (CustomEditText) rootView.findViewById(analogFour);

        booleanOneSwitch = (Switch) rootView.findViewById(booleanOne);
        booleanTwoSwitch = (Switch) rootView.findViewById(booleanTwo);
        booleanThreeSwitch = (Switch) rootView.findViewById(booleanThree);
        booleanFourSwitch = (Switch) rootView.findViewById(booleanFour);

        Log.d(TAG, "Setting I/O param values");
        analogOneEditText.setText(((Double) connection.getSparkplugMetrics().get("Analog 1").getValue()).toString());
        analogTwoEditText.setText(((Double) connection.getSparkplugMetrics().get("Analog 2").getValue()).toString());
        analogThreeEditText.setText(((Double) connection.getSparkplugMetrics().get("Analog 3").getValue()).toString());
        analogFourEditText.setText(((Double) connection.getSparkplugMetrics().get("Analog 4").getValue()).toString());

        booleanOneSwitch.setChecked((Boolean) connection.getSparkplugMetrics().get("Boolean 1").getValue());
        booleanTwoSwitch.setChecked((Boolean) connection.getSparkplugMetrics().get("Boolean 2").getValue());
        booleanThreeSwitch.setChecked((Boolean) connection.getSparkplugMetrics().get("Boolean 3").getValue());
        booleanFourSwitch.setChecked((Boolean) connection.getSparkplugMetrics().get("Boolean 4").getValue());

        analogOneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "ANALOG CHANGE!!! " + s.toString());
                handleAnalog(s, "Analog 1");
            }
        });

        analogTwoEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "ANALOG CHANGE!!! " + s.toString());
                handleAnalog(s, "Analog 2");
            }
        });

        analogThreeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "ANALOG CHANGE!!! " + s.toString());
                handleAnalog(s, "Analog 3");
            }
        });

        analogFourEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "ANALOG CHANGE!!! " + s.toString());
                handleAnalog(s, "Analog 4");
            }
        });

        booleanOneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "BOOLEAN CHANGE!!! " + isChecked);
                handleBoolean(isChecked, "Boolean 1");
            }
        });

        booleanTwoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "BOOLEAN CHANGE!!! " + isChecked);
                handleBoolean(isChecked, "Boolean 2");
            }
        });

        booleanThreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "BOOLEAN CHANGE!!! " + isChecked);
                handleBoolean(isChecked, "Boolean 3");
            }
        });

        booleanFourSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "BOOLEAN CHANGE!!! " + isChecked);
                handleBoolean(isChecked, "Boolean 4");
            }
        });

        ioSubmitButton = (Button) rootView.findViewById(R.id.io_submit_button);
        ioSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (connection.getSeqLock()) {
                    try {
                        if (queuedMetrics != null && !queuedMetrics.isEmpty()) {
                            SparkplugBPayloadBuilder payloadBuilder = new SparkplugBPayloadBuilder()
                                    .setTimestamp(new Date())
                                    .setSeq(connection.getSeqNum());

                            for (Metric metric : queuedMetrics.values()) {
                                payloadBuilder.addMetric(metric);

                                // Set the real internal value of this metric right before publish
                                connection.getSparkplugMetrics().get(metric.getName()).setValue(metric.getValue());
                            }

                            String topic = "spBv1.0/" + connection.getGroupId() + "/NDATA/" + connection.getEdgeNodeId();
                            ((MainActivity) getActivity()).publish(connection, topic, payloadBuilder.createPayload(), 0, false);

                            // Reset the hashmap and button color
                            Log.d(TAG, "Resetting the publisher");
                            queuedMetrics = new HashMap<String, Metric>();
                            ioSubmitButton.getBackground().clearColorFilter();
                        } else {
                            Log.d(TAG, "Nothing to publish");
                        }
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

        Log.d(TAG, "Removing all views to properly refresh");
        ((ViewGroup) rootView).removeAllViews();
    }

    public void setAnalogOne(double value) {
        Log.d(TAG, "PublishFragment - setting text " + value);
        analogOneEditText.setText(Double.toString(value));
    }

    public double getAnalogOne() {
        return Double.parseDouble(analogOneEditText.getText().toString());
    }

    public void setAnalogTwo(double value) {
        Log.d(TAG, "PublishFragment - setting text " + value);
        analogTwoEditText.setText(Double.toString(value));
    }

    public double getAnalogTwo() {
        return Double.parseDouble(analogTwoEditText.getText().toString());
    }

    public void setAnalogThree(double value) {
        Log.d(TAG, "PublishFragment - setting text " + value);
        analogThreeEditText.setText(Double.toString(value));
    }

    public double getAnalogThree() {
        return Double.parseDouble(analogThreeEditText.getText().toString());
    }

    public void setAnalogFour(double value) {
        Log.d(TAG, "PublishFragment - setting text " + value);
        analogFourEditText.setText(Double.toString(value));
    }

    public double getAnalogFour() {
        return Double.parseDouble(analogFourEditText.getText().toString());
    }

    public void setBooleanOne(boolean value) {
        Log.d(TAG, "PublishFragment - setting boolean " + value);
        booleanOneSwitch.setChecked(value);
    }

    public boolean getBooleanOne() {
        return booleanOneSwitch.isChecked();
    }

    public void setBooleanTwo(boolean value) {
        Log.d(TAG, "PublishFragment - setting boolean " + value);
        booleanTwoSwitch.setChecked(value);
    }

    public boolean getBooleanTwo() {
        return booleanTwoSwitch.isChecked();
    }

    public void setBooleanThree(boolean value) {
        Log.d(TAG, "PublishFragment - setting boolean " + value);
        booleanThreeSwitch.setChecked(value);
    }

    public boolean getBooleanThree() {
        return booleanThreeSwitch.isChecked();
    }

    public void setBooleanFour(boolean value) {
        Log.d(TAG, "PublishFragment - setting boolean " + value);
        booleanFourSwitch.setChecked(value);
    }

    public boolean getBooleanFour() {
        return booleanFourSwitch.isChecked();
    }

    private void handleAnalog(Editable s, String metricName) {
        synchronized (connection.getSeqLock()) {
            String analog = s.toString();
            if (analog == null || analog.trim().isEmpty()) {
                return;
            }

            try {
                if ((double) connection.getSparkplugMetrics().get(metricName).getValue() != Double.parseDouble(s.toString())) {
                    Log.d(TAG, "Handling analog: " + analog);
                    Metric metric = new Metric.MetricBuilder(metricName, MetricDataType.Double, Double.parseDouble(analog)).createMetric();
                    queuedMetrics.put(metricName, metric);
                    ioSubmitButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                }
            } catch (Exception e) {
                Log.d(TAG, "Failed to publish ", e);
            }
        }
    }

    private void handleBoolean(boolean isChecked, String metricName) {
        synchronized (connection.getSeqLock()) {
            boolean booleanOne = isChecked;

            try {
                // Only add the the queue if this was a locally generated change rather than the result of an NCMD
                if ((boolean) connection.getSparkplugMetrics().get(metricName).getValue() != isChecked) {
                    Log.d(TAG, "Handling boolean: " + booleanOne);
                    Metric metric = new Metric.MetricBuilder(metricName, MetricDataType.Boolean, booleanOne).createMetric();
                    queuedMetrics.put(metricName, metric);
                    ioSubmitButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                }
            } catch (Exception e) {
                Log.d(TAG, "Failed to publish ", e);
            }
        }
    }
}