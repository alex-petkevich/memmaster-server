# MemMaster Server app

A small and free open source application for memorizing various terms, words, definitions. Just go to the browser part of the application, create and modify dictionaries - manually or by importing from a file. After that, the words will be shown randomly in the selected folders until you mark the card as "learned".

Repository of the server part of the application. Requirements:
 - Java 21
 - MariaDB base (MySQL)


Use ./mvnw to build the application.

## Docker Compose quick start

The Docker setup in this folder starts 3 containers:
- `mysql` (database)
- `memmaster-server` (Spring Boot API)
- `memmaster-client` (Angular app via nginx)

From `memmaster-server` directory:

```bash
docker compose -f docker-compose.yml up --build -d
```

### External storage for uploads

Static uploads are stored outside the server container via bind mount.

Default host path (relative to this folder):
- `./data/uploads`

Optional custom host path:

```bash
set MEMMASTER_UPLOADS_PATH=C:/memmaster/uploads
docker compose -f docker-compose.yml up --build -d
```

### Access URLs

- Client: `http://localhost:4200`
- Server API: `http://localhost:8383/api`
- MySQL: `localhost:3306`

### Stop

```bash
docker compose -f docker-compose.yml down
```

For more Docker details, see `README.docker.md`.
