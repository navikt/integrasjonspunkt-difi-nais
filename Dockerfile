FROM navikt/java:11

EXPOSE 8080

LABEL package="no.difi"
LABEL artifact="meldingsformidler"
LABEL version="2.0"
LABEL description="Meldingsformidler for offentlige tjenester levert av Difi"

ENV LC_ALL="no_NB.UTF-8"
ENV LANG="no_NB.UTF-8"
ENV TZ="Europe/Oslo"

RUN apt install jq

# -Dlogback.configurationFile=/app/logback-spring.xml'
#ENV JAVA_LOGG_OVERRIDE='-Dlogging.level.org.springframework.ws.client.MessageTracing=DEBUG -Dlogging.level.org.springframework.ws.server.MessageTracing=DEBUG -Dlogging.level.mf.logger.translog=DEBUG -Dlogging.level.no.difi.sdp.client2.internal.DigipostMessageSenderFacade=DEBUG'
#ENV JAVA_LOGG_OVERRIDE='-Dlogging.level=DEBUG'

ENV RUNTIME_OPTS 'no.difi.meldingsutveksling.IntegrasjonspunktApplication --spring.profiles.active=${APP_PROFILE}'
ENV APP_PROFILE staging
ENV SPRING_CLOUD_CONFIG_ENABLED true
ENV SERVER_PORT 8080
ENV ENDPOINTS_ENABLED=true
ENV ENDPOINTS_HEALTH_SENSITIVE=true
ENV ENDPOINTS_HEALTH_ENABLED=true
ENV ENDPOINTS_INFO_ENABLED=true
#ENV SPRING_DATASOURCE_URL=jdbc:postgresql://tpa-move-integrasjonspunkt-postgresql.tpa/move_db

COPY integrasjonspunkt.jar /app/app.jar
COPY logback-spring.xml /app
COPY config-dev.properties /app
COPY config-prod.properties /app
COPY 10-inject-keystore-credentials.sh /init-scripts

WORKDIR /app
