#!/bin/bash
set -e
set -u
set -x

# This script is separate so we can move the 'git pull' out of
# 'build_and_deploy.sh', otherwise the latter will be updated
# while it is running. Essentially, this means we can't modify
# this script crontab.sh at all.

cd /mnt/data/james/projects/dfielden/gloriana/libraryapp
git pull
./build_and_deploy.sh
