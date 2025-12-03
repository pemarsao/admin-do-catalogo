# criar as docker networks necessárias
docker network create adm_videos_services

# Criar as pastas com permissões corretas
mkdir -m 777 .docker
mkdir -m 777 .docker/keycloak

docker compose -f services/docker-compose.yml up -d
docker compose -f app/docker-compose.yml up -d

echo "Inicializando os containers..."
sleep 20