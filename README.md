# 💳 Система управления банковскими картами

## 📦 Предварительные требования
- Docker
- Docker Compose

## ▶️ Запуск проекта
1. Выполните в корне проекта:

```bash
docker compose up --build
   ```

2. После запуска сервисы будут доступны:

    * Spring Boot приложение: [http://localhost:8080](http://localhost:8080)
    * PostgreSQL: `localhost:5432` (логин/пароль `postgres/postgres`)

---

## 🧾 Примечания

* Все миграции БД выполняются автоматически через Liquibase (`src/main/resources/db/migration`).
* Есть начальные данные для карт и пользователей.
---
    * Админ - username: admin | password: pass
    * Пользователь 1 - username: user1 | password: pass
    * Пользователь 2 - username: user2 | password: pass
---
* API-документация доступна через Swagger UI: [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui). Там же можно протестировать эндпоинты.
* Для остановки контейнеров:

  ```bash
  docker compose down
  ```

