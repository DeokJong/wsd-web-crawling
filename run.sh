#!/bin/bash

# Spring Boot 이미지 빌드
sudo docker build --network=host -t springboot-image .

# Docker Compose 실행
sudo docker compose  -f "docker-compose.yml" up -d --build postgreSQL redis springboot-container
