# Culinary Connect - Backend

## Container guide
1. Install [Podman](https://podman.io/docs/installation)
2. Clone repo
3. Setup **Podman Compose**
```cmd
podman compose up
```

## Dev guide
1. Start postgresql container
```cmd
 podman run --name culcon_db_dev -p 5432:5432 -e POSTGRES_USER=culcon -e POSTGRES_DB=culcon_user -e POSTGRES_PASSWORD=culcon  docker.io/postgres
```
