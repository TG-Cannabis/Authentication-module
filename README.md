# Authentication-module

## Importante:
Favor crear .env en el folder Database, que contenga:

```env
POSTGRES_DB=
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_PORT=
```

Favor crear .env en el folder Authentication que contenga:

```env
POSTGRES_DB=
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_PORT=
POSTGRES_IP=
SPRING_PORT=
```

Buildear por fuera el paquete con:
```bash
    mvn clean package -DskipTests
```
Luego correr el compose de la base de datos y luego el de auth:
```env
    cd Database
    docker compose up -d
    cd ..
    cd Authentication
    docker compose up --build -d
```

las peticiones (exceptuando register y login) requieren un header de authorization de tipo Bearer Token (con el token que retorna login)

