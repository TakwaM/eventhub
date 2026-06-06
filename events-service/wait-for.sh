#!/bin/sh
set -e

host="$1"
shift
cmd="$@"

echo "Waiting for $host to be ready..."

while ! nc -z "$host" 5432; do
  sleep 2
done

echo "$host is up!"
exec $cmd
