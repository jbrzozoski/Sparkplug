package org.eclipse.paho.android.sample.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import com.cirruslink.sparkplug.message.SparkplugBPayloadEncoder;
import com.cirruslink.sparkplug.message.model.SparkplugBPayload;
import com.cirruslink.sparkplug.message.model.SparkplugBPayload.SparkplugBPayloadBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.eclipse.paho.android.sample.R;
import org.eclipse.paho.android.sample.internal.Connections;
import org.eclipse.paho.android.sample.model.ConnectionModel;
import org.eclipse.paho.android.sample.model.PublishedMessage;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private FragmentDrawer drawerFragment;

    private static final String TAG = "MainActivity";

    private final ChangeListener changeListener = new ChangeListener();

    private final MainActivity mainActivity = this;

    private ArrayList<String> connectionMap;

    private static final String groupId = "Android Edge Nodes";
    private String edgeNodeId = "AEN";

    private static final int LOCATION_PERMISSION = 2;
    public static boolean HAS_LOCATION_PERMISSION = false;

    public static boolean HAS_FINGERPRINT_SUPPORT = false;
    private static final String KEY_NAME = "sparkplug_key";
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;
    private CancellationSignal cancellationSignal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        } else {
            HAS_LOCATION_PERMISSION = true;
        }

        //Button btn = (Button) findViewById(R.id.authBtn);
        /*
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            if (!keyguardManager.isKeyguardSecure()) {
                Log.d(TAG, "Secure lock screen not enabled");
            } else {

                if (!fingerprintManager.isHardwareDetected()) {
                    Log.d(TAG, "Device doesn't support fingerprint authentication");
                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    Log.d(TAG, "User hasn't enrolled any fingerprints to authenticate with");
                } else {
                    Log.d(TAG, "Fingerprint support is present");
                    HAS_FINGERPRINT_SUPPORT = true;

                    generateKey();

                    if (cipherInit()) {
                        cancellationSignal = new CancellationSignal();
                        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                        FingerprintHandler helper = new FingerprintHandler(this);
                        helper.startAuth(fingerprintManager, cryptoObject);

                    } else {
                        Log.e(TAG, "Failed to init the cipher");
                    }
                }
            }
        } else {
            Log.d(TAG, "No fingerprint support");
        }*/

        /*
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        edgeNodeId = wInfo.getMacAddress();
        */

        File file = new File(getApplicationContext().getFilesDir(), "id.txt");
        if (file.exists()) {
            try {
                Log.d(TAG, "Getting ID from f/s");
                BufferedReader br = new BufferedReader(new FileReader(file));
                edgeNodeId = br.readLine();
            } catch (Exception e) {
                Log.e(TAG, "Failed to set UUID", e);
            }
        } else {
            try {
                Log.d(TAG, "New install - writing ID to f/s");
                edgeNodeId = UUID.randomUUID().toString().substring(0, 18);
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(edgeNodeId);
                bw.write("\n");
                bw.flush();
                bw.close();
            } catch (Exception e) {
                Log.e(TAG, "Failed to set UUID", e);
            }
        }

        populateConnectionList();
    }

    public void removeConnectionRow(Connection connection){
        drawerFragment.removeConnection(connection);
        populateConnectionList();
    }


    private void populateConnectionList(){
        // Clear drawerFragment
        drawerFragment.clearConnections();

        // get all the available connections
        Map<String, Connection> connections = Connections.getInstance(this)
                .getConnections();
        int connectionIndex = 0;
        connectionMap = new ArrayList<String>();

        Iterator connectionIterator = connections.entrySet().iterator();
        while (connectionIterator.hasNext()){
            Map.Entry pair = (Map.Entry) connectionIterator.next();
            ((Connection) pair.getValue()).setGroupId(groupId);
            ((Connection) pair.getValue()).setEdgeNodeId(edgeNodeId);
            drawerFragment.addConnection((Connection) pair.getValue());
            connectionMap.add((String) pair.getKey());
            ++connectionIndex;
        }

        if(connectionMap.size() == 0){
            displayView(-1);
        } else {
            displayView(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onDrawerItemSelected(int position){
        displayView(position);
    }

    @Override
    public void onDrawerItemLongSelected(int position){
        displayDeleteView(position);
    }

    @Override
    public void onAddConnectionSelected() {
        Fragment editConnectionFragment =  new EditConnectionFragment();
        String title = "Edit Connection";
        displayFragment(editConnectionFragment, title);
    }

    @Override
    public void onHelpSelected() {
        Fragment helpFragment = new HelpFragment();
        displayFragment(helpFragment, getString(R.string.help_and_feedback));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] result) {
        super.onRequestPermissionsResult(requestCode, permissions, result);

        if(requestCode == LOCATION_PERMISSION && result[0] == PackageManager.PERMISSION_GRANTED) {
            HAS_LOCATION_PERMISSION = true;
        }
    }

    private void displayDeleteView(int position){
        if(position == -1){
            displayFragment(new HomeFragment(), "Home");
        } else {
            Fragment fragment  = new ManageConnectionFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ActivityConstants.CONNECTION_KEY, connectionMap.get(position));
            fragment.setArguments(bundle);
            Map<String, Connection> connections = Connections.getInstance(this)
                    .getConnections();
            Connection connection = connections.get(connectionMap.get(position));
            displayFragment(fragment, "");
        }
    }

    private void displayView(int position){
        if(position == -1){
            displayFragment(new HomeFragment(), "Home");
        } else {
            Fragment fragment  = new ConnectionFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ActivityConstants.CONNECTION_KEY, connectionMap.get(position));
            fragment.setArguments(bundle);
            Map<String, Connection> connections = Connections.getInstance(this)
                    .getConnections();
            Connection connection = connections.get(connectionMap.get(position));
            String title = connection.getId();
            displayFragment(fragment, title);
        }
    }

    private void displayFragment(Fragment fragment, String title){
        if (fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // Set Toolbar Title
            getSupportActionBar().setTitle(title);
        }
    }

    public void updateAndConnect(ConnectionModel model){
        Map<String, Connection> connections = Connections.getInstance(this)
                .getConnections();

        Log.i(TAG, "Updating connection: " + connections.keySet().toString());
        try {
            Connection connection = connections.get(model.getClientHandle());
            // First disconnect the current instance of this connection
            if(connection.isConnected()){
                connection.changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTING);

                SparkplugBPayloadBuilder deathPayload = new SparkplugBPayloadBuilder().setTimestamp(new Date());
                deathPayload = connection.addBdSeqNum(deathPayload);
                byte [] deathBytes = new SparkplugBPayloadEncoder().getBytes(deathPayload.createPayload());
                String topic = "spBv1.0/" + connection.getGroupId() + "/NDEATH/" + connection.getEdgeNodeId();
                connection.getClient().publish(topic, deathBytes, 0, false, null, null);
                connection.getMessages().add(0, new PublishedMessage(topic, new MqttMessage(deathBytes)));
                HistoryFragment.notifyDataSetChanged();

                connection.getClient().disconnect();
            }
            // Update the connection.
            connection.updateConnection(model.getClientId(), model.getServerHostName(), model.getServerPort(), model.isTlsConnection());
            connection.setGroupId(groupId);
            connection.setEdgeNodeId(edgeNodeId);
            connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);

            String[] actionArgs = new String[1];
            actionArgs[0] = model.getClientId();
            final ActionListener callback = new ActionListener(this,
                    ActionListener.Action.CONNECT, connection, actionArgs);
            connection.getClient().setCallback(new MqttCallbackHandler(this, model.getClientHandle()));

            connection.getClient().setTraceCallback(new MqttTraceCallback());
            MqttConnectOptions connOpts = optionsFromModel(model);

            MqttConnectOptions mqttConnectOptions = connection.getConnectionOptions();
            SparkplugBPayloadBuilder deathPayload = new SparkplugBPayloadBuilder().setTimestamp(new Date());
            deathPayload = connection.addBdSeqNum(deathPayload);
            byte [] deathBytes = new SparkplugBPayloadEncoder().getBytes(deathPayload.createPayload());
            String lwtTopic = "spBv1.0/" + connection.getGroupId() + "/NDEATH/" + connection.getEdgeNodeId();
            Log.d(TAG, "2. Setting up LWT: " + lwtTopic);
            connOpts.setWill(lwtTopic, deathBytes, 0, false);

            connection.addConnectionOptions(connOpts);
            Connections.getInstance(this).updateConnection(connection);
            drawerFragment.updateConnection(connection);

            connection.getClient().connect(connOpts, null, callback);
            Fragment fragment  = new ConnectionFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ActivityConstants.CONNECTION_KEY, connection.handle());
            fragment.setArguments(bundle);
            String title = connection.getId();
            displayFragment(fragment, title);
        } catch (Exception ex){
            Log.e(TAG, "Exception occurred updating connection: " + connections.keySet().toString() + " : " + ex.getMessage());
        }
    }


    /**
     * Takes a {@link ConnectionModel} and uses it to connect
     * and then persist.
     * @param model - The connection Model
     */
    public void persistAndConnect(ConnectionModel model){
        Log.i(TAG, "Persisting new connection:" + model.getClientHandle());
        Connection connection = Connection.createConnection(model.getClientHandle(),model.getClientId(),model.getServerHostName(),model.getServerPort(),this,model.isTlsConnection());
        connection.setGroupId(groupId);
        connection.setEdgeNodeId(edgeNodeId);
        connection.registerChangeListener(changeListener);
        connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);


        String[] actionArgs = new String[1];
        actionArgs[0] = model.getClientId();
        final ActionListener callback = new ActionListener(this,
                ActionListener.Action.CONNECT, connection, actionArgs);
        connection.getClient().setCallback(new MqttCallbackHandler(this, model.getClientHandle()));



        connection.getClient().setTraceCallback(new MqttTraceCallback());

        MqttConnectOptions connOpts = optionsFromModel(model);

        connection.addConnectionOptions(connOpts);
        Connections.getInstance(this).addConnection(connection);
        connectionMap.add(model.getClientHandle());
        drawerFragment.addConnection(connection);

        try {
            connection.getClient().connect(connOpts, null, callback);
            Fragment fragment  = new ConnectionFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ActivityConstants.CONNECTION_KEY, connection.handle());
            bundle.putBoolean(ActivityConstants.CONNECTED, true);
            fragment.setArguments(bundle);
            String title = connection.getId();
            displayFragment(fragment, title);

        }
        catch (MqttException e) {
            Log.e(this.getClass().getCanonicalName(),
                    "MqttException occurred", e);
        }

    }





    private MqttConnectOptions optionsFromModel(ConnectionModel model){

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(model.isCleanSession());
        connOpts.setConnectionTimeout(model.getTimeout());
        connOpts.setKeepAliveInterval(model.getKeepAlive());
        if(!model.getUsername().equals(ActivityConstants.empty)){
            connOpts.setUserName(model.getUsername());
        }

        if(!model.getPassword().equals(ActivityConstants.empty)){
            connOpts.setPassword(model.getPassword().toCharArray());
        }
        /*
        if(!model.getLwtTopic().equals(ActivityConstants.empty) && !model.getLwtMessage().equals(ActivityConstants.empty)){
            connOpts.setWill(model.getLwtTopic(), model.getLwtMessage().getBytes(), model.getLwtQos(), model.isLwtRetain());
        }*/
        //   if(tlsConnection){
        //       // TODO Add Keys to conOpts here
        //       //connOpts.setSocketFactory();
        //   }
        return connOpts;
    }




    public void connect(Connection connection) {
        String[] actionArgs = new String[1];
        actionArgs[0] = connection.getId();
        final ActionListener callback = new ActionListener(this,
                ActionListener.Action.CONNECT, connection, actionArgs);
        connection.getClient().setCallback(new MqttCallbackHandler(this, connection.handle()));
        try {
            MqttConnectOptions mqttConnectOptions = connection.getConnectionOptions();
            SparkplugBPayloadBuilder deathPayload = new SparkplugBPayloadBuilder().setTimestamp(new Date());
            deathPayload = connection.addBdSeqNum(deathPayload);
            byte [] deathBytes = new SparkplugBPayloadEncoder().getBytes(deathPayload.createPayload());
            String lwtTopic = "spBv1.0/" + connection.getGroupId() + "/NDEATH/" + connection.getEdgeNodeId();
            Log.d(TAG, "1. Setting up LWT: " + lwtTopic);
            mqttConnectOptions.setWill(lwtTopic, deathBytes, 0, false);

            connection.getClient().connect(mqttConnectOptions, null, callback);
        }
        catch (Exception e) {
            Log.e(this.getClass().getCanonicalName(),
                    "Exception occurred", e);
        }
    }

    public void disconnect(Connection connection){

        try {
            SparkplugBPayloadBuilder deathPayload = new SparkplugBPayloadBuilder().setTimestamp(new Date());
            deathPayload = connection.addBdSeqNum(deathPayload);
            byte [] deathBytes = new SparkplugBPayloadEncoder().getBytes(deathPayload.createPayload());
            String topic = "spBv1.0/" + connection.getGroupId() + "/NDEATH/" + connection.getEdgeNodeId();
            connection.getClient().publish(topic, deathBytes, 0, false, null, null);
            connection.getMessages().add(0, new PublishedMessage(topic, new MqttMessage(deathBytes)));
            HistoryFragment.notifyDataSetChanged();

            connection.getClient().disconnect();
        } catch( Exception ex){
            Log.e(TAG, "Exception occurred during disconnect: " + ex.getMessage());
        }
    }

    public void publish(Connection connection, String topic, String message, int qos, boolean retain){

        try {
            String[] actionArgs = new String[2];
            actionArgs[0] = message;
            actionArgs[1] = topic;
            final ActionListener callback = new ActionListener(this,
                    ActionListener.Action.PUBLISH, connection, actionArgs);
            connection.getClient().publish(topic, message.getBytes(), qos, retain, null, callback);
            connection.getMessages().add(0, new PublishedMessage(topic, new MqttMessage(message.getBytes())));
            HistoryFragment.notifyDataSetChanged();
        } catch( MqttException ex){
            Log.e(TAG, "Exception occurred during publish: " + ex.getMessage());
        }
    }

    public void publish(Connection connection, String topic, SparkplugBPayload payload, int qos, boolean retain){

        try {
            SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
            byte[] bytes = encoder.getBytes(payload);

            connection.getClient().publish(topic, bytes, qos, retain);
            connection.getMessages().add(0, new PublishedMessage(topic, new MqttMessage(bytes)));
            HistoryFragment.notifyDataSetChanged();
        } catch( Exception e){
            Log.e(TAG, "Exception occurred during publish: " + e.getMessage());
        }
    }

    /**
     * This class ensures that the user interface is updated as the Connection objects change their states
     *
     *
     */
    private class ChangeListener implements PropertyChangeListener {

        /**
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(PropertyChangeEvent event) {

            if (!event.getPropertyName().equals(ActivityConstants.ConnectionStatusProperty)) {
                return;
            }
            mainActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mainActivity.drawerFragment.notifyDataSetChanged();
                }

            });

        }
    }

    public void publishBirth(Connection connection) {
        connection.publishBirth(this);
    }


    public boolean updateFragmentViews(Connection connection) {
        boolean updateOccurred = false;

        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                //Log.d(TAG, "Found Fragment: " + fragment.toString());
                if (fragment instanceof ConnectionFragment) {
                    if (fragment.getChildFragmentManager() != null && fragment.getChildFragmentManager().getFragments() != null) {
                        for (Fragment childFragment : fragment.getChildFragmentManager().getFragments()) {
                            //Log.d(TAG, "Found Child Fragment: " + childFragment.toString());
                            if (childFragment instanceof PublishFragment) {

                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                if ((Double) connection.getSparkplugMetrics().get("Analog 1").getValue() != ((PublishFragment) childFragment).getAnalogOne()) {
                                    Log.d(TAG, "Setting Analog 1: " + (Double) connection.getSparkplugMetrics().get("Analog 1").getValue());
                                    ((PublishFragment) childFragment).setAnalogOne((Double) connection.getSparkplugMetrics().get("Analog 1").getValue());
                                    updateOccurred = true;
                                }
                                if ((Double) connection.getSparkplugMetrics().get("Analog 2").getValue() != ((PublishFragment) childFragment).getAnalogTwo()) {
                                    Log.d(TAG, "Setting Analog 2: " + (Double) connection.getSparkplugMetrics().get("Analog 2").getValue());
                                    ((PublishFragment) childFragment).setAnalogTwo((Double) connection.getSparkplugMetrics().get("Analog 2").getValue());
                                    updateOccurred = true;
                                }
                                if ((Double) connection.getSparkplugMetrics().get("Analog 3").getValue() != ((PublishFragment) childFragment).getAnalogThree()) {
                                    Log.d(TAG, "Setting Analog 3: " + (Double) connection.getSparkplugMetrics().get("Analog 3").getValue());
                                    ((PublishFragment) childFragment).setAnalogThree((Double) connection.getSparkplugMetrics().get("Analog 3").getValue());
                                    updateOccurred = true;
                                }
                                if ((Double) connection.getSparkplugMetrics().get("Analog 4").getValue() != ((PublishFragment) childFragment).getAnalogFour()) {
                                    Log.d(TAG, "Setting Analog 4: " + (Double) connection.getSparkplugMetrics().get("Analog 4").getValue());
                                    ((PublishFragment) childFragment).setAnalogFour((Double) connection.getSparkplugMetrics().get("Analog 4").getValue());
                                    updateOccurred = true;
                                }
                                if ((Boolean)connection.getSparkplugMetrics().get("Boolean 1").getValue() != ((PublishFragment) childFragment).getBooleanOne()) {
                                    Log.d(TAG, "Setting Boolean 1: " + (Boolean) connection.getSparkplugMetrics().get("Boolean 1").getValue());
                                    ((PublishFragment) childFragment).setBooleanOne((Boolean) connection.getSparkplugMetrics().get("Boolean 1").getValue());
                                    updateOccurred = true;
                                }
                                if ((Boolean)connection.getSparkplugMetrics().get("Boolean 2").getValue() != ((PublishFragment) childFragment).getBooleanTwo()) {
                                    Log.d(TAG, "Setting Boolean 2: " + (Boolean) connection.getSparkplugMetrics().get("Boolean 2").getValue());
                                    ((PublishFragment) childFragment).setBooleanTwo((Boolean) connection.getSparkplugMetrics().get("Boolean 2").getValue());
                                    updateOccurred = true;
                                }
                                if ((Boolean)connection.getSparkplugMetrics().get("Boolean 3").getValue() != ((PublishFragment) childFragment).getBooleanThree()) {
                                    Log.d(TAG, "Setting Boolean 3: " + (Boolean) connection.getSparkplugMetrics().get("Boolean 3").getValue());
                                    ((PublishFragment) childFragment).setBooleanThree((Boolean) connection.getSparkplugMetrics().get("Boolean 3").getValue());
                                    updateOccurred = true;
                                }
                                if ((Boolean)connection.getSparkplugMetrics().get("Boolean 4").getValue() != ((PublishFragment) childFragment).getBooleanFour()) {
                                    Log.d(TAG, "Setting Boolean 4: " + (Boolean) connection.getSparkplugMetrics().get("Boolean 4").getValue());
                                    ((PublishFragment) childFragment).setBooleanFour((Boolean) connection.getSparkplugMetrics().get("Boolean 4").getValue());
                                    updateOccurred = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return updateOccurred;
    }

    private void generateKey() {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore");
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                keyGenerator = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES,
                        "AndroidKeyStore");
            } catch (NoSuchAlgorithmException |
                    NoSuchProviderException e) {
                throw new RuntimeException(
                        "Failed to get KeyGenerator instance", e);
            }

            try {
                keyStore.load(null);
                keyGenerator.init(new
                        KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                                KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
                keyGenerator.generateKey();
                Log.d(TAG, "Successfully generated the key");
            } catch (NoSuchAlgorithmException |
                    InvalidAlgorithmParameterException
                    | CertificateException | IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Log.e(TAG, "Fingerprint not supported...");
            throw new RuntimeException();
        }
    }

    private boolean cipherInit() {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                cipher = Cipher.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES + "/"
                                + KeyProperties.BLOCK_MODE_CBC + "/"
                                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            } catch (NoSuchAlgorithmException |
                    NoSuchPaddingException e) {
                throw new RuntimeException("Failed to get Cipher", e);
            }

            try {
                keyStore.load(null);
                SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                        null);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return true;
            } catch (KeyPermanentlyInvalidatedException e) {
                return false;
            } catch (KeyStoreException | CertificateException
                    | UnrecoverableKeyException | IOException
                    | NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException("Failed to init Cipher", e);
            }
        } else {
            Log.e(TAG, "Fingerprint not supported...");
            return false;
        }
    }

    @TargetApi(23)
    private class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

        private CancellationSignal cancellationSignal;
        private Context appContext;

        public FingerprintHandler(Context context) {
            appContext = context;
        }

        public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
            CancellationSignal cancellationSignal = new CancellationSignal();
            if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "No permissions for USE_FINGERPRINT");
                return;
            }
            Log.d(TAG, "Attempting to authenticate...");
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        }

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            Log.d(TAG, "Authentication error: " + errString);
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            Log.d(TAG, "Authentication help: " + helpString);
        }

        @Override
        public void onAuthenticationFailed() {
            Log.d(TAG, "Authentication failed.");
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            Log.d(TAG, "Authentication succeeded: " +
                    result.getCryptoObject().getCipher() +
                    " :: " + result.getCryptoObject().getCipher().getAlgorithm() +
                    " :: " + result.getCryptoObject().getCipher().getBlockSize() +
                    " :: " + result.getCryptoObject().getCipher().getExemptionMechanism() +
                    " :: " + result.getCryptoObject().getCipher().getIV().toString() +
                    " :: " + result.getCryptoObject().getCipher().getOutputSize(100) +
                    " :: " + result.getCryptoObject().getCipher().getParameters() +
                    " :: " + result.getCryptoObject().getCipher().getProvider() +
                    " :: " + result.getCryptoObject().getMac() +
                    " :: " + result.getCryptoObject().getSignature());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        // Log.d(TAG, "Got something? " + resultCode + " :: " + intent.toString());

        if (scanningResult != null) {
            Log.d(TAG, "Got something? " + scanningResult.toString());
        } else{
            Log.d(TAG, "Didn't get anything?");
        }
    }
}
