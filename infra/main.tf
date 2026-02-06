# ============================================
# FASE 1: NETWORKING
# ============================================
# VPC, Subnets, Internet Gateway, NAT Gateway, Route Tables

module "networking" {
  source = "./modules/networking"

  project            = var.project
  env                = var.env
  vpc_cidr           = var.vpc_cidr
  availability_zones = var.availability_zones
}

# ============================================
# FASE 2: ECR (Repositorio Docker)
# ============================================

module "ecr" {
  source = "./modules/ecr"

  project = var.project
  env     = var.env
}

# ============================================
# FASE 3: RDS (MySQL Database)
# ============================================

module "rds" {
  source = "./modules/rds"

  project_name    = var.project
  environment     = var.env
  vpc_id          = module.networking.vpc_id
  subnet_ids      = module.networking.private_subnet_ids  # Back to private subnets
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
  source = "./modules/bastion"

  project           = var.project
  env               = var.env
  vpc_id            = module.networking.vpc_id
  private_subnet_id = module.networking.private_subnet_ids[0]
}

# ============================================
# FASE 5: Application Load Balancer
# ============================================

module "alb" {
  source = "./modules/alb"

  project            = var.project
  env                = var.env
  vpc_id             = module.networking.vpc_id
  public_subnet_ids  = module.networking.public_subnet_ids  # ALB en subnets públicas

  container_port               = var.container_port
  health_check_path            = var.health_check_path
  enable_deletion_protection   = var.alb_deletion_protection
}
