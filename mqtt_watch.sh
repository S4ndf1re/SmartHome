#!/bin/bash


~/development/cpp/martin/cmake-build-mqtt/mqtt-client-cli/mqtt-client-cli -H 192.168.100.10 -s -t doorlock/+/status -t doorlock/+/open -t doorlock/+/read -t doorlock/+/write/data -t doorlock/+/write/ok -t doorlock/+/error -t backend/status/active
