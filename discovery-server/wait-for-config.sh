#!/bin/sh
set -e

echo "Waiting for config-server to be UP..."
# boucle jusqu'à ce que config-server renvoie status UP
until wget -qO- http://config-server:8888/actuator/health 2>/dev/null | grep -q '"status":"UP"'; do
  echo "config-server not ready, sleeping 1s..."
  sleep 1
done

echo "Config-server is UP — starting discovery"
exec java -jar /app/app.jar