# Instagram Clone Project (Backend)

퇴근하고 뭐라도 해보려고 한다. 
TDD 공부도 하고 Spring 프로젝트를 계속 손에 익혀본다.

## 시작하기 앞서
- 환경 변수 추가
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
  - 선택 입력
    - ACCESS_TOKEN_VALID_TIME
      - access token 만료 시간
      - millisecond 단위
      - 기본 값 1일
    - REFRESH_TOKEN_VALID_TIME
      - refresh token 만료 시간
      - millisecond 단위
      - 기본 값 5일
- 파일 추가 및 변경
    - resources/application-aws.yaml
        ```yaml
        cloud:
            aws:
              s3:
                bucket: <S3_BUCKET_NAME>
              region:
                static: <S3_BUCKET_REGION>
              stack:
                auto: false
              credentials:
                instanceProfile: true
        ```
    - resources/application-credential.yaml
        ```yaml
        cloud:
              aws:
                credentials:
                  accessKey: <S3_IAM_ACCESS_KEY>
                  secretKey: <S3_IAM_SECRET_KEY> 
        ```

## 변경사항 및 TODO
- [2022/02/12](https://dogfooter219.notion.site/2022-02-3-3c0bd58d436a462b94880dcf3a366e33)
- [2022/02/08](https://dogfooter219.notion.site/2022-02-2-b66133f5680a4094bba04662b2975ad8)
- [2022/02/01](https://dogfooter219.notion.site/2022-02-1-7ebcb5300811407da8f3bd8dc6c13490)
- [2022/01/20](https://dogfooter219.notion.site/2022-01-3-4-74f4f6d709d942e0a14e6dc5d587ae4a)
