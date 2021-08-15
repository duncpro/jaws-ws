val serverboundMessageHandlerPackage: Configuration by configurations.creating

dependencies {
    serverboundMessageHandlerPackage(project(":serverbound-message-handler", "lambdaPackage"))
}

val deploy by tasks.registering {
    dependsOn(serverboundMessageHandlerPackage)

    doLast {
        exec {
            commandLine("cdk", "deploy", "--outputs-file", "latest-deployment-cfn-outputs.json",
                "--require-approval", "never")
            environment("PATH_TO_SERVERBOUND_MESSAGE_HANDLER_PACKAGE",
                serverboundMessageHandlerPackage.singleFile.absolutePath)
        }
    }

    outputs.file("./latest-deployment-cfn-outputs.json");
    outputs.upToDateWhen { false } // Defer diffing to AWS CDK CLI
}

// Expose the deployed REST API URL to the integration testing module.
val cfnOutputs by configurations.registering {}
artifacts.add(cfnOutputs.name, deploy)
