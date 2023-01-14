#!/bin/sh

DEPLOY_DIR=/home/grip/deploy
timestamp=`date +%Y%m%d%H%M%S`

if [ $USE_TRACES -eq 1 ]
then
  export JAVA_TOOL_OPTIONS=-javaagent:/opt/aws-opentelemetry-agent.jar
  export OTEL_TRACES_EXPORTER=otlp
  export OTEL_TRACES_SAMPLER=parentbased_traceidratio
  export OTEL_TRACES_SAMPLER_ARG=0.1
  export OTEL_EXPORTER_OTLP_ENDPOINT=http://adot-collector-ex-service:4317
  export OTEL_RESOURCE_ATTRIBUTES="service.name=gripcloud-admin"
fi

java -jar -Xms1024m -Xmx1536m -XX:+UseParallelGC -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=prod -Djava.net.preferIPv4Stack=true $DEPLOY_DIR/$1
