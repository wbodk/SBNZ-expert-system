#!/usr/bin/env bash
# Clean and install all Maven modules (model, rules, service).
# Skips tests — use scripts/test.sh to run them.

source "$(dirname "${BASH_SOURCE[0]}")/_env.sh"

cd "${PROJECT_DIR}"
./mvnw clean install -DskipTests "$@"
