package com.duncpro.jaws.ws;

public class WebSocketApiCoordinates {
    private final String id;
    private final String region;
    private final String stage;

    public WebSocketApiCoordinates(String id, String region, String stage) {
        this.id = id;
        this.region = region;
        this.stage = stage;
    }

    public String getId() {
        return id;
    }

    public String getRegion() {
        return region;
    }

    public String getStage() {
        return stage;
    }
}
