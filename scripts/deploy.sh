#!/bin/bash

# Define log directories
DEPLOY_LOG_DIR="${LOG_DIR}/deploy"
DEPLOY_ERR_LOG_DIR="${LOG_DIR}/deploy_err"

# Create directories if they don't exist
mkdir -p $DEPLOY_LOG_DIR $DEPLOY_ERR_LOG_DIR

CURRENT_TIME=$(date '+%Y-%m-%d %H:%M:%S %Z')
echo ">>> 현재 서버 시간: $CURRENT_TIME" >> "${LOG_DIR}/deploy_sh.log"

BUILD_JAR=$(ls /home/ubuntu/app/build/libs/moongkl-here-mobileapi-0.0.1-SNAPSHOT.jar)
JAR_NAME=$(basename $BUILD_JAR)
echo ">>> build 파일명: $JAR_NAME" >> "${LOG_DIR}/deploy_sh.log"

echo ">>> build 파일 복사" >> "${LOG_DIR}/deploy_sh.log"
DEPLOY_PATH=/home/ubuntu/app/
cp $BUILD_JAR $DEPLOY_PATH

echo ">>> 현재 실행중인 애플리케이션 pid 확인" >> "${LOG_DIR}/deploy_sh.log"
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo ">>> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> "${LOG_DIR}/deploy_sh.log"
else
  echo ">>> kill -15 $CURRENT_PID" >> "${LOG_DIR}/deploy_sh.log"
  kill -15 $CURRENT_PID
  sleep 40
fi

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo ">>> DEPLOY_JAR 배포 시각: $(date '+%H:%M:%S')" >> "${LOG_DIR}/deploy_sh.log"

export $(grep -v '^#' /home/ubuntu/.env | xargs -d '\n' -n 1)
# Java 어플리케이션 실행
CURRENT_TIME=$(date '+%Y%m%d_%H%M%S')
nohup java -jar $DEPLOY_JAR >> "${DEPLOY_LOG_DIR}/${CURRENT_TIME}.log" 2>>"${DEPLOY_ERR_LOG_DIR}/${CURRENT_TIME}.log" &
