#!/bin/bash

base64 -i /var/run/secrets/nais.io/virksomhetssertifikat/key.p12.b64 -o /tmp/key.p12
chmod 400 /tmp/key.p12
