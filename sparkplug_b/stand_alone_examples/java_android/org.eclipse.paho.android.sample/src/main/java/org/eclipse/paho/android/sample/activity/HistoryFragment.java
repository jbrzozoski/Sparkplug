package org.eclipse.paho.android.sample.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import org.eclipse.paho.android.sample.R;
import org.eclipse.paho.android.sample.components.MessageListItemAdapter;
import org.eclipse.paho.android.sample.internal.Connections;
import org.eclipse.paho.android.sample.internal.IReceivedMessageListener;
import org.eclipse.paho.android.sample.model.Message;
import org.eclipse.paho.android.sample.model.ReceivedMessage;

import java.util.ArrayList;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class HistoryFragment extends Fragment {

    private static MessageListItemAdapter messageListAdapter;

    private ArrayList<Message> messages;
    public HistoryFragment() {
        setHasOptionsMenu(true);
    }

    public static void notifyDataSetChanged() {
        messageListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Map<String, Connection> connections = Connections.getInstance(this.getActivity())
                .getConnections();
        final Connection connection = connections.get(this.getArguments().getString(ActivityConstants.CONNECTION_KEY));
        Log.d(TAG, "History Fragment: " + connection.getId());
        setHasOptionsMenu(true);
        messages = connection.getMessages();
        connection.addReceivedMessageListner(new IReceivedMessageListener() {
            @Override
            public void onMessageReceived(ReceivedMessage message) {
                Log.d(TAG, "Adding history message " + message.getTopic());
                Log.d(TAG, "Message array size: " + messages.size());
                messageListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_connection_history, container, false);

        messageListAdapter = new MessageListItemAdapter(getActivity(), messages);
        ListView messageHistoryListView = (ListView) rootView.findViewById(R.id.history_list_view);
        messageHistoryListView.setAdapter(messageListAdapter);

        Button clearButton = (Button) rootView.findViewById(R.id.history_clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messages.clear();
                messageListAdapter.notifyDataSetChanged();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }
}

