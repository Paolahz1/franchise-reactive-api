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
