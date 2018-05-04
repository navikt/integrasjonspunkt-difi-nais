FROM openjdk:9-jre-slim

EXPOSE 8080

MAINTAINER NAV IKT <tommy.kristiansen@nav.no>

LABEL package="no.difi"
LABEL artifact="meldingsformidler"
LABEL version="2.0"
LABEL description="Meldingsformidler for offentlige tjenester levert av Difi"

ENV LC_ALL="no_NB.UTF-8"
ENV LANG="no_NB.UTF-8"
ENV TZ="Europe/Oslo"

# Please see https://blogs.oracle.com/java-platform-group/java-se-support-for-docker-cpu-and-memory-limits
ENV JAVA_APP_PARAMS='-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Dlogback.configurationFile=/app/logback-spring.xml'
#ENV JAVA_LOGG_OVERRIDE='-Dlogging.level.org.springframework.ws.client.MessageTracing=DEBUG -Dlogging.level.org.springframework.ws.server.MessageTracing=DEBUG -Dlogging.level.mf.logger.translog=DEBUG -Dlogging.level.no.difi.sdp.client2.internal.DigipostMessageSenderFacade=DEBUG'
ENV JAVA_LOGG_OVERRIDE='-Dlogging.level=DEBUG'

ENV APP_MAIN_CLASS no.difi.meldingsutveksling.IntegrasjonspunktApplication
ENV APP_PROFILE staging
ENV SPRING_CLOUD_CONFIG_ENABLED false
ENV SERVER_PORT 8080
ENV ENDPOINTS_ENABLED=false
ENV ENDPOINTS_HEALTH_SENSITIVE=false
ENV ENDPOINTS_HEALTH_ENABLED=true
ENV ENDPOINTS_INFO_ENABLED=true
#ENV SPRING_DATASOURCE_URL=jdbc:postgresql://tpa-move-integrasjonspunkt-postgresql.tpa/move_db

COPY integrasjonspunkt.jar /app/app.jar
COPY logback-spring.xml /app


WORKDIR /app

CMD java -jar ${APP_JAVA_PARAMS} ${JAVA_LOGG_OVERRIDE} app.jar ${APP_MAIN_CLASS} --spring.profiles.active=${APP_PROFILE}
