#!/bin/bash
set -e
echo "🔁 Starting LOCAL: only DB's with ports exposed..."
#docker compose -f docker-compose.yml -f docker-compose.local.yml build agent-gateway
docker compose -f docker-compose.yml -f docker-compose.local.yml up -d
