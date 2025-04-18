#!/usr/bin/env bash

hostport="$1"
shift

host=$(echo "$hostport" | cut -d: -f1)
port=$(echo "$hostport" | cut -d: -f2)

echo "⏳ Waiting for $host:$port..."

while ! (echo > /dev/tcp/$host/$port) 2>/dev/null; do
  sleep 0.5
done

echo "✅ $host:$port is up. Running $@"
exec "$@"
