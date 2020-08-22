
BASEDIR := $(abspath ../.. )

IMAGE_TAG := local/pityoulish-server


build:
	echo "BASEDIR=$(BASEDIR)"
	podman build -t $(IMAGE_TAG) -f $(abspath Containerfile) $(BASEDIR)

run:
	podman run --rm -p 2888:2888/tcp  -it $(IMAGE_TAG)
# need two ports for JRMI, registry and calls:  -p 1088:1088/tcp -p ...
