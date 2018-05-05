#!/usr/bin/env bash

current_path=$(dirname $(which $0))

${current_path}/stop.sh
${current_path}/start.sh

