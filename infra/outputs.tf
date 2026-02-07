# ============================================
# NETWORKING OUTPUTS
# ============================================

output "vpc_id" {
  description = "ID de la VPC creada"
  value       = module.networking.vpc_id
}

output "vpc_cidr" {
  description = "CIDR block de la VPC"
  value       = module.networking.vpc_cidr
}

output "public_subnet_ids" {
  description = "IDs de las subnets públicas (para ALB)"
  value       = module.networking.public_subnet_ids
}

output "private_subnet_ids" {
  description = "IDs de las subnets privadas (para ECS y RDS)"
  value       = module.networking.private_subnet_ids
}

# ============================================
# ECR OUTPUTS
# ============================================

output "ecr_repository_url" {
  description = "URL del repositorio ECR"
  value       = module.ecr.repository_url
}

output "ecr_repository_arn" {
  description = "ARN del repositorio ECR"
  value       = module.ecr.repository_arn
}

output "ecr_repository_name" {
  description = "Nombre del repositorio ECR"
  value       = module.ecr.repository_name
}

# ============================================
# RDS OUTPUTS
# ============================================

output "db_instance_endpoint" {
  description = "Endpoint de conexión a la base de datos"
  value       = module.rds.db_instance_endpoint
}

output "db_instance_address" {
  description = "Hostname de la instancia RDS"
  value       = module.rds.db_instance_address
}

output "db_instance_port" {
  description = "Puerto de la base de datos"
  value       = module.rds.db_instance_port
}

output "db_name" {
  description = "Nombre de la base de datos"
  value       = module.rds.db_name
}

output "db_security_group_id" {
  description = "ID del security group de la base de datos"
  value       = module.rds.db_security_group_id
}

# ============================================
# BASTION OUTPUTS
# ============================================

output "bastion_instance_id" {
  description = "ID de la instancia bastion para Session Manager"
  value       = module.bastion.bastion_instance_id
}

output "bastion_connection_command" {
  description = "Comando para conectar via Session Manager"
  value       = "aws ssm start-session --target ${module.bastion.bastion_instance_id}"
}

# ============================================
# ALB OUTPUTS
# ============================================

output "alb_dns_name" {
  description = "URL pública del Application Load Balancer (para acceder a la API)"
  value       = module.alb.alb_dns_name
}

output "alb_arn" {
  description = "ARN del Application Load Balancer"
  value       = module.alb.alb_arn
}

output "alb_target_group_arn" {
  description = "ARN del Target Group (usado por ECS)"
  value       = module.alb.target_group_arn
}

output "alb_security_group_id" {
  description = "ID del Security Group del ALB"
  value       = module.alb.alb_security_group_id
}

output "api_url" {
  description = "URL completa de la API"
  value       = "http://${module.alb.alb_dns_name}"
}
