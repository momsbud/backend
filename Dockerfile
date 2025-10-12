FROM eclipse-temurin:21-jre-alpine
WORKDIR /opt/app
ENV TZ=Asia/Kolkata
COPY target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=5s --retries=10 CMD wget -qO- http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java","-XX:+UseZGC","-XX:MaxRAMPercentage=75","-jar","/opt/app/app.jar"]
