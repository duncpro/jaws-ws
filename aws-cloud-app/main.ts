import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import {Code, Function, Runtime} from '@aws-cdk/aws-lambda';
import {CfnOutput, Duration} from '@aws-cdk/core';
import {WebSocketApi, WebSocketStage} from '@aws-cdk/aws-apigatewayv2';
import {LambdaWebSocketIntegration} from '@aws-cdk/aws-apigatewayv2-integrations';
import {Effect, PolicyStatement} from '@aws-cdk/aws-iam';

export class MainStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const serverboundMessageHandler = new Function(this, 'ServerboundMessageHandler', {
      runtime: Runtime.JAVA_11,
      code: Code.fromAsset(process.env.PATH_TO_SERVERBOUND_MESSAGE_HANDLER_PACKAGE!),
      handler: "com.example.AWSLambdaEntryPoint",
      timeout: Duration.seconds(30),
      memorySize: 1024,
    });

    const wsApi = new WebSocketApi(this, "MainWebSocketApi", {
      defaultRouteOptions: {
        integration: new LambdaWebSocketIntegration({ handler: serverboundMessageHandler  })
      },
      disconnectRouteOptions: {
        integration: new LambdaWebSocketIntegration({ handler: serverboundMessageHandler })
      },
      connectRouteOptions: {
        integration: new LambdaWebSocketIntegration({ handler: serverboundMessageHandler })
      }
    });

    const wsApiStage = new WebSocketStage(this, "MainWebSocketApiStage", {
      autoDeploy: true,
      webSocketApi: wsApi,
      stageName: "main"
    });

    new CfnOutput(this, 'MainWebSocketApiUrl', {
      value: wsApiStage.url
    });

    serverboundMessageHandler.addEnvironment('AWS_WEB_SOCKET_API_ID', wsApi.apiId);
    serverboundMessageHandler.addEnvironment('AWS_WEB_SOCKET_API_REGION', wsApi.env.region);
    serverboundMessageHandler.addEnvironment('AWS_WEB_SOCKET_API_STAGE', wsApiStage.stageName);

    serverboundMessageHandler.addToRolePolicy(new PolicyStatement({
      effect: Effect.ALLOW,
      actions: [
        "execute-api:ManageConnections"
      ],
      resources: [
        `arn:aws:execute-api:${wsApi.env.region}:${wsApi.env.account}:${wsApi.apiId}/*`
      ]
    }));
  }
}

const app = new cdk.App();
new MainStack(app, 'JawsWsStack', {
  /* If you don't specify 'env', this stack will be environment-agnostic.
   * Account/Region-dependent features and context lookups will not work,
   * but a single synthesized template can be deployed anywhere. */

  /* Uncomment the next line to specialize this stack for the AWS Account
   * and Region that are implied by the current CLI configuration. */
  // env: { account: process.env.CDK_DEFAULT_ACCOUNT, region: process.env.CDK_DEFAULT_REGION },

  /* Uncomment the next line if you know exactly what Account and Region you
   * want to deploy the stack to. */
  // env: { account: '123456789012', region: 'us-east-1' },

  /* For more information, see https://docs.aws.amazon.com/cdk/latest/guide/environments.html */
});
