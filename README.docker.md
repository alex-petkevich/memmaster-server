# Docker Compose setup

This setup runs:
- `mysql` (MySQL 8.4)
- `memmaster-server` (Spring Boot)
- `memmaster-client` (Angular app served by nginx)

## External upload storage

The server stores uploaded/static files in:
- container path: `/opt/memmaster/data/`
- host path: `${MEMMASTER_UPLOADS_PATH:-./data/uploads}`

By default, uploads are persisted in `./data/uploads` next to `docker-compose.yml`.
You can override this with an absolute host path:

```bash
set MEMMASTER_UPLOADS_PATH=C:/memmaster/uploads
```

## Run

```bash
docker compose -f docker-compose.yml up --build -d
```

App URLs:
- Client: `http://localhost:4200`
- Server API (direct): `http://localhost:8383/api`
- MySQL: `localhost:3306`

## Stop

```bash
docker compose -f docker-compose.yml down
```

