FROM navikt/java:8

EXPOSE 8080

LABEL package="no.difi"
LABEL artifact="meldingsformidler"
LABEL version="2.0"
LABEL description="Meldingsformidler for offentlige tjenester levert av Difi"

ENV LC_ALL="no_NB.UTF-8"
ENV LANG="no_NB.UTF-8"
ENV TZ="Europe/Oslo"

RUN apt install jq -y

ENV RUNTIME_OPTS '--logging.config=/app/logback.xml'

COPY logback.xml /app
COPY workdir/app.jar /app/app.jar
COPY integrasjonspunkt-local.properties /app
COPY 10-inject-keystore-credentials.sh /init-scripts
COPY 11-inject-dpo-credentials.sh /init-scripts
COPY 20-decode-virkcert.sh /init-scripts

WORKDIR /app
