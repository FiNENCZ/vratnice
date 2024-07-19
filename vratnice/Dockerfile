FROM eclipse-temurin:17.0.6_10-jre-alpine
WORKDIR /vratnice
ARG ver=0.0.1
ARG branch
ARG commit=0
COPY ./target/vratnice-${ver}${branch}-${commit}.jar vratnice.jar
RUN sh -c 'touch vratnice.jar'
EXPOSE 8080
ENTRYPOINT ["java","-Duser.timezone=Europe/Prague","-Djava.security.egd=file:/dev/./urandom","-jar","portla.jar"]