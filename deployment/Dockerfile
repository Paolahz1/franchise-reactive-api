FROM eclipse-temurin:25-jdk-alpine
VOLUME /tmp

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY *.jar /app/ServiceFranchise.jar

ENV JAVA_OPTS="-Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE=aws

# Change ownership of the app directory
RUN chown -R appuser:appgroup /app

WORKDIR /app

# Switch to non-root user
USER appuser

EXPOSE 8080

ENTRYPOINT ["/bin/sh", "-c", "/opt/java/openjdk/bin/java $JAVA_OPTS -jar /app/ServiceFranchise.jar"]
