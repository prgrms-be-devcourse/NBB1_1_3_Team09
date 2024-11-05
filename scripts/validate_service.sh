#!/bin/bash
# 최대 10회까지 애플리케이션 상태 확인을 재시도
for i in {1..10}; do
  # 애플리케이션 상태 확인
  curl -f http://localhost:8080 && exit 0  # 성공 시 스크립트 종료
  echo "Waiting for the application to be ready..."
  sleep 5  # 5초 대기 후 재시도
done

# 10회 재시도 후 실패 시 오류로 종료
echo "Application failed to start within expected time."
exit 1