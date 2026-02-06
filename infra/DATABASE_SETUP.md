# Database Setup Instructions

## Initial Database Configuration

### 1. Connect to RDS MySQL Instance

After Terraform creates the RDS instance, get the connection details from the outputs:

```bash
cd infra
terraform output db_instance_endpoint
terraform output db_name
```

### 2. Create Database Schema

Connect to the MySQL instance using the credentials from `terraform.auto.tfvars`:

```bash
mysql -h <db_instance_endpoint> -u admin -p
```

Then run the schema from `applications/app-service/src/main/resources/schema.sql`:

```sql
-- Schema for Service Franchise Database
-- MySQL 8.0

CREATE TABLE IF NOT EXISTS franchises (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_franchise_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS branches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    franchise_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (franchise_id) REFERENCES franchises(id) ON DELETE CASCADE,
    INDEX idx_franchise_id (franchise_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    branch_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE,
    INDEX idx_branch_id (branch_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 3. Configure Application Environment Variables

Set the following environment variables in your ECS task definition or local environment:

```bash
DB_HOST=<db_instance_address from terraform output>
DB_PORT=3306
DB_NAME=franchises_db
DB_USERNAME=admin
DB_PASSWORD=<your_secure_password>
```

### 4. Verify Connection

Test the connection from your application or using a MySQL client:

```bash
mysql -h $DB_HOST -P $DB_PORT -u $DB_USERNAME -p$DB_PASSWORD $DB_NAME
```

## Security Best Practices

⚠️ **Important**: 
- Change the default password in `terraform.auto.tfvars` before deploying to production
- Consider using AWS Secrets Manager for storing database credentials
- Enable deletion protection for production RDS instances
- Enable Multi-AZ deployment for high availability in production

## Database Relationships

```
franchises (1) -----> (N) branches (1) -----> (N) products
```

- Each Franchise can have multiple Branches
- Each Branch can have multiple Products
- Foreign keys enforce referential integrity with CASCADE deletes
