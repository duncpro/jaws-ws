package com.duncpro.jaws.ws;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiAsyncClient;

import java.net.URI;

public class WebSocketSupportModule extends AbstractModule {
    @Provides
    @Singleton
    public ApiGatewayManagementApiAsyncClient provideApiGatewayClient(WebSocketApiCoordinates
                                                                              apiCoordinates) {
        // https://{api-id}.execute-api.{region}.amazonaws.com/{stage}
        return ApiGatewayManagementApiAsyncClient.builder()
                .endpointOverride(URI.create("https://" + apiCoordinates.getId() + ".execute-api." +
                        apiCoordinates.getRegion() + ".amazonaws.com/" + apiCoordinates.getStage()))
                .build();
    }

    @Provides
    public WebSocketApiCoordinates provideWebSocketApiCoordinates() {
        return new WebSocketApiCoordinates(
                System.getenv("AWS_WEB_SOCKET_API_ID"),
                System.getenv("AWS_WEB_SOCKET_API_REGION"),
                System.getenv("AWS_WEB_SOCKET_API_STAGE")
        );
    }
}
