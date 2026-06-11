#!/bin/sh
set -e

# Usage: /wait-for.sh host [port] -- command args...
HOST="$1"
PORT="${2:-5432}"

shift 2
if [ "$1" = "--" ]; then
  shift
fi

echo "Waiting for $HOST:$PORT..."

until nc -z "$HOST" "$PORT"; do
  echo "Waiting for $HOST:$PORT..."
  sleep 1
done

echo "Waiting for Config Server at config-server:8888/actuator/health..."
until wget -qO- http://config-server:8888/actuator/health 2>/dev/null | grep -q '"status":"UP"'; do
  echo "Config Server not ready yet..."
  sleep 1
done

echo "Dependencies ready — launching command:"
echo "$@"
exec "$@"