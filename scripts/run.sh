#!/usr/bin/env bash
# Start the Spring Boot service. Requires that model + rules are installed first.

source "$(dirname "${BASH_SOURCE[0]}")/_env.sh"

cd "${PROJECT_DIR}"
./mvnw install -pl model,rules -am -DskipTests
./mvnw spring-boot:run -pl service "$@"
