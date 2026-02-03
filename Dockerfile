# Aşama 1: Build Aşaması (Maven ile derleme)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Önce sadece pom.xml'i kopyalayıp bağımlılıkları indiriyoruz (Cache optimizasyonu için)
COPY pom.xml .
RUN mvn dependency:go-offline

# Şimdi kaynak kodları kopyalayıp derliyoruz
COPY src ./src
RUN mvn clean package -DskipTests

# Aşama 2: Çalıştırma Aşaması (Sadece JRE yeterli)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Derlenen jar dosyasını kopyala
COPY --from=build /app/target/*.jar app.jar

# Portu dışarı aç
EXPOSE 8080

# Uygulamayı başlat
ENTRYPOINT ["java", "-jar", "app.jar"]
