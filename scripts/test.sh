#!/usr/bin/env bash
# Build the project and run JUnit tests in the service module.
# Console output from each rule firing is captured by surefire.

source "$(dirname "${BASH_SOURCE[0]}")/_env.sh"

cd "${PROJECT_DIR}"
./mvnw install -pl model,rules -am -DskipTests
./mvnw test -pl service "$@"
