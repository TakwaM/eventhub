#!/bin/sh
set -e

# Accepts:
#   wait-for.sh host port -- cmd...
#   wait-for.sh host:port -- cmd...
#   wait-for.sh host -- cmd...   (defaults to port 5432)

arg1="$1"
arg2="$2"

# default port
DEFAULT_PORT=5432

# if arg1 contains colon, split
case "$arg1" in
  *:*)
    host="${arg1%%:*}"
    port="${arg1##*:}"
    shift 1
    ;;
  *)
    host="$arg1"
    # if second arg is numeric and not "--", treat as port
    if [ -n "$arg2" ] && echo "$arg2" | grep -qE '^[0-9]+$'; then
      port="$arg2"
      shift 2
    else
      port="$DEFAULT_PORT"
      shift 1
    fi
    ;;
esac

# skip optional "--"
if [ "$1" = "--" ]; then
  shift
fi

echo "Waiting for $host:$port..."

until nc -z "$host" "$port"; do
  echo "Waiting for $host:$port..."
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