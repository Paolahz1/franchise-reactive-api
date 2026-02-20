# ============================================
# Stage 1: Builder (Compilación con Gradle)
# ============================================
FROM gradle:8.14-jdk21-alpine AS builder

WORKDIR /build

# Copiar archivos de configuración de Gradle
COPY build.gradle settings.gradle gradle.properties lombok.config main.gradle ./
COPY gradle ./gradle
COPY gradlew ./

# Copiar código fuente
COPY domain ./domain
COPY infrastructure ./infrastructure
COPY applications ./applications

# Compilar aplicación (sin tests y sin validación de estructura para build más rápido)
RUN gradle :app-service:build -x test -x validateStructure --no-daemon

# ============================================
# Stage 2: Runtime (Imagen final optimizada)
# ============================================
FROM ibm-semeru-runtimes:open-21-jre

LABEL maintainer="bancolombia"
LABEL description="Franchise Management System - Reactive API"
LABEL version="1.0.0"

VOLUME /tmp

# Instalar dependencias mínimas y crear usuario no-root
RUN apt-get update && apt-get install -y --no-install-recommends wget && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

# Copiar JAR desde el builder (Stage 1)
COPY --from=builder --chown=appuser:appgroup /build/applications/app-service/build/libs/*.jar app.jar

# Configuración de JVM optimizada para contenedores
ENV JAVA_OPTS="-Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=70 \
    -Djava.security.egd=file:/dev/./urandom"

# Exponer puerto
EXPOSE 8080

# Cambiar a usuario no-root por seguridad
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Entrypoint
ENTRYPOINT ["/bin/sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]