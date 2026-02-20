# Use official Maven + JDK 17 image to build WAR
FROM maven:3.9.12-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build WAR
RUN mvn clean package -DskipTests

# Use Tomcat image to deploy WAR
FROM tomcat:10.1.10-jdk17
WORKDIR /usr/local/tomcat/webapps

# Copy WAR from build stage
COPY --from=build /app/target/HOOT.war .

EXPOSE 8080

CMD ["catalina.sh", "run"]
