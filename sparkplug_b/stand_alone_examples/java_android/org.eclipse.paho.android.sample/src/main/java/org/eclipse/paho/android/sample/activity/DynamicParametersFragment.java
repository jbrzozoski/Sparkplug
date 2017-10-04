package org.eclipse.paho.android.sample.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.cirruslink.sparkplug.message.model.Metric;
import com.cirruslink.sparkplug.message.model.MetricDataType;
import com.cirruslink.sparkplug.message.model.SparkplugBPayload.SparkplugBPayloadBuilder;

import org.eclipse.paho.android.sample.R;
import org.eclipse.paho.android.sample.components.CustomEditText;
import org.eclipse.paho.android.sample.internal.Connections;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static android.content.ContentValues.TAG;

public class DynamicParametersFragment extends Fragment {

    private Connection connection;

    private List<Metric> androidFormComponents;

    private LinearLayout dynamicLinearLayout;

    Map<String, Switch> booleanFields;
    Map<Map.Entry<String, MetricDataType>, CustomEditText> otherFields;

    private Button submitButton;

    private View rootView = null;

    public DynamicParametersFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_dynamic_parameters, container, false);

        dynamicLinearLayout = (LinearLayout) rootView.findViewById(R.id.dynamic_parameters);

        if (androidFormComponents != null && !androidFormComponents.isEmpty()) {

            View topDividerView = new View(getContext());
            LinearLayout.LayoutParams topDividerLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, 2);
            topDividerView.setLayoutParams(topDividerLp);
            TypedArray topDividerArray = getContext().getTheme()
                    .obtainStyledAttributes(new int[] {android.R.attr.listDivider});
            Drawable topDividerDraw = topDividerArray.getDrawable(0);
            topDividerArray.recycle();
            topDividerView.setBackgroundDrawable(topDividerDraw);
            dynamicLinearLayout.addView(topDividerView);

            // Reset field lists
            booleanFields = new HashMap<>();
            otherFields = new HashMap<>();

            for (final Metric metric : androidFormComponents) {
                if (metric.getDataType() == MetricDataType.Boolean) {
                    Switch booleanSwitch = new Switch(getContext());
                    booleanSwitch.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    booleanSwitch.setText(metric.getName());
                    LinearLayout.LayoutParams booleanParams = (LinearLayout.LayoutParams) booleanSwitch.getLayoutParams();
                    booleanParams.leftMargin = 100;
                    booleanParams.rightMargin = 100;
                    booleanSwitch.setChecked((boolean) metric.getValue());

                    booleanSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            submitButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                        }
                    });

                    // Update maps and the layout
                    booleanFields.put(metric.getName(), booleanSwitch);
                    dynamicLinearLayout.addView(booleanSwitch);
                } else if (metric.getDataType() == MetricDataType.String || metric.getDataType() == MetricDataType.Text ||
                        metric.getDataType() == MetricDataType.Int8 || metric.getDataType() == MetricDataType.Int16 ||
                        metric.getDataType() == MetricDataType.Int32 || metric.getDataType() == MetricDataType.Int64 ||
                        metric.getDataType() == MetricDataType.UInt8 || metric.getDataType() == MetricDataType.UInt16 ||
                        metric.getDataType() == MetricDataType.UInt32 || metric.getDataType() == MetricDataType.UInt64 ||
                        metric.getDataType() == MetricDataType.Float || metric.getDataType() == MetricDataType.Double) {

                    LinearLayout linearLayout = new LinearLayout(getContext());
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    linearLayout.setLayoutParams(layoutParams);

                    TextView textView = new TextView(getContext(), null, R.style.spinnerTextFieldLabel);
                    textView.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    LinearLayout.LayoutParams textViewParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    textViewParams.leftMargin = 100;
                    textView.setLayoutParams(textViewParams);
                    textView.setText(metric.getName());
                    linearLayout.addView(textView);

                    CustomEditText editText = new CustomEditText(getContext());
                    editText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    LinearLayout.LayoutParams editTextParams = (LinearLayout.LayoutParams) editText.getLayoutParams();
                    editTextParams.leftMargin = 100;
                    editTextParams.rightMargin = 100;
                    editText.setLayoutParams(editTextParams);
                    editText.setEms(10);
                    editText.setLines(1);

                    if (metric.getDataType() == MetricDataType.String || metric.getDataType() == MetricDataType.Text) {
                        editText.setText((String) metric.getValue());
                    } else if (metric.getDataType() == MetricDataType.Int8 || metric.getDataType() == MetricDataType.Int16 ||
                            metric.getDataType() == MetricDataType.Int32 || metric.getDataType() == MetricDataType.Int64 ||
                            metric.getDataType() == MetricDataType.UInt8 || metric.getDataType() == MetricDataType.UInt16 ||
                            metric.getDataType() == MetricDataType.UInt32 || metric.getDataType() == MetricDataType.UInt64) {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                        editText.setText(metric.getValue().toString());
                    } else if (metric.getDataType() == MetricDataType.Float || metric.getDataType() == MetricDataType.Double) {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        editText.setText(metric.getValue().toString());
                    }

                    linearLayout.addView(editText);

                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                            submitButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                        }
                    });

                    // Update maps and the layout
                    //stringFields.put(metric.getName(), editText);
                    otherFields.put(new Entry<String, MetricDataType>() {
                        @Override
                        public String getKey() {
                            return metric.getName();
                        }

                        @Override
                        public MetricDataType getValue() {
                            return metric.getDataType();
                        }

                        @Override
                        public MetricDataType setValue(MetricDataType metricDataType) {
                            return null;
                        }
                    }, editText);
                    dynamicLinearLayout.addView(linearLayout, layoutParams);
                }
            }

            View bottomDividerView = new View(getContext());
            LinearLayout.LayoutParams bottomDividerLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, 2);
            bottomDividerView.setLayoutParams(bottomDividerLp);
            TypedArray bottomDividerArray = getContext().getTheme()
                    .obtainStyledAttributes(new int[] {android.R.attr.listDivider});
            Drawable bottomDividerDraw = bottomDividerArray.getDrawable(0);
            bottomDividerArray.recycle();
            bottomDividerView.setBackgroundDrawable(bottomDividerDraw);
            dynamicLinearLayout.addView(bottomDividerView);

            Log.d(TAG, "Adding button");
            submitButton = new Button(getContext());
            submitButton.setText("Submit");
            submitButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams submitButtonParams = (LinearLayout.LayoutParams) submitButton.getLayoutParams();
            submitButtonParams.gravity = Gravity.CENTER;
            submitButton.setLayoutParams(submitButtonParams);

            submitButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    synchronized (connection.getSeqLock()) {
                        try {
                            SparkplugBPayloadBuilder payloadBuilder = new SparkplugBPayloadBuilder()
                                    .setTimestamp(new Date())
                                    .setSeq(connection.getSeqNum());

                            for (Entry<String, Switch> entry : booleanFields.entrySet()) {
                                payloadBuilder.addMetric(new Metric.MetricBuilder(entry.getKey(), MetricDataType.Boolean, entry.getValue().isChecked()).createMetric());
                            }
                            for (Entry<Entry<String, MetricDataType>, CustomEditText> entry : otherFields.entrySet()) {
                                if (entry.getKey().getValue() == MetricDataType.String || entry.getKey().getValue() == MetricDataType.String) {
                                    payloadBuilder.addMetric(new Metric.MetricBuilder(entry.getKey().getKey(), entry.getKey().getValue(), entry.getValue().getText().toString()).createMetric());
                                } else  if (entry.getKey().getValue() == MetricDataType.Int8) {
                                    payloadBuilder.addMetric(new Metric.MetricBuilder(entry.getKey().getKey(), entry.getKey().getValue(), Byte.parseByte(entry.getValue().getText().toString())).createMetric());
                                } else  if (entry.getKey().getValue() == MetricDataType.UInt8 || entry.getKey().getValue() == MetricDataType.Int16) {
                                    payloadBuilder.addMetric(new Metric.MetricBuilder(entry.getKey().getKey(), entry.getKey().getValue(), Short.parseShort(entry.getValue().getText().toString())).createMetric());
                                } else  if (entry.getKey().getValue() == MetricDataType.UInt16 || entry.getKey().getValue() == MetricDataType.Int32) {
                                    payloadBuilder.addMetric(new Metric.MetricBuilder(entry.getKey().getKey(), entry.getKey().getValue(), Integer.parseInt(entry.getValue().getText().toString())).createMetric());
                                } else  if (entry.getKey().getValue() == MetricDataType.UInt32 || entry.getKey().getValue() == MetricDataType.Int64) {
                                    payloadBuilder.addMetric(new Metric.MetricBuilder(entry.getKey().getKey(), entry.getKey().getValue(), Long.parseLong(entry.getValue().getText().toString())).createMetric());
                                } else  if (entry.getKey().getValue() == MetricDataType.UInt64) {
                                    payloadBuilder.addMetric(new Metric.MetricBuilder(entry.getKey().getKey(), entry.getKey().getValue(), Long.parseLong(entry.getValue().getText().toString())).createMetric());
                                } else  if (entry.getKey().getValue() == MetricDataType.Float) {
                                    payloadBuilder.addMetric(new Metric.MetricBuilder(entry.getKey().getKey(), entry.getKey().getValue(), Float.parseFloat(entry.getValue().getText().toString())).createMetric());
                                } else  if (entry.getKey().getValue() == MetricDataType.Double) {
                                    payloadBuilder.addMetric(new Metric.MetricBuilder(entry.getKey().getKey(), entry.getKey().getValue(), Double.parseDouble(entry.getValue().getText().toString())).createMetric());
                                }
                            }

                            // Set the timestamp
                            payloadBuilder.addMetric(new Metric.MetricBuilder("DateTime", MetricDataType.DateTime, new Date()).createMetric());

                            // Set the latitude and longitude
                            if (Build.VERSION.SDK_INT >= 23 &&
                                    ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Log.e(TAG, "Location Permissions is not enabled");
                            } else {
                                LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                                Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                payloadBuilder.addMetric(new Metric.MetricBuilder("Position/altitude", MetricDataType.Double, locationGPS.getAltitude()).createMetric());
                                payloadBuilder.addMetric(new Metric.MetricBuilder("Position/latitude", MetricDataType.Double, locationGPS.getLatitude()).createMetric());
                                payloadBuilder.addMetric(new Metric.MetricBuilder("Position/longitude", MetricDataType.Double, locationGPS.getLongitude()).createMetric());
                                Date lockDate = new Date();
                                lockDate.setTime(locationGPS.getTime());
                                payloadBuilder.addMetric(new Metric.MetricBuilder("Position/lock_datetime", MetricDataType.DateTime, lockDate).createMetric());
                            }

                            String topic = "spBv1.0/" + connection.getGroupId() + "/DBIRTH/" + connection.getEdgeNodeId() + "/" + "3";
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

            dynamicLinearLayout.addView(submitButton);
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "DynamicParametersFragment - Removing all views to properly refresh");
        ((ViewGroup) rootView).removeAllViews();
    }

    public void setAndroidFormComponents(List<Metric> androidFormComponents) {
        Log.d(TAG, "Setting the Android dynamic form components");
        this.androidFormComponents = androidFormComponents;
    }
}
