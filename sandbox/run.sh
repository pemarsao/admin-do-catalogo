# criar as docker networks necessárias
docker network create adm_videos_services
docker network create elastic

# Criar as pastas com permissões corretas
sudo chown root app/filebeat/filebeat.docker.yml
mkdir -m 777 .docker
mkdir -m 777 .docker/keycloak
mkdir -m 777 .docker/es01
mkdir -m 777 .docker/filebeat
sudo chmod go-w app/filebeat/filebeat.docker.yml

docker compose -f services/docker-compose.yml up -d
docker compose -f elk/docker-compose.yml up -d
#docker compose -f app/docker-compose.yml up -d

echo "Inicializando os containers..."
sleep 20