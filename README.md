# java-explore-with-me

Pull request:

https://github.com/Kazannik/java-explore-with-me-for-viktor/pull/2

| Сервисы:                  | ports (внешний: внутренний): | path:                                
|---------------------------|:----------------------------:|---------------------------------------------|                    
| ewm-gateway               |  8080:8080<br/> 9090:9090;   | http://gateway:8080<br/>http://gateway:9090 |
| ewm-main-service          |          6540:8080;          | http://ewm-main-service:8080                |
| postgresql-ewm-main-db    |          6541:5432;          | http://main-db:5432                         |
| ewm-stats-service         |          6542:8080;          | http://ewm-stats-service:8080               |                  
| postgresql-ewm-stats-db   |          6543:5432;          | http://stat-db:5432                         |
| ewm-feature-service       |          6544:8080;          | http://ewm-feature-service:8080             |                                     
| postgresql-ewm-feature-db |          6545:5432;          | http://feature-db:5432                      |



### Внешний API к Gateway:
#### Создать комментарий:
- POST /users/{userId}/comments
#### Удалить комментарий:
- DELETE /users/{userId}/comments/{commentId}
- DELETE /admin/comments/{commentId}
#### Получить комментарий по его индексу:
- GET  /comments/{commentId}
- GET  /users/{userId}/comments/{id}
- GET  /admin/comments/{id}
#### Поиск комментариев по параметрам:
- GET  /comments?text={text}&
- GET  /users/{userId}/comments?text={text}
- GET  /admin/comments?text={text}&
#### Правка комментария по его индексу:
- PUT /user/{userId}/comments/{id}
#### Публикация комментария:
- PATCH /admin/comments/{id}/publish
#### Отклонить публикацию комментария:
- PATCH /admin/comments/{id}/reject
#### Поставить лайк комментарию:
- PATCH /user/{userId}/comments/{id}/like
#### Отменить предыдущую оценку комментария:
- PATCH /user/{userId}/comments/{id}/unknown
#### Поставить дизлайк комментарию:
- PATCH /user/{userId}/comments/{id}/dislike

### Внутренний API к Feature Service:
#### Создать комментарий:
- POST /comments
#### Удалить комментарий:
- DELETE /comments/{commentId} (HEADERS user/admin, userId)
#### Получить комментарий по его индексу:
- GET  /comments/{id} (HEADERS public/user/admin, userId)
#### Поиск комментариев по параметрам:
- GET  /comments?text={text}& (HEADERS public/user/admin, userId)
#### Правка комментария по его индексу:
- PUT /comments/{id}
#### Публикация комментария:
- PATCH /comments/{id}/publish
#### Отклонить публикацию комментария:
- PATCH /comments/{id}/reject
#### Поставить лайк комментарию:
- PATCH /comments/{id}/like
#### Отменить предыдущую оценку комментария:
- PATCH /comments/{id}/unknown
#### Поставить дизлайк комментарию:
- PATCH /comments/{id}/dislike

### Внутренний API к Main Service:
#### Получить краткие служебные сведения о событии:
- POST /events/{eventId}/sys