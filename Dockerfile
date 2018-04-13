FROM openjdk:9-jre-slim

MAINTAINER NAV IKT <kevin.sillerud@nav.no>

LABEL package="no.difi"
LABEL artifact="meldingsformidler"
LABEL version="2.0"
LABEL description="Meldingsformidler for offentlige tjenester levert av Difi"

EXPOSE 8080

ENV APP_DIR /var/lib/difi
ENV APP_PREFIX integrasjonspunkt
ENV APP_MAIN_CLASS no.difi.meldingsutveksling.IntegrasjonspunktApplication
ENV APP_PROFILE staging
ENV SPRING_CLOUD_CONFIG_ENABLED false
ENV SERVER_PORT 8080
ENV ENDPOINTS_ENABLED=false
ENV ENDPOINTS_HEALTH_SENSITIVE=false
ENV ENDPOINTS_HEALTH_ENABLED=true
ENV ENDPOINTS_INFO_ENABLED=true
#ENV SPRING_DATASOURCE_URL=jdbc:postgresql://tpa-move-integrasjonspunkt-postgresql.tpa/move_db

ADD ${APP_PREFIX}/target/${APP_PREFIX}*.jar ${APP_DIR}/

WORKDIR ${APP_DIR}

CMD APP_NAME=$(ls ${APP_PREFIX}*.jar) && java -jar ${APP_JAVA_PARAMS} ${APP_NAME} ${APP_MAIN_CLASS} --spring.profiles.active=${APP_PROFILE}
