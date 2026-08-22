#!/usr/bin/env bash
set -euo pipefail

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
DEMO_DIR="$BASE_DIR/target/gossip-spring-demo"
JAR="$BASE_DIR/gossip-spring-example/target/gossip-spring-example-0.1.4-SNAPSHOT.jar"

cleanup() {
  if [[ -n "${NODE_A_PID:-}" ]]; then
    kill "$NODE_A_PID" 2>/dev/null || true
  fi
  if [[ -n "${NODE_B_PID:-}" ]]; then
    kill "$NODE_B_PID" 2>/dev/null || true
  fi
}
trap cleanup EXIT

cd "$BASE_DIR"
mvn -pl gossip-spring-example -am -DskipTests package

mkdir -p "$DEMO_DIR"

cat > "$DEMO_DIR/node-a.json" <<'JSON'
[{
  "cluster": "spring-demo",
  "id": "node-a",
  "uri": "http://127.0.0.1:18081",
  "properties": {},
  "gossip_interval": 500,
  "cleanup_interval": 5000,
  "window_size": 1000,
  "minimum_samples": 5,
  "convict_threshold": 2.6,
  "distribution": "exponential",
  "persist_ring_state": false,
  "persist_data_state": false,
  "members": [
    {"cluster": "spring-demo", "id": "node-b", "uri": "http://127.0.0.1:18082"}
  ]
}]
JSON

cat > "$DEMO_DIR/node-b.json" <<'JSON'
[{
  "cluster": "spring-demo",
  "id": "node-b",
  "uri": "http://127.0.0.1:18082",
  "properties": {},
  "gossip_interval": 500,
  "cleanup_interval": 5000,
  "window_size": 1000,
  "minimum_samples": 5,
  "convict_threshold": 2.6,
  "distribution": "exponential",
  "persist_ring_state": false,
  "persist_data_state": false,
  "members": [
    {"cluster": "spring-demo", "id": "node-a", "uri": "http://127.0.0.1:18081"}
  ]
}]
JSON

java -jar "$JAR" --server.port=18081 "--gossip.settings-location=file:$DEMO_DIR/node-a.json" > "$DEMO_DIR/node-a.log" 2>&1 &
NODE_A_PID=$!

java -jar "$JAR" --server.port=18082 "--gossip.settings-location=file:$DEMO_DIR/node-b.json" > "$DEMO_DIR/node-b.log" 2>&1 &
NODE_B_PID=$!

printf 'Node A: http://127.0.0.1:18081/\n'
printf 'Node B: http://127.0.0.1:18082/\n'
printf 'Logs: %s/node-a.log and %s/node-b.log\n' "$DEMO_DIR" "$DEMO_DIR"
printf 'Press Ctrl-C to stop both nodes.\n'

wait
