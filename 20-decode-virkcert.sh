#!/bin/bash

base64 -d /var/run/secrets/nais.io/virksomhetssertifikat/key.p12.b64 > /tmp/key.p12
chmod 400 /tmp/key.p12

# - opprett jks keystore fra pkcs12 keystore (workaround for https://github.com/bcgit/bc-java/issues/586) -
SRC_ALIAS="$(cat /var/run/secrets/nais.io/virksomhetssertifikat/credentials.json | jq -r '.alias')"
SRC_PASSWORD="$(cat /var/run/secrets/nais.io/virksomhetssertifikat/credentials.json | jq -r '.password')"

keytool -importkeystore -srckeystore /tmp/key.p12 -srcstoretype pkcs12 -srcstorepass $SRC_PASSWORD -srcalias "$SRC_ALIAS" -destkeystore /tmp/key.jks -deststoretype jks -deststorepass $SRC_PASSWORD -destalias "$SRC_ALIAS"
chmod 400 /tmp/key.jks
# ---
