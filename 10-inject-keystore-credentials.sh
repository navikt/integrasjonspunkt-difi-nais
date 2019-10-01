#!/bin/bash

# bruk jks keystore (workaround for https://github.com/bcgit/bc-java/issues/586)
#export SPRING_APPLICATION_JSON="$(cat /var/run/secrets/nais.io/virksomhetssertifikat/credentials.json | jq -r '. | {difi:{move:{org:{keystore:.}}}}')"
export SPRING_APPLICATION_JSON="$(cat /var/run/secrets/nais.io/virksomhetssertifikat/credentials.json | sed 's/pkcs12/jks/' | jq -r '. | {difi:{move:{org:{keystore:.}}}}')"
