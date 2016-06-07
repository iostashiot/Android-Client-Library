package com.iostash.client;

import org.json.JSONObject;

/**
 * Created by aravindvijayan on 04/06/16.
 */
public interface iostashDeviceListener {
    public void onUpdate(JSONObject data) throws Exception;
    public void onCustomDataReceived(JSONObject data) throws Exception;
    public void onLocationUpdate(JSONObject data) throws Exception;
}
