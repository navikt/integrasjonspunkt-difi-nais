FROM navikt/java:9

EXPOSE 8080

MAINTAINER NAV IKT <kevin.sillerud@nav.no>

LABEL package="no.difi"
LABEL artifact="meldingsformidler"
LABEL version="2.0"
LABEL description="Meldingsformidler for offentlige tjenester levert av Difi"

ENV LC_ALL="no_NB.UTF-8"
ENV LANG="no_NB.UTF-8"
ENV TZ="Europe/Oslo"

# Please see https://blogs.oracle.com/java-platform-group/java-se-support-for-docker-cpu-and-memory-limits
ENV DEFAULT_JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap"

ENV APP_MAIN_CLASS no.difi.meldingsutveksling.IntegrasjonspunktApplication
ENV APP_PROFILE staging
ENV SPRING_CLOUD_CONFIG_ENABLED false
ENV SERVER_PORT 8080
ENV ENDPOINTS_ENABLED=false
ENV ENDPOINTS_HEALTH_SENSITIVE=false
ENV ENDPOINTS_HEALTH_ENABLED=true
ENV ENDPOINTS_INFO_ENABLED=true
#ENV SPRING_DATASOURCE_URL=jdbc:postgresql://tpa-move-integrasjonspunkt-postgresql.tpa/move_db

EXPOSE 8080

WORKDIR /app

COPY integrasjonspunkt.jar /app/


CMD java -jar ${APP_JAVA_PARAMS} ${DEFAULT_JAVA_OPTS} integrasjonspunkt.jar ${APP_MAIN_CLASS} --spring.profiles.active=${APP_PROFILE}
