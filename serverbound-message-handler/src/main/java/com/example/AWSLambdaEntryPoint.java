package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.duncpro.jaws.AWSLambdaIntegrationModule;
import com.duncpro.jaws.AWSLambdaRuntime;
import com.duncpro.jaws.LNTLambdaRequestHandler;
import com.duncpro.jaws.ws.ServerboundMessageRouter;
import com.duncpro.jaws.ws.WebSocketSupportModule;
import com.google.inject.Guice;

/**
 * An instance of this class is instantiated by AWS upon cold start of a new AWS Lambda VM.
 * If you move this class make sure to update the AWS CDK script as well.
 */
public class AWSLambdaEntryPoint extends LNTLambdaRequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {
    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent event, Context context,
                                                        AWSLambdaRuntime runtime) {

        final var injector = Guice.createInjector(new AWSLambdaIntegrationModule(runtime),
                new WebSocketSupportModule(), new MainModule());

        injector.getInstance(ServerboundMessageRouter.class)
                .route(event);

        // This response is not passed back to the client but is instead an an indication to AWS that the message
        // was processed successfully.
        final var response = new APIGatewayV2ProxyResponseEvent();
        response.setStatusCode(200);
        return response;
    }
}
