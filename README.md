# Franchise Management System

Sistema de gestión de franquicias construido con el plugin [Scaffold Clean Architecture de Bancolombia](https://bancolombia.github.io/scaffold-clean-architecture/), programación funcional reactiva y desplegado en AWS.

El proyecto fue generado usando el plugin de Gradle `co.com.bancolombia.cleanArchitecture`, que proporciona una estructura base siguiendo los principios de Clean Architecture y Hexagonal Architectura.

## Tabla de Contenidos

- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Pre-requisitos](#pre-requisitos)
- [Configuración Local](#configuración-local)
- [Ejecución Local](#ejecución-local)
- [API Endpoints](#api-endpoints)
- [Docker](#docker)
- [Infraestructura AWS](#infraestructura-aws)
- [Base de Datos](#base-de-datos)
- [Testing](#testing)

## Tecnologías

### Backend
- **Java 21** - Lenguaje de programación
- **Spring Boot 4.0.1** - Framework base
- **Spring WebFlux** - Programación reactiva
- **R2DBC MySQL** - Driver reactivo para MySQL
- **Reactor Core 3.8.1** - Reactive Streams
- **Lombok** - Reducción de boilerplate
- **MapStruct** - Mapeo de objetos
- **SpringDoc OpenAPI 2.7.0** - Documentación API (Swagger)

### Build & Scaffolding
- **Gradle 8.x** - Sistema de construcción
- **[Scaffold Clean Architecture Plugin](https://bancolombia.github.io/scaffold-clean-architecture/) 4.0.5** - Plugin de Bancolombia para generación de estructura Clean Architecture
- **JUnit 5** - Testing


### Infrastructure
- **Terraform** - Infrastructure as Code
- **AWS RDS MySQL 8.0.45** - Base de datos
- **AWS VPC** - Red privada
- **AWS ECR** - Registro de contenedores Docker
- **AWS EC2** (Bastion) - Acceso seguro a base de datos
- **AWS Systems Manager** - Port forwarding sin SSH


### Principios Aplicados

-  **Clean Architecture** - Separación de responsabilidades
-  **Hexagonal Architecture** - Puertos y adaptadores
-  **Functional Reactive Programming** - Streams reactivos con Mono/Flux
-  **SOLID Principles** - Diseño orientado a objetos
-  **Lazy Evaluation** - Evaluación diferida con `Mono.defer()` y `Mono.fromSupplier()`
-  **Dependency Inversion** - Interfaces en el dominio

## Arquitectura

### Diagrama de Infraestructura AWS

![Arquitectura AWS](docs/architecture-aws.png)

La aplicación está desplegada en AWS con los siguientes componentes:

| Componente | Descripción |
|------------|-------------|
| **VPC** | Red privada virtual con subnets públicas y privadas en 2 AZs |
| **RDS MySQL** | Base de datos en subnet privada (sin acceso público directo) |
| **ECR** | Registro de contenedores Docker para las imágenes de la aplicación |
| **NAT Gateway** | Permite salida a internet desde las subnets privadas |
| **Bastion Host** | EC2 con Session Manager para acceso seguro a RDS (sin SSH keys) |
| **S3 + DynamoDB** | Backend remoto para el state de Terraform |


### Módulos Gradle

| Módulo | Tipo | Descripción |
|--------|------|-------------|
| `:model` | Domain | Entidades de dominio e interfaces de repositorio |
| `:usecase` | Domain | Casos de uso (lógica de negocio) |
| `:mysql` | Infrastructure | Implementación de repositorios con R2DBC MySQL |
| `:reactive-web` | Infrastructure | Controladores REST con WebFlux |

## Pre-requisitos

### Para desarrollo local

1. **Java 21**
   ```bash
   brew install openjdk@21
   ```

2. **Gradle 8.x** (incluido con wrapper)
   ```bash
   ./gradlew --version
   ```

3. **MySQL Server** (base de datos local para desarrollo)
   ```bash
   brew install mysql
   brew services start mysql
   ```

4. **MySQL Client** (opcional, para administrar la base de datos)
   ```bash
   brew install mysql-client
   ```

### Para gestión de infraestructura AWS

5. **AWS CLI**
   ```bash
   brew install awscli
   aws configure
   ```

6. **Session Manager Plugin** (para túnel SSM hacia RDS)
   ```bash
   brew install --cask session-manager-plugin
   ```

7. **Terraform**
   ```bash
   brew install terraform
   ```

8. **Docker** (para construir imágenes)
   ```bash
   brew install --cask docker
   ```

## ⚙️ Configuración Local


###  Configurar base de datos local

Para desarrollo local se usa **MySQL instalado en tu máquina**. Los defaults del `application.yaml` ya apuntan a `localhost:3306` con usuario `root`.

Crea la base de datos y carga el schema:

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS franchises_db"
mysql -u root -p franchises_db < applications/app-service/src/main/resources/schema.sql
```

Configura la contraseña de tu MySQL local como variable de entorno:

```bash
export DB_PASSWORD=TuPasswordLocal
```

### 3. Variables de entorno

La aplicación usa variables de entorno para las credenciales.  


Para desarrollo local solo es necesario configurar `DB_PASSWORD`, ya que los demás valores tienen defaults que apuntan a MySQL local:

| Variable | Default | Descripción |
|----------|---------|-------------|
| `DB_HOST` | `localhost` | Host de MySQL |
| `DB_PORT` | `3306` | Puerto de MySQL |
| `DB_NAME` | `franchises_db` | Nombre de la base de datos |
| `DB_USERNAME` | `root` | Usuario de MySQL |
| `DB_PASSWORD` | *(vacío)* | Contraseña (requerida) |

## Ejecución Local

### Compilar el proyecto

```bash
./gradlew clean build -x test
```

### Ejecutar la aplicación

**Con Gradle**

```bash
./gradlew :app-service:bootRun
```



La aplicación estará disponible en:
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/webjars/swagger-ui/index.html

### Verificar que funciona

```bash
# Health check
curl http://localhost:8080/actuator/health
```

## API Endpoints

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


Documentación completa en: http://localhost:8080/webjars/swagger-ui/index.html

## Docker

El proyecto usa Docker para el despliegue en AWS. El `Dockerfile` usa multi-stage build para crear una imagen optimizada que se sube automáticamente a ECR mediante el CI/CD.

### Build local de la imagen

```bash
# 1. Compilar JAR
./gradlew :app-service:build -x test

# 2. Build imagen Docker
docker build -f deployment/Dockerfile -t franchise-service:latest .
```

### Probar imagen localmente (opcional)

Si quieres probar la imagen Docker conectada a tu MySQL local:

```bash
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=3306 \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=$DB_PASSWORD \
  franchise-service:latest
```

> **Nota**: Para desarrollo local es más simple usar `./gradlew bootRun` directamente.

### Deploy a AWS ECR

El CI/CD de GitHub Actions se encarga automáticamente de:
1. Compilar la aplicación
2. Construir la imagen Docker
3. Subirla a AWS ECR
4. Desplegarla en ECS

Cada push a `main` ejecuta el workflow completo.

**Verificar imágenes en ECR:**
```bash
aws ecr describe-images \
  --repository-name franchise-service \
  --region us-east-1
```

### Características del Dockerfile

-  **Multi-stage build** - Imagen final solo con JRE (más pequeña)
-  **Usuario no-root** - Seguridad mejorada
-  **Health check** integrado


**Tamaño de imagen:**
- Stage 1 (builder): ~800MB (con Gradle y JDK)
- Stage 2 (final): ~200MB (solo JRE + app)

## Infraestructura AWS

La infraestructura está completamente definida como código con Terraform.


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

## Base de Datos

### Schema

La base de datos tiene 3 tablas principales:

```sql
franchises (id, name, created_at, updated_at)
    ↓
branches (id, name, franchise_id, created_at, updated_at)
    ↓
products (id, name, stock, branch_id, created_at, updated_at)
```

### Inicializar schema en MySQL local (desarrollo)

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS franchises_db"
mysql -u root -p franchises_db < applications/app-service/src/main/resources/schema.sql
```

### Inicializar schema en RDS (AWS remoto)

Para crear el schema en la base de datos RDS en AWS, se usa un **túnel SSM** a través del Bastion Host. Esto permite conectarse a la base de datos privada sin exponer puertos públicos.

```bash
# Terminal 1: Iniciar túnel SSM hacia RDS
cd infra
./connect-mysql.sh
# Esto crea un túnel desde localhost:3307 → RDS en la subnet privada

# Terminal 2: Cargar el schema en RDS
mysql -h 127.0.0.1 -P 3307 -u admin -p < applications/app-service/src/main/resources/schema.sql
```

### Configuración de la aplicación

La aplicación se conecta automáticamente usando las variables de entorno configuradas en `application.yaml`:

```yaml
spring:
  r2dbc:
    url: r2dbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:franchises_db}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:}
```

> **Nota:** En desarrollo local la app usa los defaults (`localhost:3306`, `root`). En producción (AWS), las variables de entorno se configuran en el servicio desplegado para apuntar a RDS.

## Testing

```bash
# Ejecutar todos los tests
./gradlew test


### R2DBC vs JPA

Este proyecto usa **R2DBC** (reactivo), no JPA:

```java
// NO usar anotaciones JPA
@Entity  // NO
@Table   // NO

//  Usar anotaciones R2DBC
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

