# 📚 Book Exchange Platform

Book Exchange — это современная платформа для обмена книгами между пользователями. Проект построен на микросервисной архитектуре, включает отдельный сервис авторизации, основной сервис на Java Spring, SPA на React и полностью docker-ориентирован.

---

## 🛠️ Стек технологий
- **Backend:** Java 17, Spring Boot, Spring Data JPA, Spring Security, PostgreSQL, Flyway, Gradle, Swagger/OpenAPI
- **Auth Service:** Go (отдельный сервис, JWT, взаимодействие по API)
- **Frontend:** React SPA (TypeScript, MUI, qrcode.react, react-qr-reader)
- **CI/CD:** GitLab CI, Docker, docker-compose

---

## 🚀 Основные возможности
- Регистрация и аутентификация пользователей (Go-сервис, JWT)
- CRUD для книг (добавление, редактирование, удаление, фильтрация)
- Обмен книгами между пользователями (заявки, подтверждение, отклонение)
- История обменов
- Роли пользователей (USER, ADMIN), админ-панель
- Оставление отзывов и выставление рейтингов после обмена
- Генерация и сканирование QR-кодов для подтверждения обмена
- Swagger UI для тестирования и изучения API
- Полное покрытие тестами (unit и интеграционные)
- Docker-окружение для быстрого запуска всех сервисов

---

## 💡 Killer Features
- **Отзывы и рейтинги:** Оставляйте отзывы и оценки после обмена, просматривайте средний рейтинг пользователя.
- **QR-коды:** Подтверждение обмена через сканирование QR-кода.
- **История обменов:** Просматривайте все свои обмены и отзывы.
- **Web Push (опционально):** Получайте уведомления о новых заявках и событиях (если включено).

---

## ⚡ Быстрый старт

### 1. Клонируйте репозиторий
```bash
git clone https://github.com/your-org/book-exchange.git
cd book-exchange
```

### 2. Запустите все сервисы через Docker Compose
```bash
docker compose up --build
```
- Backend: `http://localhost:8080`
- Frontend: `http://localhost:3000`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Auth Service: `http://localhost:8081`
- PostgreSQL: `localhost:5432` (логин/пароль: postgres/postgres)

### 3. Тесты
- Unit- и интеграционные тесты для backend:
  ```bash
  cd backend/book-service
  ./gradlew test
  ```
- Unit-тесты запускаются автоматически в CI (см. `.gitlab-ci.yml`).

---

## 📖 Документация API
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🗂️ Структура репозитория
```
book-exchange/
├── backend/
│   └── book-service/      # Java Spring сервис (основной)
├── auth-service/          # Go-сервис авторизации
├── frontend/              # React SPA
├── docker-compose.yml     # Запуск всех сервисов
├── README.md
└── .gitlab-ci.yml         # CI/CD pipeline
```

---

## 🔑 Авторизация
- POST `/auth/register` (Go) — регистрация
- POST `/auth/login` — вход (вернёт JWT)
- JWT подставлять в запросах к book-service:
  ```
  Authorization: Bearer <твой токен>
  ```

---

## 🧪 Тесты
- Юнит и интеграционные тесты в Spring Boot сервисе (`src/test/...`).
- Автоматически запускаются в GitLab CI/CD.

---

## 📬 Контакты и поддержка
- Вопросы и предложения — через Issues или Pull Requests.

---

**Book Exchange** — обменяйся книгами легко и безопасно!
