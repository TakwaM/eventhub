#!/bin/sh

wait_for() {
  hostport="$1"
  host=$(echo "$hostport" | cut -d: -f1)
  port=$(echo "$hostport" | cut -d: -f2)
  timeout=${WAIT_TIMEOUT:-60}

  echo "Waiting for $host:$port ..."
  while ! nc -z "$host" "$port"; do
    timeout=$((timeout-1))
    if [ "$timeout" -le 0 ]; then
      echo "Timeout waiting for $host:$port"
      exit 1
    fi
    sleep 1
  done
  echo "$host:$port is available."
}

# attendre Postgres
wait_for "postgres:5432"

# attendre MongoDB
wait_for "mongodb:27017"

# lancer l'application
echo "Launching application..."
exec "$@"
