# Paste book

Paste book is a service designed for storing pastes. When creating, the user specifies the expiration period 
of the paste, e.g., 10 minutes, 1 day or eternal, and after this period, the paste becomes inaccessible to anyone, 
including the author. The author can also choose an access modifier: public, unlisted or private.

## Supported paste lifetimes

In total, the system supports 6 expiration periods of the paste and each of them has 3 spelling options: ordinal, brief and full

| Expiration period | Ordinal | Brief | Full       |
|-------------------|:-------:|-------|------------|
| Ten minutes       |    0    | 10m   | TenMinutes |
| One Hour          |    1    | 1h    | OneHour    |
| Three hours       |    2    | 3h    | ThreeHours |
| One day           |    3    | 1d    | OneDay     |
| One week          |    4    | 1w    | OneWeek    |
| Eternal           |    5    | N     | Eternal    |

## Supported access modifiers for paste

| AccessModifier | Ordinal | Text     | Description                               |
|----------------|:-------:|----------|-------------------------------------------|
| Public         |    0    | PUBLIC   | The paste is available to any user        |
| Unlisted       |    1    | UNLISTED | Paste is available at the link            |
| Private        |    2    | PRIVATE  | The paste is available only to the author |

## Endpoints

| Method | Path                       | Description                       | Authentication is required | 
|--------|----------------------------|-----------------------------------|:--------------------------:|
| POST   | /api/v1/register           | Register new user                 |             ❌              |
| POST   | /api/v1/register           | Register new user                 |             ❌              |
| GET    | /api/v1/pastes/hash/{hash} | Get existing paste by hash        |             ✔              |
| POST   | /api/v1/pastes/new         | Create new paste                  |             ✔              |
| GET    | /api/v1/pastes/my          | Get all the user's pastes         |             ✔              |
| GET    | /api/v1/pastes/latest      | Get the latest 10 uploaded pastes |             ✔              |
| GET    | /api/v1/pastes/search      | Search for pastes                 |             ✔              |

## Used technology

1. Spring boot
2. Postgresql
3. Java JWT
4. JUnit
5. Hamcrest
6. ModelMapper
7. Lombok

# Let's try it out

1. Build the project
```text
mvn clean install -DskipTests 
```

2. Run the service with docker compose
```text
docker-compose up 
```
