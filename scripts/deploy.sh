# Define log and deploy directories
APP_DIR="/home/ec2-user/app"
LOG_DIR="/home/ec2-user/logs"
DEPLOY_LOG_DIR="${LOG_DIR}/deploy"
DEPLOY_ERR_LOG_DIR="${LOG_DIR}/deploy_err"

# Create directories if they don't exist
mkdir -p $DEPLOY_LOG_DIR $DEPLOY_ERR_LOG_DIR

CURRENT_TIME=$(date '+%Y-%m-%d %H:%M:%S %Z')
echo ">>> 현재 서버 시간: $CURRENT_TIME" >> "${LOG_DIR}/deploy_sh.log"

BUILD_JAR=$(ls ${APP_DIR}/build/libs/moongkl-here-mobileapi-0.0.1-SNAPSHOT.jar)
JAR_NAME=$(basename $BUILD_JAR)
echo ">>> build 파일명: $JAR_NAME" >> "${LOG_DIR}/deploy_sh.log"

echo ">>> build 파일 복사" >> "${LOG_DIR}/deploy_sh.log"
DEPLOY_PATH=${APP_DIR}/
cp $BUILD_JAR $DEPLOY_PATH

echo ">>> 현재 실행중인 애플리케이션 pid 확인" >> "${LOG_DIR}/deploy_sh.log"
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z "$CURRENT_PID" ]
then
  echo ">>> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> "${LOG_DIR}/deploy_sh.log"
else
  echo ">>> 애플리케이션 종료 중: kill -15 $CURRENT_PID" >> "${LOG_DIR}/deploy_sh.log"
  kill -15 $CURRENT_PID

  # 최대 60초 기다리면서 헬스체크
  for i in {1..60}
  do
    if ! ps -p $CURRENT_PID > /dev/null; then
      echo ">>> 애플리케이션 정상 종료 완료" >> "${LOG_DIR}/deploy_sh.log"
      break
    fi
    sleep 1
  done

  # 만약 60초 기다려도 종료 안 되면 SIGKILL
  if ps -p $CURRENT_PID > /dev/null; then
    echo ">>> 프로세스 강제 종료: kill -9 $CURRENT_PID" >> "${LOG_DIR}/deploy_sh.log"
    kill -9 $CURRENT_PID
  fi
fi

DEPLOY_JAR=${DEPLOY_PATH}$JAR_NAME
echo ">>> DEPLOY_JAR 배포 시각: $(date '+%H:%M:%S')" >> "${LOG_DIR}/deploy_sh.log"

export $(grep -v '^#' /home/ec2-user/.env | xargs -d '\n' -n 1)

# Java 애플리케이션 실행
CURRENT_TIME=$(date '+%Y%m%d_%H%M%S')
nohup java -jar $DEPLOY_JAR >> "${DEPLOY_LOG_DIR}/${CURRENT_TIME}.log" 2>>"${DEPLOY_ERR_LOG_DIR}/${CURRENT_TIME}.log" &
