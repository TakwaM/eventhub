#!/bin/sh
# usage: ./wait-for.sh host:port -- command args...
hostport="$1"
shift
host=$(echo "$hostport" | cut -d: -f1)
port=$(echo "$hostport" | cut -d: -f2)
timeout=${WAIT_TIMEOUT:-60}

while ! nc -z "$host" "$port"; do
  timeout=$((timeout-1))
  if [ "$timeout" -le 0 ]; then
    echo "Timeout waiting for $host:$port"
    exit 1
  fi
  echo "Waiting for $host:$port ..."
  sleep 1
done

echo "$host:$port is available - launching command"
exec "$@"
