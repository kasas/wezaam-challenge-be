mvn clean install package
docker build -t cloudpay/wezaam-challenge .
docker-compose up --build
