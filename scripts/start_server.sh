#!/bin/bash
# Docker 컨테이너 실행
docker run -d -p 8080:8080 \
  -v $(pwd)/src/main/resources/application.yaml:/app/config/application.yaml \
  --name my-spring-boot-app geonoo/my-spring-boot-app:latest