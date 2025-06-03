# Stage 1 - custom Maven builder based on eclipse-temurin 21-jdk-alpine
FROM eclipse-temurin:21-jdk-alpine as builder

RUN apk add --no-cache bash procps curl tar openssh-client

ENV MAVEN_HOME=/usr/share/maven

# Copy Maven binaries from official Maven JDK17 image
COPY --from=maven:3.9.9-eclipse-temurin-17 ${MAVEN_HOME} ${MAVEN_HOME}
COPY --from=maven:3.9.9-eclipse-temurin-17 /usr/local/bin/mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
COPY --from=maven:3.9.9-eclipse-temurin-17 /usr/share/maven/ref/settings-docker.xml /usr/share/maven/ref/settings-docker.xml

RUN ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn

ENV MAVEN_CONFIG="/root/.m2"

WORKDIR /app

# Copy project files
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2 - run stage based on eclipse-temurin 21 JRE Alpine
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]