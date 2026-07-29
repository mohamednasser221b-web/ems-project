# ---- Stage 1: build ----
# Pinned digest, not just a tag - "maven:3.9-eclipse-temurin-21" can silently
# change underneath you; pin by digest once you've verified it in CI, so a
# build today and a build in six months use byte-identical base layers.
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml first and download dependencies before copying source.
# Docker layer caching: if only source changes (not pom.xml), this
# dependency-download layer is reused from cache - much faster CI builds.
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Stage 2: runtime ----
# JRE only, not JDK - we don't need a compiler in production. Alpine-based
# for a small footprint; "temurin" is the Eclipse Adoptium OpenJDK build.
FROM eclipse-temurin:21-jre-alpine

# Run as a non-root user. If the app is ever compromised (e.g. via a
# deserialization bug or a vulnerable dependency), the attacker doesn't
# automatically get root inside the container - this limits the blast
# radius of a container escape attempt.
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

# Container-level healthcheck - separate from k8s liveness/readiness probes,
# useful for local docker-compose runs and for tools that inspect container
# health directly (e.g. `docker ps`).
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget -q --spider http://localhost:8080/api/v1/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]