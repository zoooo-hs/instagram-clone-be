# Instagram Clone Project (Backend)

![Generic badge](https://img.shields.io/badge/version-1.0.1-green.svg)

## 목차

1. [프로젝트의 목적](#프로젝트의-목적)
2. [프로젝트의 기능](#프로젝트의-기능)
3. [프로젝트의 기술 스택](#프로젝트의-기술-스택)
1. [시작하기 앞서](#시작하기-앞서)
4. [실행 방법](#실행-방법)
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

## 시작하기 앞서

시작하기 앞서 다음과 같은 절차를 거쳐야한다.

1. S3 Bucket 생성
2. YAML 수정
3. Database 환경변수 수정

본 문서에선 *1. S3 Bucket 생성* 에 대해서는 다루지 않는다. S3가 생성되었다는 전제하에 필요한 YAML 수정 및 환경변수 수정에 대해 설명한다.

### YAML 수정
모든 방법을 사용하기 이전에 application-xxx.yaml을 수정하거나 환경변수를 설정해야한다. 아래와 같이 수정한다.
- application-aws.yaml
  ```yaml
  cloud:
    aws:
      s3:
        bucket: # s3 bucket name
      region:
        static: # aws region
  ```
- application-credentials.yaml
  ```yaml
  cloud:
    aws:
      credentials:
        accessKey: # aws access key
        secretKey: # aws secret key 
  
  instagram-clone:
    # mail 인증 서비스를 사용하고 싶다면 activation: true, username: gmail id, password: gmail password
    mail:
      activation: ${MAIL_ACTIVATION:false}
      username: ${MAIL_USERNAME}
      password: ${MAIL_PASSWORD}
    jwt:
      access-token:
        key: # access token sign key. 임의의 값 입력
        valid-time: 86400000 # default: 1 day
      refresh-token:
        key: # refresh token sign key. 임의의 값 입력
        valid-time: 432000000 # default: 5 days 
  ```
### Database 환경변수 수정
기본 값이 설정되어 있지만, 수정 가능한 환경 변수.
- DB_ID
  - default: root
  - database 사용자 ID
- DB_PASSWORD
  - default: mariadb
  - database 사용자 PASSWORD
- DB_URL
  - default: localhost
  - DB 연결 URL
- DB_PORT
  - default: 3306
  - DB 연결 PORT
- DB_USE
  - default: instagram-clone
  - database 이름
- DDL_AUTO
  - JPA ddl 자동 생성 여부
  - 기본값 validate
  - 처음 실행시엔 create로 두어야 table 생성
  - 이후 validate로 바꾸는 것을 권장


## 실행 방법

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
