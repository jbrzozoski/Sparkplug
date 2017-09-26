package org.eclipse.paho.android.sample.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cirruslink.sparkplug.message.PayloadDecoder;
import com.cirruslink.sparkplug.message.SparkplugBPayloadDecoder;
import com.cirruslink.sparkplug.message.model.Metric;
import com.cirruslink.sparkplug.message.model.SparkplugBPayload;

import org.eclipse.paho.android.sample.R;
import org.eclipse.paho.android.sample.model.Message;
import org.eclipse.paho.android.sample.model.MessageType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class MessageListItemAdapter extends ArrayAdapter<Message>{

    private final Context context;
    private final ArrayList<Message> messages;

    public MessageListItemAdapter(Context context, ArrayList<Message> messages){
        super(context, R.layout.message_list_item, messages);
        this.context = context;
        this.messages = messages;

    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.message_list_item, parent, false);

        // Set up the topic tokens
        TextView topicTextView = (TextView) rowView.findViewById(R.id.message_topic_text);
        String[] topicTokens = messages.get(position).getTopic().split("/");
        if (messages.get(position).getMessageType() == MessageType.Published) {
            topicTextView.setText(topicTokens[1] + " :: " + topicTokens[3] + " :: Published");
        } else if (messages.get(position).getMessageType() == MessageType.Received) {
            topicTextView.setText(topicTokens[1] + " :: " + topicTokens[3] + " :: Received");
        }


        // Set up the payload
        try {
            PayloadDecoder<SparkplugBPayload> decoder = new SparkplugBPayloadDecoder();
            SparkplugBPayload incomingPayload = decoder.buildFromByteArray(messages.get(position).getMessage().getPayload());

            TextView payloadTextView = (TextView) rowView.findViewById(R.id.message_payload_text);

            StringBuilder sb = new StringBuilder();
            for (Metric metric : incomingPayload.getMetrics()) {
                sb.append(metric.getName()).append("=").append(metric.getValue()).append("   ");
            }

            payloadTextView.setText(sb.toString());
        } catch (Exception e) {
            Log.d(TAG, "Failed to parse out payload", e);
        }

        TextView dateTextView = (TextView) rowView.findViewById(R.id.message_date_text);
        DateFormat dateTimeFormatter = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
        String shortDateStamp = dateTimeFormatter.format(messages.get(position).getTimestamp());
        dateTextView.setText(context.getString(R.string.message_time_fmt, shortDateStamp));

        return rowView;
    }
}
