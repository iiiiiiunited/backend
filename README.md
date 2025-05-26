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

## Cache

### 검색 API에 Cache 적용한 이유

캐시, DB 인덱스 등을 적용할 때에는 데이터 접근 빈도, 데이터 변경 빈도 등을 고려해야 한다.

티케팅 서비스에서의 검색 API의 경우, 특정 공연과 관련된 검색어에 대해서 일시적으로 트래픽이 폭증하는 경향이 있다. 
한 번 등록된 공연에 대한 정보가 변경되는 경우도 거의 없다.

때문에 해당 API에 캐시를 적용하는 것이 합리적이라고 판단했다.

**검색 API가 아니더라도 적용할만한 포인트?**

- 공연 상세 조회 : 조회 빈도가 높지만 갱신은 거의 이루어지지 않음
- 통계/랭킹 조회
  - 주간, 월간 인기 컨텐츠 등의 집계 결과
  - 이 경우에는 집계 서버를 별도로 두는 것이 효율적임

### 이미 Local Cache 가 적용되어있는 API를 왜 굳이 Redis Cache 로 수정 해야하는지 이유 (scale-out 관점에서)

Local Cache는 해당 애플리케이션 프로세스 내에서만 유효하다. 

추후 scale-out을 통해 인스턴스 개수가 늘어나면 서로 캐시가 불일치하게 된다. Redis와 같은 별도의 분산 캐시를 사용하면 이에 대해 일관성을 유지할 수 있다.

### remote cache에는 다양한 종류가 있는데 redis 선택한 이유

1. Memcached
- 순수 Key-Value, 매우 빠르지만 복제·Persistence 기능 제한

2. Hazelcast / Apache Ignite
- In-memory 데이터 그리드, 분산 연산 가능하지만 클러스터 세팅·오퍼레이션 복잡

3. Redis
- 다양한 데이터 구조(String, Hash, List, Set, Sorted Set)
- Replication & 클러스터링(샤딩) 지원 
- Persistence 옵션(AOF/RDB)으로 장애 복구 가능 
- TTL, Pub/Sub, Lua 스크립트 기능 제공

=> Redis 선택 이유
- 단순 key-value를 넘어 필요에 따라 Sorted Set으로 인기 순위, Hash로 객체 필드 캐싱 활용하는 등 범용성이 좋음
- 생태계(관련 라이브러리, 자료, 커뮤니티 등)가 풍부함

### redis cache에 저장한 자료구조와 그 자료구조를 선택한 이유

String 타입에 직렬화(JSON)된 결과 목록을 저장했다.

구현이 간단하고 직렬화/역직렬화된 DTO 객체를 그대로 사용할 수 있기 떄문에 이러한 방식을 채택했다.

### RDBMS NoSQL 차이

- RDBMS
  - 정해진 스키마에 따라 데이터를 저장하여야 하므로 명확한 데이터 구조를 보장
  - 관계는 각 데이터를 중복없이 한 번만 저장
  - 시스템이 커질 경우 JOIN문이 많은 복잡한 쿼리가 만들어질 수 있음
  - 성능 향상을 위해서는 서버의 성능을 향상 시켜야하는 Scale-up만을 지원 -> 이로 인해 비용이 기하급수적으로 늘어남

- NoSQL
  - NoSQL에서는 스키마가 없기 때문에 유연하며 자유로운 데이터 구조를 가질 수 있음
  - 데이터 분산이 용이하며 성능 향상을 위한 Saclue-up 뿐만이 아닌 Scale-out 또한 가능
  - 데이터 중복이 발생할 수 있으며 중복된 데이터가 변경 될 경우 수정을 모든 컬렉션에서 수행해야 함
  - 복잡한 트랜잭션을 제공하지 않음

- 캐시를 적용하지 않은 v1, redis 캐시를 적용한 v2 간의 성능 테스트 보고서
  - 평균 응답속도가 얼마나 빨라졌는지
  - 얼마나 많은 수의 사용자 감당할 수 있는지
  - vuser를 점진적으로 늘려보고, ramp up도 활용. 왜 필요한가?
  - TPS 참고해서 포화지점 (saturation point) 찾아보자
