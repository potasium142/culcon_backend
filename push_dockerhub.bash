#!/usr/bin/bash
podman build . -t localhost/culcon_backend

podman podman push localhost/culcon_backend:latest docker://docker.io/giangltce/culcon_user_backend:non_stable
