# PINP backend ![workflow](https://github.com/PI-Navarro-Pinero/back-end/actions/workflows/ci.yml/badge.svg) ![Coverage](.github/badges/jacoco.svg)


# Local environment

## Pre-requisites

- Docker Engine: [Windows](https://docs.docker.com/docker-for-windows/install/), [MacOS](https://docs.docker.com/docker-for-mac/install/) y [Linux](https://docs.docker.com/engine/install/#server);
- docker-compose: [Windows](https://docs.docker.com/compose/install/), [MacOs](https://docs.docker.com/compose/install/),  y [Linux](https://docs.docker.com/compose/install/).


## Environment

The project needs a working `.env` file to be able to start. There is a default file named 'env.example' which you can use as an example.
So you can create your environment file for development with:

```bash
cp env.example .env
```

## Running service

<!-- If you have already set up your local environment and have a `.env` file with the development variables set, this command builds the image `backend:dev` if not exists and starts the service `backend`: -->

This command builds the image `backend:dev` if not exists and starts the service `backend`:

```shell
$ docker-compose up -d
```
Load the URL http://localhost:8080/api/ into your browser.

## How to rebuild the docker image

In case you want the changes you have been testing to remain permanently in a new docker image, you can rebuild the image by running this command:

```shell
$ docker-compose build
$ docker-compose up -d
```

After the build, you can remove the previous image with:

```shell
$ docker image prune -f
```

## Container shell access 

The `docker exec` command allows you to run commands inside a Docker container. The following command line will give you a bash shell inside your containers: `docker exec -it <container_name> <command>`. In this service, you should type:

#### Example
```shell
$ docker exec -it backend bash
```

## Viewing containers State

```shell
$ docker-compose ps
     
```

## Command-line reference

- `docker-compose down`: Stops containers and removes containers created by _docker-compose up_.
- `docker-compose stop`: Stops services in dependency order.
- `docker-compose ps -a`: Lists all containers.

For a full reference about docker-compose visit the [compose-reference](https://docs.docker.com/compose/reference/).

###  Cleanup 

- `docker system prune`: Remove unused data.
- `docker rmi $(docker image ls -q)`: Remove all docker images.