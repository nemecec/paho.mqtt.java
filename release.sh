#!/bin/bash

export MAVEN_REPO_URL=https://extranet.sympower.net/nexus/repository
RELEASE_SETTINGS_FILE="release-settings.sh"

if [ -f "$RELEASE_SETTINGS_FILE" ]; then
  source "$RELEASE_SETTINGS_FILE"
else
  echo "$RELEASE_SETTINGS_FILE missing! Use release-settings-sample.sh as a sample."
  exit 1
fi

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

git add pom.xml org.eclipse.paho.client.mqttv3/pom.xml

cd org.eclipse.paho.jmeclient/org.eclipse.paho.jmeclient.mqttv3
ant clean install deploy
ANT_EXIT_CODE=$?
cd -

if [ $ANT_EXIT_CODE -ne 0 ]; then
  git reset pom.xml org.eclipse.paho.client.mqttv3/pom.xml
  mvn versions:revert
  echo "================================================="
  echo "ERROR: Ant build failed, check above for details!"
  echo "================================================="
  exit $ANT_EXIT_CODE
else
  git commit -m "Release $RELEASE_VERSION"
fi

mvn versions:commit versions:set -DnewVersion=$NEXT_DEVELOPMENT_VERSION -DprocessAllModules -DgenerateBackupPoms=false

git add pom.xml org.eclipse.paho.client.mqttv3/pom.xml
git commit -m "Prepare for next development iteration ($NEXT_DEVELOPMENT_VERSION)"
