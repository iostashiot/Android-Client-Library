package com.iostash.client;

import org.json.JSONObject;

public interface iostashCallbackListener{
    public void onConnection(iostashClient client) throws Exception;
    public void onDisconnection(JSONObject data);
  //  public void channelUpdate(JSONObject data);
}
