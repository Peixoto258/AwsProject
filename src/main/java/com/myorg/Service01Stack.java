package com.myorg;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.logs.LogGroup;
import software.constructs.Construct;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class Service01Stack extends Stack {
    public Service01Stack(final Construct scope, final String id, Cluster cluster) {
        this(scope, id, null, cluster);
    }

    public Service01Stack(final Construct scope, final String id, final StackProps props, Cluster cluster) {
        super(scope, id, props);

        ApplicationLoadBalancedFargateService service01 = generateFargateService("ALB01",
                "service-01",
                cluster,
                "projeto_aws_01",
                "Service01LogGroup",
                "Service01",
                "Service01",
                "jpann/projeto_aws_01:1.0.0");
    }

    private ApplicationLoadBalancedFargateService generateFargateService(String id,
                                                                         String serviceName,
                                                                         Cluster cluster,
                                                                         String containerName,
                                                                         String serviceLogGroup,
                                                                         String logGroupName,
                                                                         String streamPrefixName,
                                                                         String imageName
    ) {
        ApplicationLoadBalancedFargateService applicationLoadBalancedFargateService = ApplicationLoadBalancedFargateService
                .Builder
                .create(this, id)
                .serviceName(serviceName)
                .cluster(cluster)
                .cpu(256)
                .memoryLimitMiB(256)
                .desiredCount(2)
                .listenerPort(8080) // porta para acesso externo
                .taskImageOptions(ApplicationLoadBalancedTaskImageOptions.builder()
                                .containerName(containerName)
                                .containerPort(8080)
                                .logDriver(LogDriver.awsLogs(AwsLogDriverProps
                                        .builder()
                                        .logGroup(LogGroup
                                                .Builder
                                                .create(this, serviceLogGroup)
                                                .logGroupName(logGroupName)
                                                .removalPolicy(RemovalPolicy.DESTROY)
                                                .build())
                                        .streamPrefix(streamPrefixName)
                                        .build()))
                                .image(ContainerImage.fromRegistry(imageName))
                                .build())
                .publicLoadBalancer(true)
                .build();

        return applicationLoadBalancedFargateService;
    }
}
