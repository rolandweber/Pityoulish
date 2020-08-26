# This work is released into the Public Domain under the
# terms of the Creative Commons CC0 1.0 Universal license.
# https://creativecommons.org/publicdomain/zero/1.0/

BASEDIR := $(abspath ../.. )

IMAGE_TAG := local/pityoulish-server


build:
	echo "BASEDIR=$(BASEDIR)"
	podman build -t $(IMAGE_TAG) -f $(abspath Containerfile) $(BASEDIR)

run: PORTS := -p 1088:1088/tcp -p 8108:8108/tcp -p 2888:2888/tcp
run:
	podman run --rm -it $(PORTS) $(IMAGE_TAG)
