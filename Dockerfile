# Stage 1: Build OpenRocket Swing distribution with Gradle
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew installDist --no-daemon -x test && \
    mkdir -p /app/dist && \
    cp -r swing/build/install/*/* /app/dist/ 2>/dev/null || \
    cp -r build/install/*/* /app/dist/

# Stage 2: Web Desktop Environment using Ubuntu + noVNC
FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y \
    openjdk-17-jre \
    xvfb \
    openbox \
    x11vnc \
    novnc \
    websockify \
    supervisor \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy application binaries and supervisor configuration
COPY --from=builder /app/dist /app/
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

RUN chmod +x /app/bin/*

ENV PORT=8080
EXPOSE 8080

CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
