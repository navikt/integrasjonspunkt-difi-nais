#!/bin/bash

export JAVA_OPTS="$JAVA_OPTS -Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true -Dcom.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump=true -Dcom.sun.xml.ws.transport.http.HttpAdapter.dump=true -Dcom.sun.xml.internal.ws.transport.http.HttpAdapter.dump=true -Dcom.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold=999999"
