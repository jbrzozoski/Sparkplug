/*******************************************************************************
 * Copyright (c) 1999, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.paho.android.sample.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.cirruslink.sparkplug.message.PayloadDecoder;
import com.cirruslink.sparkplug.message.SparkplugBPayloadDecoder;
import com.cirruslink.sparkplug.message.SparkplugBPayloadEncoder;
import com.cirruslink.sparkplug.message.model.Metric;
import com.cirruslink.sparkplug.message.model.MetricDataType;
import com.cirruslink.sparkplug.message.model.SparkplugBPayload;
import com.cirruslink.sparkplug.message.model.SparkplugBPayload.SparkplugBPayloadBuilder;

import org.eclipse.paho.android.sample.R;
import org.eclipse.paho.android.sample.internal.Connections;
import org.eclipse.paho.android.sample.model.PublishedMessage;
import org.eclipse.paho.android.sample.model.Subscription;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import org.eclipse.paho.android.sample.Connection.ConnectionStatus;

/**
 * Handles call backs from the MQTT Client
 */
class MqttCallbackHandler implements MqttCallbackExtended {

    /** {@link Context} for the application used to format and import external strings**/
    private final Context context;
    /** Client handle to reference the connection that this handler is attached to**/
    private final String clientHandle;

    private static final String TAG = "MqttCallbackHandler";
    private static final String activityClass = "org.eclipse.paho.android.sample.activity.MainActivity";

    /**
     * Creates an <code>MqttCallbackHandler</code> object
     * @param context The application's context
     * @param clientHandle The handle to a {@link Connection} object
     */
    public MqttCallbackHandler(Context context, String clientHandle) {
        this.context = context;
        this.clientHandle = clientHandle;
    }

