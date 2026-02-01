#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
#
# Optional ENV vars
#   JAVA_HOME - location of a JDK home dir
#   MAVEN_OPTS - parameters passed to the Java VM when running Maven
# ----------------------------------------------------------------------------

if [ -z "$MAVEN_OPTS" ]; then
  MAVEN_OPTS="-Xmx1024m"
fi

if [ -z "$JAVA_HOME" ] ; then
  JAVACMD="`\\unset -f command 2>/dev/null; \\command -v java`"
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly." >&2
  echo "  We cannot execute $JAVACMD" >&2
  exit 1
fi

WDIR="`dirname "$0"`"
WDIR="`cd "$WDIR" && pwd`"

# Provide a "standardized" way to retrieve the CLI args that will
# work with both Windows and non-Windows executions.
MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-"$WDIR"}"
export MAVEN_PROJECTBASEDIR

WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"
WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"

if [ -r "$WRAPPER_PROPERTIES" ]; then
  WRAPPER_URL=$(grep "wrapperUrl" "$WRAPPER_PROPERTIES" | cut -d'=' -f2- | tr -d '[:space:]')
  DIST_URL=$(grep "distributionUrl" "$WRAPPER_PROPERTIES" | cut -d'=' -f2- | tr -d '[:space:]')
fi

# Download maven wrapper jar if not present
if [ ! -f "$WRAPPER_JAR" ]; then
  if [ -n "$WRAPPER_URL" ]; then
    echo "Downloading Maven Wrapper..."
    if command -v wget > /dev/null; then
      wget -q -O "$WRAPPER_JAR" "$WRAPPER_URL"
    elif command -v curl > /dev/null; then
      curl -sL -o "$WRAPPER_JAR" "$WRAPPER_URL"
    else
      echo "Error: Cannot download maven-wrapper.jar. Install wget or curl." >&2
      exit 1
    fi
  fi
fi

# If wrapper jar exists, use it; otherwise download Maven directly
if [ -f "$WRAPPER_JAR" ]; then
  exec "$JAVACMD" \
    $MAVEN_OPTS \
    $MAVEN_DEBUG_OPTS \
    -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECTBASEDIR" \
    -classpath "$WRAPPER_JAR" \
    org.apache.maven.wrapper.MavenWrapperMain "$@"
else
  # Fallback: download and use Maven directly
  MAVEN_HOME="$MAVEN_PROJECTBASEDIR/.mvn/maven"
  MAVEN_BIN="$MAVEN_HOME/bin/mvn"

  if [ ! -f "$MAVEN_BIN" ]; then
    if [ -n "$DIST_URL" ]; then
      echo "Downloading Apache Maven..."
      mkdir -p "$MAVEN_HOME"
      TMPFILE=$(mktemp)
      if command -v wget > /dev/null; then
        wget -q -O "$TMPFILE" "$DIST_URL"
      elif command -v curl > /dev/null; then
        curl -sL -o "$TMPFILE" "$DIST_URL"
      else
        echo "Error: Cannot download Maven. Install wget or curl." >&2
        exit 1
      fi
      # Extract
      if command -v unzip > /dev/null; then
        unzip -q "$TMPFILE" -d "$MAVEN_HOME"
        # Move contents up one level
        mv "$MAVEN_HOME"/apache-maven-*/* "$MAVEN_HOME"/
        rm -rf "$MAVEN_HOME"/apache-maven-*
      else
        echo "Error: unzip is required." >&2
        exit 1
      fi
      rm -f "$TMPFILE"
    else
      echo "Error: Cannot find Maven distribution URL." >&2
      exit 1
    fi
  fi

  exec "$MAVEN_BIN" $MAVEN_OPTS "$@"
fi
