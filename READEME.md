## 요구사항 1 - index.html 응답하기
- 요청 URL: http://localhost:8080/index.html
```
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accpet: */*
```

## 요구사항 2 - GET 방식으로 회원가입하기
- 요청 URL: http://localhost:8080/user/create?userId=javajigi&password=123&name=changwon
- http://localhost:8080/user.form.html 로 이동해야 한다.
- 요청 URL 에서 유저 정보를 파싱하여 User 클래스에 담는다.

## 요구사항 3 - POST 방식으로 회원가입하기
- http://localhost:8080/user/form.html 파일의 form 태그 method를 GET에서 POST로 수정한다.
- POST 로 데이터 전송시 전달하는 데이터는 HTTP 본문에 담긴다.
- BufferedReader 에서 본문 데이터는 util.IOUtils 클래스의 readDate() 메소드를 사용한다.
- 본문의 길이는 HTTP 헤더의 Content-Length 의 값이다.

```
POST /user/create HTTP 1.1
Host: localhost:8080
Connection: keep-alive
Content-Length: 59
Content-Type: application/x-www-form-urlencoded
Accept: */*

userId=javajigi&password=password&name=cwpark
```
