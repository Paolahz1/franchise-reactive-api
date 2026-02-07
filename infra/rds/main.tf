# ============================================
# RDS - MySQL Database
# ============================================

# Subnet Group para RDS
resource "aws_db_subnet_group" "main" {
  name       = "${var.project_name}-${var.environment}-db-subnet-group"
  subnet_ids = var.subnet_ids

  tags = {
    Name        = "${var.project_name}-${var.environment}-db-subnet-group"
    Environment = var.environment
    Project     = var.project_name
  }
}

# Security Group para RDS
resource "aws_security_group" "main" {
  name        = "${var.project_name}-${var.environment}-rds-sg"
  description = "Security group for RDS MySQL instance"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = var.publicly_accessible ? ["0.0.0.0/0"] : var.allowed_cidr_blocks
    description = var.publicly_accessible ? "MySQL access from anywhere (DEV ONLY)" : "MySQL access from allowed CIDR blocks"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow all outbound traffic"
  }

  tags = {
    Name        = "${var.project_name}-${var.environment}-rds-sg"
    Environment = var.environment
    Project     = var.project_name
  }
}

# Parameter Group para MySQL
resource "aws_db_parameter_group" "main" {
  name   = "${var.project_name}-${var.environment}-mysql-params"
  family = "mysql8.0"

  parameter {
    name  = "character_set_server"
    value = "utf8mb4"
  }

  parameter {
    name  = "collation_server"
    value = "utf8mb4_unicode_ci"
  }

  parameter {
    name  = "max_connections"
    value = var.max_connections
  }

  tags = {
    Name        = "${var.project_name}-${var.environment}-mysql-params"
    Environment = var.environment
    Project     = var.project_name
  }
}

# RDS Instance
resource "aws_db_instance" "main" {
  identifier     = "${var.project_name}-${var.environment}-mysql"
  engine         = "mysql"
  engine_version = var.mysql_version

  instance_class        = var.instance_class
  allocated_storage     = var.allocated_storage
  max_allocated_storage = var.max_allocated_storage
  storage_type          = "gp3"
  storage_encrypted     = true

  db_name  = var.database_name
  username = var.master_username
  password = var.master_password

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.main.id]
  parameter_group_name   = aws_db_parameter_group.main.name

  backup_retention_period = var.backup_retention_period
  backup_window           = var.backup_window
  maintenance_window      = var.maintenance_window

  skip_final_snapshot       = var.skip_final_snapshot
  final_snapshot_identifier = var.skip_final_snapshot ? null : "${var.project_name}-${var.environment}-mysql-final-snapshot"

  enabled_cloudwatch_logs_exports = ["error", "general", "slowquery"]

  multi_az                   = var.multi_az
  publicly_accessible        = var.publicly_accessible
  deletion_protection        = var.deletion_protection
  copy_tags_to_snapshot      = true
  auto_minor_version_upgrade = true

  tags = {
    Name        = "${var.project_name}-${var.environment}-mysql"
    Environment = var.environment
    Project     = var.project_name
  }
}
