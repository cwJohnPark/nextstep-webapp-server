요구사항 1 - index.html 응답하기
- 요청 URL: http://localhost:8080/index.html
```
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accpet: */*
```

요구사항 2 - GET 방식으로 회원가입하기
- 요청 URL: http://localhost:8080/user/create?userId=javajigi&password=123&name=changwon
- http://localhost:8080/user.form.html 로 이동해야 한다.
- 요청 URL 에서 유저 정보를 파싱하여 User 클래스에 담는다.