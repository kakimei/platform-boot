#!/bin/bash

TOMCAT_LOG_PATH=/boot/logs/platform-boot

export CATALINA_OPTS="$CATALINA_OPTS \
                        -server \
                        -Dspring.profiles.active="${SPRING_PROFILE}" \
                        -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${TOMCAT_LOG_PATH}/heapdump_`date '+%Y%m%d%H%M'`.hprof"
