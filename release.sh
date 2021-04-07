#!/bin/bash

# Fix username and password before running this script
export MAVEN_REPO_USER=circleCI
export MAVEN_REPO_PASS=FIXME
export MAVEN_REPO_URL=https://extranet.sympower.net/nexus/repository

RELEASE_VERSION=$1
NEXT_DEVELOPMENT_VERSION=$2

if [ -z "$RELEASE_VERSION" ]; then
  echo "Release version not specified!"
  HAS_ERROR="true"
fi
if [ -z "$NEXT_DEVELOPMENT_VERSION" ]; then
  echo "Next development version not specified!"
  HAS_ERROR="true"
fi
if [ ! -z "$HAS_ERROR" ]; then
  echo "Usage: ./release.sh <release version> <next development version>"
  exit 1
fi

mvn versions:set -DnewVersion=$RELEASE_VERSION -DprocessAllModules

cd org.eclipse.paho.jmeclient/org.eclipse.paho.jmeclient.mqttv3
ant clean install deploy
ANT_EXIT_CODE=$?
cd -

if [ $ANT_EXIT_CODE -ne 0 ]; then
  mvn versions:revert
  echo "================================================="
  echo "ERROR: Ant build failed, check above for details!"
  echo "================================================="
  exit $ANT_EXIT_CODE
fi

mvn versions:set -DnewVersion=$NEXT_DEVELOPMENT_VERSION -DprocessAllModules -DgenerateBackupPoms=false
