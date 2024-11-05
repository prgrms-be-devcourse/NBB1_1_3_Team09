#!/bin/bash
# 서비스 상태 확인
curl -f http://localhost:8080 || exit 1