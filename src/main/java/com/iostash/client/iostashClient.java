/**
 * IOStash Android Client Library
 */

package com.iostash.client;


import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.ConnectException;
import javax.net.ssl.SSLContext;

public class iostashClient {
    private Socket mSocket;
    private String token;
    private boolean connectionStatus = false;

    public class iostashException extends Exception {
        public iostashException(String exception) {
            super("IOStash : " + exception);
        }
    }

    /**
     * Initilizes the IOStash client object with Access Token
     * @param accessToken User's X-Access-Token
     * @param listener    iostashCallbackListner object for attaching onConnection and onDisconnection.
     * @throws iostashException
     */
    public void init(final String accessToken, final iostashCallbackListener listener) throws iostashException {

        this.token = accessToken;
        if (!this.connectionStatus) {
            try {
                IO.Options opts = new IO.Options();
                opts.query = "accessToken=" + this.token;
                opts.reconnection = true;
                opts.sslContext = SSLContext.getDefault();
                opts.reconnectionAttempts = 99;
                opts.secure = true;
                mSocket = IO.socket("https://api.iostash.io:83", opts);
                mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.i("IOStash", "Server connection successful");
                        try {
                            listener.onConnection(iostashClient.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        System.out.println(args[0]);
                        JSONObject data = (JSONObject) args[0];
                        try {
                            listener.onDisconnection(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
                mSocket.connect();
            } catch (Exception e) {
                throw new iostashException("Connection to IOStash server failed!");
            }
        }
    }

    /**
     * Subscribes to a device under the user,
     * @param deviceId ID of the device to listened to. The user must have access rights to this device or the method will fail
     * @param listener iostashDeviceListener for attaching onUpdate, onCustomDataReceived and onLocationUpdate
     * @throws iostashException
     */
    public void subscribeDevice(final String deviceId, final iostashDeviceListener listener) throws iostashException {
        if (mSocket.connected()) {
            mSocket.emit("subscribeDevice", deviceId);
            mSocket.on("deviceUpdate" + deviceId, new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    JSONObject data = (JSONObject) objects[0];
                    try {
                        listener.onUpdate(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).on("devicesubscriptionFailed" + deviceId, new Emitter.Listener() {

                @Override
                public void call(Object... objects) {
                    try {
                        throw new iostashException("Device subscription failed! Make sure the device exists and you have permissions");
                    } catch (iostashException e) {
                        e.printStackTrace();
                    }
                }
            }).on("publish" + deviceId, new Emitter.Listener() {

                @Override
                public void call(Object... objects) {
                    JSONObject data = (JSONObject) objects[0];
                    try {
                        listener.onCustomDataReceived(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).on("newlocationUpdate" + deviceId, new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    JSONObject data = (JSONObject) objects[0];
                    try {
                        listener.onLocationUpdate(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Subscribe to a particular datapoint of a device
     *
     * @param deviceId      ID of the device to which the datapoint belongs to.
     * @param dataPointName Name of the datapoint to listen to
     * @param listener      iostashDataPointListener to attach onUpdate()
     * @throws iostashException
     */

    public void subscribeDataPoint(final String deviceId, final String dataPointName, final iostashDataPointListener listener) throws iostashException {
        if (mSocket.connected()) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("deviceID", deviceId);
                obj.put("dataPoint", dataPointName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("subscribeDataPoint", obj);
            mSocket.on("dataPointUpdate" + deviceId + dataPointName, new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    JSONObject data = (JSONObject) objects[0];
                    try {
                        listener.onUpdate(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).on("datapointsubscriptionFailed" + deviceId + dataPointName, new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    try {
                        throw new iostashException("Datapoint subscription failed! Make sure the device exists and you have permissions");
                    } catch (iostashException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Subscribe to a channel under the user
     *
     * @param channelId ID of the channel to listen to
     * @param listener  iostashChannelListener to attach onUpdate()
     * @throws Exception
     */

    public void subscribeChannel(final String channelId, final iostashChannelListener listener) throws Exception {
        if (mSocket.connected()) {
            mSocket.emit("channelSubscribe", channelId);
            mSocket.on("channelUpdate" + channelId, new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    JSONObject data = (JSONObject) objects[0];
                    try {
                        listener.onUpdate(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).on("channelsubscriptionFailed" + channelId, new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    try {
                        throw new iostashException("Channel subscription failed! Make sure that the channel exists and you have permissions");
                    } catch (iostashException e) {
                        e.printStackTrace();
                    }
                }

            });
        } else
            throw new ConnectException();
    }
}
