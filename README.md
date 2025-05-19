# Tickenity

## 네이밍

```
브랜치:
feat/#2-unit-test

이슈:
feat : 테스트코드 작성

커밋메세지
feat/#3 : 테스트코드 작성

PR 
feat/#3 : 테스트코드 작성 
```

## 프로젝트 실행 방법

1. dev용 docker container 실행
```
 docker-compose -f docker-compose.dev.yml up -d
```

2. 루트 경로에 `.env` 파일 생성
```
JWT_SECRET=BvdCDROpy/QRNPizw10GXalXzl41f/YOK2SfJNK2s+w=
```
  - 민감 정보(토큰, API 키 등)은 반드시 `.env`에 넣어서 사용할 것!

3. Active Profiles `dev`로 설정
