#!/usr/bin/env bash
# Shared environment for build/test/run scripts.
# Sourced by the other scripts; not meant to be executed directly.

set -euo pipefail

# Repo root (absolute, regardless of caller's CWD)
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_DIR="${REPO_ROOT}/incident-detection-app"

# Pick a Java 11+ JDK. Drools 7.49 + MVEL 2.5 work on Java 11/17/21.
# If JAVA_HOME is already set and points to a working JDK, respect it.
if [[ -z "${JAVA_HOME:-}" ]]; then
    if command -v /usr/libexec/java_home >/dev/null 2>&1; then
        # macOS: prefer Java 21 if available, fall back to 17, 11
        for v in 21 17 11; do
            if home="$(/usr/libexec/java_home -v "$v" 2>/dev/null)"; then
                export JAVA_HOME="$home"
                break
            fi
        done
    fi
fi

if [[ -z "${JAVA_HOME:-}" ]]; then
    echo "ERROR: JAVA_HOME is not set and no JDK 11+ was found." >&2
    echo "Install a JDK (11/17/21) and try again." >&2
    exit 1
fi

echo "Using JAVA_HOME=${JAVA_HOME}"
echo "Project    : ${PROJECT_DIR}"
