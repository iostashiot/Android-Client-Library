package com.iostash.client;

import org.json.JSONObject;

/**
 * Created by aravindvijayan on 04/06/16.
 */
public interface iostashChannelListener {
    public void onUpdate(JSONObject data);
}
