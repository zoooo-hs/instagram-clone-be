# Instagram Clone Project (Backend)

![Generic badge](https://img.shields.io/badge/version-1.0.1-green.svg)

## 목차

1. [프로젝트의 목적](#프로젝트의-목적)
2. [프로젝트의 기능](#프로젝트의-기능)
3. [프로젝트의 기술 스택](#프로젝트의-기술-스택)
4. [실행 방법](#실행-방법)
   1. [시작하기-앞서](#시작하기-앞서)
   2. [Source Code](#Source-Code)
   3. [Docker](#Docker)
5. [변경사항 및 TODO](#변경사항-및-TODO)

## 프로젝트의 목적

Spring boot 기반 프로젝트로 REST API 서버 구현 및 TDD 실습

## 프로젝트의 기능

- 기존에 존재하는 SNS [인스타그램](https://www.instagram.com/) 백엔드 기능을 일부 사용 가능
- 자세한 API 구성 설명은 [Swagger Hub - instagram-clone-be](https://app.swaggerhub.com/apis-docs/zoooo-hs/instagram-clone-be) 참고
  - 회원
    - 회원 가입
    - 로그인
    - 회원 바이오, 프로필 사진 변경
    - 회원 정보 조회
  - 팔로우/언팔로우
  - 게시글 관리
    - 피드 불러오기
      - 팔로잉 및 자신의 게시글을 불러옴
    - 게시글 작성/수정/삭제
  - 좋아요/좋아요 취소
    - 댓글, 게시글 좋아요/좋아요 취소
  - 댓글 관리
    - 게시글의 댓글 불러오기, 작성/수정/삭제
    - 대댓글은 지원하지 않습니다.
  - 검색
    - 해쉬태그 리스트 검색
    - 사용자 이름 검색

## 프로젝트의 기술 스택

- Language
  - Java (11)
- WAS + WS
  - Spring boot(Tomcat, Java)
- DB
  - Vendor
    - MariaDB
  - DB Interface
    - JPA(Hibernate) + Spring Data
- Storage
  - AWS S3
    - For Test → s3mock
- Authentication
  - JWT
    - 별도의 인증 서버 없이 Spring Security로 구현하여 WAS에 내장
- Test
  - Junit5 + Mockito

## 실행 방법

실행 방법은 source code, Docker 총 2가지를 지원한다.

### 시작하기 앞서

모든 방법을 사용하기 이전에 환경변수 설정을 해야한다. 아래와 같은 환경 변수를 추가한다. Soure Code는 JVM이 실행되는 환경에, 그리고 Docker는 container 실행 옵션으로 환경변수를 설정한다.

- 필수 입력
  - ACCESS_TOKEN_KEY
    - access token 발급용 secret key
    - 임의 값 입력
  - REFRESH_TOKEN_KEY
    - refresh token 발급용 secret key
    - 임의 값 입력
  - DB_URL
    - DB 연결 URL
  - DB_PORT
    - DB 연결 PORT
  - DB_USE
    - database 이름
  - DB_ID
    - database 사용자 ID
  - DB_PASSWORD
    - database 사용자 PASSWORD
  - MAIL_USERNAME
    - 회원 인증 메일 전송을 위한 Gmail 계정 ID
  - MAIL_PASSWORD
    - 회원 인증 메일 전송을 위한 Gmail 계정 비밀 번호
- 선택 입력
  - ACCESS_TOKEN_VALID_TIME
    - access token 만료 시간
    - millisecond 단위
    - 기본 값 1일
  - REFRESH_TOKEN_VALID_TIME
    - refresh token 만료 시간
    - millisecond 단위
    - 기본 값 5일

### Source Code

```bash
git clone https://github.com/zoooo-hs/instagram-clone-be
cd instagram-clone-be
./gradlew bootRun
```

### Docker

```bash
docker run --name instagram-clone -e DB_URL=localhost ... \ #환경 변수 설정 ... 
	-p 8080:8080 \	
	dogfooter/instagram-clone-be:1.0.1
```

## 변경사항 및 TODO
- [2022/03/02 (1.0.1 릴리즈)](https://dogfooter219.notion.site/1-0-1-91a10bbc10e741fc9f57c602d47a4e7d)
- [2022/02/21 (1.0.0 릴리즈)](https://dogfooter219.notion.site/1-0-0-e4307c1f5b1c4b5baf0d9754dc442284)
- [2022/02/12](https://dogfooter219.notion.site/2022-02-3-3c0bd58d436a462b94880dcf3a366e33)
- [2022/02/08](https://dogfooter219.notion.site/2022-02-2-b66133f5680a4094bba04662b2975ad8)
- [2022/02/01](https://dogfooter219.notion.site/2022-02-1-7ebcb5300811407da8f3bd8dc6c13490)
- [2022/01/20](https://dogfooter219.notion.site/2022-01-3-4-74f4f6d709d942e0a14e6dc5d587ae4a)
