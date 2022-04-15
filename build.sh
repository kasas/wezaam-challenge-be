mvn clean install package
docker build -t cloudpay/wezaam-challenge .
docker network create resolute  
docker-compose up --build
