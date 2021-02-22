#!/bin/bash
set -e
set -u
set -x

# The location that mvn will put the deploy JAR.
TARGET="$HOME/.m2/repository/com/danfielden/gloriana/1.0.0/gloriana-1.0.0.jar"

# The location of the running copy that Launcher will use on stoat.
TARGET_LAUNCHER="/mnt/data/launcher/swd/gloriana/data/gloriana.jar"

# Re-build.
mvn clean install
echo "build_and_deploy: Built target OK: $TARGET"

# Make sure it's dead. (Note that pgrep will return a non-zero exit code if
# no process is found, meaning the script will exit).
echo "build_and_deploy: Killing Gloriana Launcher processes:"
pgrep -u launcher -f "gloriana.jar"
sudo pkill -u launcher -f "gloriana.jar"

# Wait for the application to quit. At this point, the Launcher 15 second
# re-launch timer is counting down (it will have noticed the exit of the
# process).
echo "build_and_deploy: Sleeping for 5 seconds..."
sleep 5

# Replace the JAR of the running application.
sudo cp "$TARGET" "$TARGET_LAUNCHER"
sudo chown launcher "$TARGET_LAUNCHER"
sudo chmod 755 "$TARGET_LAUNCHER"

echo "build_and_deploy: Done."
