#!/bin/bash

base64 -d /var/run/secrets/nais.io/virksomhetssertifikat/key.p12.b64 > /tmp/key.p12
chmod 400 /tmp/key.p12
