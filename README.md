# Franchise Management System

Sistema de gestión de franquicias construido con Clean Architecture, programación funcional reactiva y desplegado en AWS.

## 📋 Tabla de Contenidos

- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Pre-requisitos](#pre-requisitos)
- [Configuración Local](#configuración-local)
- [Ejecución Local](#ejecución-local)
- [API Endpoints](#api-endpoints)
- [Infraestructura AWS](#infraestructura-aws)
- [Base de Datos](#base-de-datos)

## 🚀 Tecnologías

### Backend
- **Java 21** - Lenguaje de programación
- **Spring Boot 4.0.1** - Framework base
- **Spring WebFlux** - Programación reactiva
- **R2DBC MySQL** - Driver reactivo para MySQL
- **Reactor Core 3.8.1** - Reactive Streams
- **Lombok** - Reducción de boilerplate
- **MapStruct** - Mapeo de objetos
- **SpringDoc OpenAPI 2.7.0** - Documentación API (Swagger)

### Build & Testing
- **Gradle 8.x** - Sistema de construcción
- **JUnit 5** - Testing

### Infrastructure
- **Terraform** - Infrastructure as Code
- **AWS RDS MySQL 8.0.45** - Base de datos
- **AWS VPC** - Red privada
- **AWS ECR** - Registro de contenedores Docker
- **AWS EC2** (Bastion) - Acceso seguro a base de datos
- **AWS Systems Manager** - Port forwarding sin SSH


### Principios Aplicados

- ✅ **Clean Architecture** - Separación de responsabilidades
- ✅ **Hexagonal Architecture** - Puertos y adaptadores
- ✅ **Functional Reactive Programming** - Streams reactivos con Mono/Flux
- ✅ **SOLID Principles** - Diseño orientado a objetos
- ✅ **Lazy Evaluation** - Evaluación diferida con `Mono.defer()` y `Mono.fromSupplier()`
- ✅ **Immutability** - Uso de `final` y Records
- ✅ **Dependency Inversion** - Interfaces en el dominio


## 📦 Pre-requisitos

### Para desarrollo local

1. **Java 21**
   ```bash
   brew install openjdk@21
   ```

2. **Gradle 8.x** (incluido con wrapper)
   ```bash
   ./gradlew --version
   ```

3. **AWS CLI** (para conectarse a RDS)
   ```bash
   brew install awscli
   aws configure
   ```

4. **Session Manager Plugin** (para túnel a base de datos)
   ```bash
   brew install --cask session-manager-plugin
   ```

5. **MySQL Client** (opcional, para conectarse manualmente)
   ```bash
   brew install mysql-client
   ```

### Para despliegue en AWS

6. **Terraform**
   ```bash
   brew install terraform
   ```

7. **Docker** (para construir imágenes)
   ```bash
   brew install --cask docker
   ```

## ⚙️ Configuración Local

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd Service-franchise
```

### 2. Configurar conexión a base de datos

La aplicación se conecta a **AWS RDS MySQL** a través de un túnel SSH via Session Manager.

**Opción A: Usar túnel automático (recomendado)**

En una terminal separada, ejecuta:

```bash
cd infra
./connect-mysql.sh
```

Esto crea un túnel desde `localhost:3307` hacia RDS.

**Opción B: Base de datos local (para desarrollo aislado)**

Si prefieres usar MySQL local:

```bash
# Iniciar MySQL local
docker run -d \
  --name mysql-franchise \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=franchises_db \
  -p 3306:3306 \
  mysql:8.0

# Crear schema
docker exec -i mysql-franchise mysql -uroot -proot franchises_db < applications/app-service/src/main/resources/schema.sql
```

Luego modifica `applications/app-service/src/main/resources/application.yml`:

```yaml
spring:
  r2dbc:
    url: r2dbc:mysql://localhost:3306/franchises_db
    username: root
    password: root
```

### 3. Variables de entorno (opcional)

Puedes sobrescribir configuración con variables:

```bash
export DB_HOST=localhost
export DB_PORT=3307
export DB_NAME=franchises_db
export DB_USER=admin
export DB_PASSWORD=ChangeThisPassword123!
```

## 🏃 Ejecución Local

### Compilar el proyecto

```bash
./gradlew clean build -x test
```

### Ejecutar la aplicación

**Opción 1: Con Gradle**

```bash
./gradlew :applications:app-service:bootRun
```

**Opción 2: Con JAR**

```bash
java -jar applications/app-service/build/libs/app-service.jar
```

La aplicación estará disponible en:
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/webjars/swagger-ui/index.html

### Verificar que funciona

```bash
# Health check
curl http://localhost:8080/actuator/health

# Crear una franquicia
curl -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Starbucks"}'
```

## 📡 API Endpoints

### Franchises

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/franchises` | Crear franquicia |
| PATCH | `/api/franchises/{franchiseId}/name` | Actualizar nombre de franquicia |
| GET | `/api/franchises/{franchiseId}/max-stock-products` | Obtener productos con mayor stock por sucursal |

### Branches

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/branches` | Agregar sucursal a franquicia |
| PATCH | `/api/branches/{branchId}/name` | Actualizar nombre de sucursal |

### Products

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/products` | Agregar producto a sucursal |
| DELETE | `/api/products/{productId}` | Eliminar producto |
| PATCH | `/api/products/{productId}/stock` | Actualizar stock de producto |
| PATCH | `/api/products/{productId}/name` | Actualizar nombre de producto |

### Ejemplos de uso

**Crear franquicia:**
```bash
curl -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "McDonald'\''s"}'
```

**Agregar sucursal:**
```bash
curl -X POST http://localhost:8080/api/branches \
  -H "Content-Type: application/json" \
  -d '{
    "franchiseId": 1,
    "name": "Sucursal Centro"
  }'
```

**Agregar producto:**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "branchId": 1,
    "name": "Big Mac",
    "stock": 50
  }'
```

**Actualizar stock:**
```bash
curl -X PATCH http://localhost:8080/api/products/1/stock \
  -H "Content-Type: application/json" \
  -d '{"stock": 100}'
```

**Obtener productos con mayor stock:**
```bash
curl http://localhost:8080/api/franchises/1/max-stock-products
```

Documentación completa en: http://localhost:8080/webjars/swagger-ui/index.html

## 🐳 Docker

El proyecto está completamente dockerizado para facilitar el despliegue en cualquier entorno.

### Build local

**Multi-stage build (recomendado):**
```bash
cd deployment
./build-image.sh latest
```

Esto crea una imagen optimizada usando multi-stage build (compile + runtime).

**Build manual (si ya tienes el JAR):**
```bash
# 1. Compilar JAR
./gradlew :applications:app-service:build -x test

# 2. Build imagen
docker build -f deployment/Dockerfile -t franchise-service:latest .
```

### Ejecutar con Docker

**Opción 1: Solo la app (requiere MySQL externo)**
```bash
docker run -p 8080:8080 \
  -e SPRING_R2DBC_URL=r2dbc:mysql://host.docker.internal:3307/franchises_db \
  -e SPRING_R2DBC_USERNAME=admin \
  -e SPRING_R2DBC_PASSWORD=ChangeThisPassword123! \
  franchise-service:latest
```

**Opción 2: App + MySQL con docker-compose (desarrollo local)**
```bash
cd deployment
docker-compose up -d
```

Esto levanta:
- **franchise-api**: La aplicación en http://localhost:8080
- **mysql**: Base de datos MySQL con schema pre-cargado

Para detener:
```bash
docker-compose down
```

### Push a AWS ECR

**Proceso completo (build + push):**
```bash
cd deployment
./deploy-docker.sh v1.0.0
```

**Paso a paso manual:**
```bash
# 1. Build
./build-image.sh v1.0.0

# 2. Push a ECR
./push-to-ecr.sh v1.0.0 us-east-1
```

**Verificar imagen en ECR:**
```bash
aws ecr describe-images \
  --repository-name franchise-service \
  --region us-east-1
```

### Características del Dockerfile

- ✅ **Multi-stage build** - Imagen final solo con JRE (más pequeña)
- ✅ **Usuario no-root** - Seguridad mejorada
- ✅ **Health check** integrado
- ✅ **JVM optimizado para contenedores** (MaxRAMPercentage, G1GC)
- ✅ **Alpine Linux** - Imagen base ligera

**Tamaño de imagen:**
- Stage 1 (builder): ~800MB (con Gradle y JDK)
- Stage 2 (final): ~200MB (solo JRE + app)

## ☁️ Infraestructura AWS

La infraestructura está completamente definida como código con Terraform.

### Recursos desplegados

- **VPC** con subnets públicas y privadas
- **RDS MySQL 8.0.45** en subnet privada (sin acceso público)
- **NAT Gateway** para salida a internet desde subnets privadas
- **EC2 Bastion** con Systems Manager (sin SSH keys)
- **ECR Repository** para imágenes Docker
- **S3 + DynamoDB** para remote state de Terraform

### Desplegar infraestructura

```bash
cd infra

# Inicializar Terraform
terraform init

# Ver cambios
terraform plan

# Aplicar cambios
terraform apply
```

### Destruir infraestructura

```bash
cd infra
terraform destroy
```

**⚠️ ADVERTENCIA**: Esto eliminará todos los recursos y la base de datos.

## 🗄️ Base de Datos

### Schema

La base de datos tiene 3 tablas principales:

```sql
franchises (id, name, created_at, updated_at)
    ↓
branches (id, name, franchise_id, created_at, updated_at)
    ↓
products (id, name, stock, branch_id, created_at, updated_at)
```

### Conectarse a RDS MySQL

**Desde terminal local:**

```bash
# Terminal 1: Iniciar túnel
cd infra
./connect-mysql.sh

# Terminal 2: Conectarse con MySQL client
mysql -h 127.0.0.1 -P 3307 -u admin -p
# Password: ChangeThisPassword123!
```

**Desde la aplicación:**

La aplicación se conecta automáticamente usando la configuración en `application.yml`:

```yaml
spring:
  r2dbc:
    url: r2dbc:mysql://localhost:3307/franchises_db
```

### Inicializar schema

El schema se crea automáticamente al ejecutar:

```bash
# Conectarse a MySQL (con túnel activo)
mysql -h 127.0.0.1 -P 3307 -u admin -p < applications/app-service/src/main/resources/schema.sql
```

## 🧪 Testing

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests con reporte
./gradlew test jacocoTestReport

# Ver reporte de cobertura
open build/reports/jacoco/test/html/index.html
```

## 📝 Notas Importantes

### Programación Funcional Reactiva

El proyecto usa programación reactiva con Reactor:

```java
// ✅ Buena práctica: Lazy evaluation
Mono.defer(() -> Mono.error(...))
Mono.fromSupplier(() -> pathVariable)

// ✅ Composición de operadores
repository.findById(id)
    .switchIfEmpty(Mono.defer(() -> Mono.error(...)))
    .flatMap(entity -> repository.update(...))
    .then(repository.findById(id))
```

### R2DBC vs JPA

Este proyecto usa **R2DBC** (reactivo), no JPA:

```java
// ❌ NO usar anotaciones JPA
@Entity  // NO
@Table   // NO

// ✅ Usar anotaciones R2DBC
@Table("franchises")
@Id
private Long id;
```

### @Param requerido en queries

R2DBC requiere `@Param` para mapeo correcto:

```java
@Query("UPDATE franchises SET name = :name WHERE id = :id")
Mono<Integer> updateName(@Param("id") Long id, @Param("name") String name);
```

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama (`git checkout -b feature/amazing-feature`)
3. Commit cambios (`git commit -m 'feat: add amazing feature'`)
4. Push a la rama (`git push origin feature/amazing-feature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto es privado y confidencial.

---

**Desarrollado con ☕ y Clean Architecture**
