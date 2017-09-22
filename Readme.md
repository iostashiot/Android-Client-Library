
![IOStash IoT PaaS](http://iostash.io/wp-content/uploads/2016/06/iostashbeta_black.png) 

Android Client Library
===================


[ ![Download](https://api.bintray.com/packages/iostash/maven/iostash/images/download.svg) ](https://bintray.com/iostash/maven/iostash/_latestVersion)


Android Client library for IOStash IoT PaaS. Supports realtime data subscription to Devices, Data Points, Location, Channels and Custom Data sent to devices.

 **Installing**

Add this to the build.gradle of your module:

    dependencies {
        compile 'com.iostash.client:iostash:0.1'
    }

 
 **How To Use**

This library supports all realtime events supported by IOStash. Throws iostashclientException in case of errors. Refer to API Docs on https://iostash.io for more information and guides on this library.

    public iostashClient iostash = new iostashClient();
    try {
        iostash.init("X-ACCESS_TOKEN_HERE", new iostashCallbackListener() {
            @Override
            public void onConnection(iostashClient client) throws Exception {
                client.subscribeDevice("DEVICEID_HERE", new iostashDeviceListener() {
                    @Override
                    public void onUpdate(JSONObject data) {
                        //Fired when data is pushed by the specified device
                    }

                    @Override
                    public void onCustomDataReceived(JSONObject data) throws Exception {
                        //Fired when custom data is published to the device
                    }

                    @Override
                    public void onLocationUpdate(JSONObject data) throws Exception {
                        //Fired when the specified device updates its location
                    }
                });

                client.subscribeDataPoint("DEVICEID_HERE","DATAPOINT_NAME_HERE", new iostashDataPointListener() {
                    @Override
                    public void onUpdate(JSONObject data) {
                        //Fired when the specified datapoint of the specified device updates data
                    }

                });

                client.subscribeChannel("CHANNELID_HERE",new iostashChannelListener(){
                    @Override
                    public void onUpdate(JSONObject data) {
                        //Fired when an update event is fired in the specified channel.
                    }
                });

            }

            @Override
            public void onDisconnection(JSONObject data) {
                //Handle disconnection
            }

        });
    } catch (Exception e) {
        e.printStackTrace();
    }
   
   
**Notes**

   Corresponding callbacks will be executed during updates. This library does not (yet) support data pushing to IOStash.
For support and assistance, drop an email to support@iostash.com
