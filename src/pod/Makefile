# This work is released into the Public Domain under the
# terms of the Creative Commons CC0 1.0 Universal license.
# https://creativecommons.org/publicdomain/zero/1.0/

BASEDIR := $(abspath ../.. )

IMAGE_TAG := pityoulish-server


build:
	echo "BASEDIR=$(BASEDIR)"
	podman build -t $(IMAGE_TAG) -f $(abspath Containerfile) $(BASEDIR)

run: PORTS := -p 1088:1088/tcp -p 8108:8108/tcp -p 2888:2888/tcp
run:
	podman run --rm -it $(PORTS) $(IMAGE_TAG)


retag-github: github-image-tag
	podman tag $(IMAGE_TAG) $(GITHUB_IMAGE_TAG)

push-github: retag-github
# GITHUB_CREDENTIALS must be set by the caller
	podman push --creds "$(GITHUB_CREDENTIALS)" $(GITHUB_IMAGE_TAG)

# provide defaults when running outside of a GitHub workflow
github-image-tag: GITHUB_REPOSITORY ?= pityoulish/origins
github-image-tag: GITHUB_RUN_NUMBER ?= $(shell date -u +%Y%m%d-%H%M)
github-image-tag: GITHUB_REF ?= refs/heads/$(shell git rev-parse --abbrev-ref HEAD || echo nobranch)
github-image-tag: GIT_BRANCH := $(notdir $(GITHUB_REF))

# define a tag for the container image on GitHub
# needs to be done with $(eval) so the variable is available to other targets
github-image-tag:
	$(eval GITHUB_IMAGE_TAG := ghcr.io/$(GITHUB_REPOSITORY)/$(IMAGE_TAG):$(GIT_BRANCH)-$(GITHUB_RUN_NUMBER))
	$(eval GITHUB_IMAGE_TAG := $(subst pityoulish/pityoulish-,pityoulish/,$(GITHUB_IMAGE_TAG)))
	@echo "github image tag is $(GITHUB_IMAGE_TAG)"
