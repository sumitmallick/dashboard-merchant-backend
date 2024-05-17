docker system prune -f
docker network create -d bridge payment_network
docker network create -d overlay --attachable payment_backend_network
docker-compose down
docker-compose build
docker-compose up -d
# echo $PATH