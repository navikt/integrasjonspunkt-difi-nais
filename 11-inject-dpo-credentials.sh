#!/bin/bash

DPO_JSON="$(cat /var/run/secrets/nais.io/dpo/dpo.json | jq -r '. | {difi:{move:{dpo:.}}}')"

# combine keystore and dpo credentials json
export SPRING_APPLICATION_JSON="$(echo $SPRING_APPLICATION_JSON $DPO_JSON | jq -s '.[0] * .[1]')"
