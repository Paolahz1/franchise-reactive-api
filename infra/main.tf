# ============================================
# FASE 1: NETWORKING
# ============================================
# VPC, Subnets, Internet Gateway, NAT Gateway, Route Tables

module "networking" {
  source = "./networking"

  project            = var.project
  env                = var.env
  vpc_cidr           = var.vpc_cidr
  availability_zones = var.availability_zones
}

# ============================================
# FASE 2: ECR (Repositorio Docker)
# ============================================

module "ecr" {
  source = "./ecr"

  project = var.project
  env     = var.env
}

# ============================================
# FASE 3: RDS (MySQL Database)
# ============================================

module "rds" {
  source = "./rds"

  project_name    = var.project
  environment     = var.env
  vpc_id          = module.networking.vpc_id
  subnet_ids      = module.networking.private_subnet_ids
  allowed_cidr_blocks = [var.vpc_cidr]

  mysql_version       = var.mysql_version
  instance_class      = var.db_instance_class
  allocated_storage   = var.db_allocated_storage
  max_allocated_storage = var.db_max_allocated_storage
  
  database_name       = var.db_name
  master_username     = var.db_master_username
  master_password     = var.db_master_password

  backup_retention_period = var.db_backup_retention_period
  multi_az                = var.db_multi_az
  skip_final_snapshot     = var.db_skip_final_snapshot
  deletion_protection     = var.db_deletion_protection
  publicly_accessible     = var.db_publicly_accessible
}

# ============================================
# FASE 4: Bastion Host (Systems Manager)
# ============================================

module "bastion" {
  source = "./bastion"

  project           = var.project
  env               = var.env
  vpc_id            = module.networking.vpc_id
  private_subnet_id = module.networking.private_subnet_ids[0]
}

# ============================================
# FASE 5: Application Load Balancer
# ============================================

module "alb" {
  source = "./alb"

  project            = var.project
  env                = var.env
  vpc_id             = module.networking.vpc_id
  public_subnet_ids  = module.networking.public_subnet_ids

  container_port               = var.container_port
  health_check_path            = var.health_check_path
  enable_deletion_protection   = var.alb_deletion_protection
}

# ============================================
# FASE 6: ECS (Elastic Container Service)
# ============================================

module "ecs" {
  source = "./ecs"

  project_name       = var.project
  environment        = var.env
  aws_region         = var.aws_region
  
  # Networking
  vpc_id                 = module.networking.vpc_id
  private_subnet_ids     = module.networking.private_subnet_ids
  alb_security_group_id  = module.alb.alb_security_group_id
  alb_target_group_arn   = module.alb.target_group_arn
  
  # ECR
  ecr_repository_url = module.ecr.repository_url
  
  # Database
  db_host     = module.rds.db_instance_address
  db_port     = 3306
  db_name     = var.db_name
  db_username = var.db_master_username
  db_password = var.db_master_password
  
  # ALB
  alb_dns_url = "http://${module.alb.alb_dns_name}"
  
  # ECS Task Configuration
  task_cpu       = var.ecs_task_cpu
  task_memory    = var.ecs_task_memory
  container_name = var.container_name
  container_port = var.container_port
  
  # ECS Service Configuration
  desired_count = var.ecs_desired_count
  min_capacity  = var.ecs_min_capacity
  max_capacity  = var.ecs_max_capacity
  
  # Auto-scaling thresholds
  cpu_target_value    = var.ecs_cpu_target
  memory_target_value = var.ecs_memory_target
  
  # Logging
  log_retention_days = var.ecs_log_retention_days

  tags = {
    Project     = var.project
    Environment = var.env
    ManagedBy   = "Terraform"
  }
}

# ============================================
# SECURITY GROUP RULE: Allow ECS to access RDS
# ============================================

resource "aws_security_group_rule" "rds_from_ecs" {
  type                     = "ingress"
  from_port                = 3306
  to_port                  = 3306
  protocol                 = "tcp"
  source_security_group_id = module.ecs.ecs_security_group_id
  security_group_id        = module.rds.db_security_group_id
  description              = "Allow MySQL access from ECS tasks"
}



