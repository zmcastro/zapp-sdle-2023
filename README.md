# ZAPP - FEUP SDLE Project 2023/24

| Name             | Number    | 
| ---------------- | --------- | 
| António Oliveira Santos   | 202008004 |
| José Maria Castro         | 202006963 | 
| Pedro Miguel Nunes        | 202004714 |
| Pedro Alexandre Silva     | 202004985 | 

## How to run

In order to have all of the different components of the project interact nicely we are using docker composer, as such to run only the following commands are needed.

```sh
docker compose build && docker compose up
```

## Some limitations

We are currently only exposing a maximum of 5 endpoints from our  Shopping List Services in ``docker-compose.yml`` (in the cloud service) and on the NGINX config, ``nginx.conf`` (in the upstream backends).

Scaling the number of database nodes is also limited by disk space.