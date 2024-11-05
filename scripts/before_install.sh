#!/bin/bash
# 기존 컨테이너 중지 및 삭제
docker stop my-spring-boot-app || true
docker rm my-spring-boot-app || true

# 최신 이미지 가져오기
docker pull <your-docker-hub-username>/my-spring-boot-app:latest