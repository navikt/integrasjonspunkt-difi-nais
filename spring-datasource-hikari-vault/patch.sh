#!/bin/bash

rm -rf workdir
mkdir -p workdir/BOOT-INF/lib
cp target/dependency/*.jar workdir/BOOT-INF/lib/
cp target/*.jar workdir/BOOT-INF/lib/
cd workdir || exit 1
wget -O app.jar https://beta-meldingsutveksling.difi.no/content/repositories/itest/no/difi/meldingsutveksling/integrasjonspunkt/2.0.0-SNAPSHOT/integrasjonspunkt-2.0.0-20190830.084655-20.jar
zip -r -0 app.jar BOOT-INF/
