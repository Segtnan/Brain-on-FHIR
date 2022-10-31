FROM ubuntu/apache2:2.4-22.04_beta
COPY . .
RUN apt update
RUN apt install openjdk-11-jdk -y
RUN apt install maven -y
EXPOSE 8080
CMD ["mvn", "jetty:run"]
