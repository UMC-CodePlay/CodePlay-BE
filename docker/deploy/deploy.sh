# ECR에서 최신 이미지 pull
echo "ECR에 있는 이미지 불러오기"
if ! docker pull 120569606420.dkr.ecr.ap-northeast-2.amazonaws.com/umc/code-play:latest; then
    echo "이미지 불러오기에 실패했습니다."
    exit 1
fi

# Docker compose down으로 기존 컨테이너 중지 및 삭제
echo "Docker compose down 실행"
docker compose down

# Docker compose up 실행
echo "Docker compose up 실행"
if ! docker compose up -d; then
    echo "컨테이너 실행에 실패했습니다"
    exit 1
fi

# dangling 이미지 삭제
echo "dangling 이미지 삭제"
docker image prune -f

echo "모든 작업이 완료되었습니다."