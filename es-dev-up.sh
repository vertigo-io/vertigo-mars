#!/bin/bash
# Start the dev ES and wait until it answers (run inside WSL)
cd "$(dirname "$0")"
docker compose -f docker-compose.dev.yml up -d 2>&1 | tail -1
for i in $(seq 1 24); do
  sleep 5
  if curl -s -m 2 http://localhost:9200 >/dev/null; then
    echo "ES OK dans WSL apres $((i*5))s"
    exit 0
  fi
done
echo "ES KO apres 120s"
docker logs mars-es9 2>&1 | tail -5
exit 1
