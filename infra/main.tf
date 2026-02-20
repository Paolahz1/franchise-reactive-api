# ============================================
# FASE 1: NETWORKING
# ============================================
# VPC, Subnets, Internet Gateway, NAT Gateway, Route Tables

module "networking" {
  source = "./networking"

  project = var.project
  env     = var.env
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

  project             = var.project
  env                 = var.env
  vpc_id              = module.networking.vpc_id
  subnet_ids          = module.networking.private_subnet_ids
  allowed_cidr_blocks = [module.networking.vpc_cidr]

  database_name   = var.db_name
  master_username = var.db_master_username
  master_password = var.db_master_password

  skip_final_snapshot = var.db_skip_final_snapshot
  deletion_protection = var.db_deletion_protection
}

# ============================================
# FASE 4: Application Load Balancer
# ============================================

module "alb" {
  source = "./alb"

  project                    = var.project
  env                        = var.env
  vpc_id                     = module.networking.vpc_id
  public_subnet_ids          = module.networking.public_subnet_ids
  enable_deletion_protection = var.alb_deletion_protection
}

# ============================================
# FASE 5: ECS (Elastic Container Service)
# ============================================

module "ecs" {
  source = "./ecs"

  project    = var.project
  env        = var.env
  aws_region = var.aws_region

  # Networking
  vpc_id                = module.networking.vpc_id
  private_subnet_ids    = module.networking.private_subnet_ids
  alb_security_group_id = module.alb.alb_security_group_id
  alb_target_group_arn  = module.alb.target_group_arn

  # ECR
  ecr_repository_url = module.ecr.repository_url

  # Database connection
  db_host     = module.rds.db_instance_address
  db_name     = var.db_name
  db_username = var.db_master_username
  db_password = var.db_master_password

  # ALB
  alb_dns_url = "http://${module.alb.alb_dns_name}"

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
  security_group_id        = module.rds.db_security_group_id //Destino
  description              = "Allow MySQL access from ECS tasks"
}



