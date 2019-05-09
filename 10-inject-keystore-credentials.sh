#!/bin/bash

export SPRING_APPLICATION_JSON="${cat /var/run/secrets/nais.io/virksomhetssertifikat/credentials.json | jq -r '. | {difi:{move:{org:{keystore:.}}}}'}"
