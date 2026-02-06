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