    /**
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.Throwable)
     */
    @Override
    public void connectionLost(Throwable cause) {
        if (cause != null) {
            Log.d(TAG, "Connection Lost: " + cause.getMessage());
            Connection c = Connections.getInstance(context).getConnection(clientHandle);
            c.addAction("Connection Lost");
            c.changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTED);

            String message = context.getString(R.string.connection_lost, c.getId(), c.getHostName());

            //build intent
            Intent intent = new Intent();
            intent.setClassName(context, activityClass);
            intent.putExtra("handle", clientHandle);

            //notify the user
            Notify.notifcation(context, message, intent, R.string.notifyTitle_connectionLost);
        }
    }

    /**
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String, org.eclipse.paho.client.mqttv3.MqttMessage)
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        //Get connection object associated with this object
        Connection c = Connections.getInstance(context).getConnection(clientHandle);
        c.messageArrived(topic, message);
        //get the string from strings.xml and format
        //String messageString = context.getString(R.string.messageRecieved, new String(message.getPayload()), topic+";qos:"+message.getQos()+";retained:"+message.isRetained());
        String messageString = "New message " + topic;
        Log.i(TAG, messageString);

        //update client history
        c.addAction(messageString);

        synchronized (c.getSeqLock()) {
            // Parse the message
            String[] topicTokens = topic.split("/");
            String groupId = topicTokens[1];
            String commandType = topicTokens[2];
            String edgeNodeId = topicTokens[3];

            try {
                // Update the data model
                PayloadDecoder<SparkplugBPayload> decoder = new SparkplugBPayloadDecoder();
                SparkplugBPayload incomingPayload = decoder.buildFromByteArray(message.getPayload());

                if (commandType.equals("NCMD")) {
                    Log.d(TAG, "Got an NCMD for " + edgeNodeId);

                    List<Metric> metrics = incomingPayload.getMetrics();

                    // Create a container for Android Form Components
                    List<Metric> androidFormComponents = new ArrayList<Metric>();

                    for (Metric metric : metrics) {
                        Log.d(TAG, "\tMetric: " + metric.getName());
                        if (metric.getName().equals("Analog 1")) {
                            c.getSparkplugMetrics().get("Analog 1").setValue(metric.getValue());
                        } else if (metric.getName().equals("Analog 2")) {
                            c.getSparkplugMetrics().get("Analog 2").setValue(metric.getValue());
                        } else if (metric.getName().equals("Analog 3")) {
                            c.getSparkplugMetrics().get("Analog 3").setValue(metric.getValue());
                        } else if (metric.getName().equals("Analog 4")) {
                            c.getSparkplugMetrics().get("Analog 4").setValue(metric.getValue());
                        } else if (metric.getName().equals("Boolean 1")) {
                            c.getSparkplugMetrics().get("Boolean 1").setValue(metric.getValue());
                        } else if (metric.getName().equals("Boolean 2")) {
                            c.getSparkplugMetrics().get("Boolean 2").setValue(metric.getValue());
                        } else if (metric.getName().equals("Boolean 3")) {
                            c.getSparkplugMetrics().get("Boolean 3").setValue(metric.getValue());
                        } else if (metric.getName().equals("Boolean 4")) {
                            c.getSparkplugMetrics().get("Boolean 4").setValue(metric.getValue());
                        } else if (metric.getName().equals("Node Control/Rebirth")) {
                            Log.d(TAG, "Handling rebirth request");
                            c.publishBirth(null);
                        } else if (metric.getName().equals("Predefined")) {
                            Log.d(TAG, "Handling predefined fragment");
                            if (metric.getValue() != null && metric.getValue().toString() != null && metric.getValue().toString().equals("One")) {
                                // Set up the connection args
                                Bundle bundle = new Bundle();
                                bundle.putString(ActivityConstants.CONNECTION_KEY, clientHandle);

                                // Replace the dynamic content with an 'predefined one' fragment
                                Log.d(TAG, "Replacing Fragment with 'Predefined One'");
                                DynamicParametersOneFragment newFragment = new DynamicParametersOneFragment();
                                newFragment.setArguments(bundle);

                                FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();

                                // Replace whatever is in the fragment_container view with this fragment,
                                // and add the transaction to the back stack so the user can navigate back
                                transaction.replace(R.id.dynamic_parameters_container, newFragment);
                                transaction.addToBackStack(null);

                                // Commit the transaction
                                transaction.commit();
                            } else if (metric.getValue() != null && metric.getValue().toString() != null && metric.getValue().toString().equals("Two")) {
                                // Set up the connection args
                                Bundle bundle = new Bundle();
                                bundle.putString(ActivityConstants.CONNECTION_KEY, clientHandle);

                                // Replace the dynamic content with an 'predefined two' fragment
                                Log.d(TAG, "Replacing Fragment with 'Predefined Two'");
                                DynamicParametersTwoFragment newFragment = new DynamicParametersTwoFragment();
                                newFragment.setArguments(bundle);

                                FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();

                                // Replace whatever is in the fragment_container view with this fragment,
                                // and add the transaction to the back stack so the user can navigate back
                                transaction.replace(R.id.dynamic_parameters_container, newFragment);
                                transaction.addToBackStack(null);

                                // Commit the transaction
                                transaction.commit();
                            }
                        } else {
                            Log.e(TAG, "Android Form Metric: " + metric.getName());
                            androidFormComponents.add(metric);
                        }
                    }

                    if (!androidFormComponents.isEmpty()) {
                        // Set up the connection args
                        Bundle bundle = new Bundle();
                        bundle.putString(ActivityConstants.CONNECTION_KEY, clientHandle);

                        // Replace the dynamic content with an 'predefined two' fragment
                        Log.d(TAG, "Replacing Fragment with dynamic fragment");
                        DynamicParametersFragment newFragment = new DynamicParametersFragment();
                        newFragment.setAndroidFormComponents(androidFormComponents);
                        newFragment.setArguments(bundle);

                        FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();

                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack so the user can navigate back
                        transaction.replace(R.id.dynamic_parameters_container, newFragment);
                        transaction.addToBackStack(null);

                        // Commit the transaction
                        transaction.commit();
                    }

                    // Update the fragment views
                    ((MainActivity) context).updateFragmentViews(c);

                    Log.d(TAG, "Got an NCMD for " + edgeNodeId);

                    SparkplugBPayloadBuilder outboundPayloadBuilder = new SparkplugBPayloadBuilder();
                    outboundPayloadBuilder.setTimestamp(new Date());
                    long seq = c.getSeqNum();
                    Log.d(TAG, "Setting seq: " + seq);
                    outboundPayloadBuilder.setSeq(seq);

                    for (Metric metric : metrics) {
                        Log.d(TAG, "\tMetric: " + metric.getName());
                        if (metric.getName().equals("Analog 1")) {
                            outboundPayloadBuilder.addMetric(new Metric.MetricBuilder("Analog 1", MetricDataType.Double, metric.getValue()).createMetric());
                        } else if (metric.getName().equals("Analog 2")) {
                            outboundPayloadBuilder.addMetric(new Metric.MetricBuilder("Analog 2", MetricDataType.Double, metric.getValue()).createMetric());
                        } else if (metric.getName().equals("Analog 3")) {
                            outboundPayloadBuilder.addMetric(new Metric.MetricBuilder("Analog 3", MetricDataType.Double, metric.getValue()).createMetric());
                        } else if (metric.getName().equals("Analog 4")) {
                            outboundPayloadBuilder.addMetric(new Metric.MetricBuilder("Analog 4", MetricDataType.Double, metric.getValue()).createMetric());
                        } else if (metric.getName().equals("Boolean 1")) {
                            outboundPayloadBuilder.addMetric(new Metric.MetricBuilder("Boolean 1", MetricDataType.Boolean, metric.getValue()).createMetric());
                        } else if (metric.getName().equals("Boolean 2")) {
                            outboundPayloadBuilder.addMetric(new Metric.MetricBuilder("Boolean 2", MetricDataType.Boolean, metric.getValue()).createMetric());
                        } else if (metric.getName().equals("Boolean 3")) {
                            outboundPayloadBuilder.addMetric(new Metric.MetricBuilder("Boolean 3", MetricDataType.Boolean, metric.getValue()).createMetric());
                        } else if (metric.getName().equals("Boolean 4")) {
                            outboundPayloadBuilder.addMetric(new Metric.MetricBuilder("Boolean 4", MetricDataType.Boolean, metric.getValue()).createMetric());
                        }
                    }

                    // Publish the NDATA Message
                    String outboundTopic = "spBv1.0/" + groupId + "/NDATA/" + edgeNodeId;
                    SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
                    byte[] bytePayload = encoder.getBytes(outboundPayloadBuilder.createPayload());
                    c.getClient().publish(outboundTopic, bytePayload, 0, false);
                    c.getMessages().add(0, new PublishedMessage(topic, new MqttMessage(bytePayload)));
                    HistoryFragment.notifyDataSetChanged();
                }

            } catch (Exception e) {
                Log.e(TAG, "Failed to decode payload", e);
            }
        }
    }

    /**
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Do nothing
    }

    @Override
    public void connectComplete(boolean reconnect, java.lang.String serverURI) {

        // Set up subscriptions
        Connection c = Connections.getInstance(context).getConnection(clientHandle);

        // Clean up old subscriptions
        List<Subscription> subscriptions = c.getSubscriptions();
        for (Subscription sub : subscriptions) {
            try {
                c.unsubscribe(sub);
            } catch (Exception e) {
                Log.e(TAG, "Failed to unsubscribe ", e);
            }
        }

        try {
            String topic = "spBv1.0/" + c.getGroupId() + "/NCMD/" + c.getEdgeNodeId() + "/#";
            c.addNewSubscription(new Subscription(topic, 0, clientHandle, true));

            topic = "spBv1.0/" + c.getGroupId() + "/DCMD/" + c.getEdgeNodeId() + "/#";
            c.addNewSubscription(new Subscription(topic, 0, clientHandle, true));

            Log.d(TAG, "Publishing Birth Certificate");
            c.publishBirth(null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to subscribe", e);
        }
    }
}
