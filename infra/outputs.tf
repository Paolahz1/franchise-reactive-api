# ============================================
# NETWORKING OUTPUTS
# ============================================

output "vpc_id" {
  description = "ID of the created VPC"
  value       = module.networking.vpc_id
}

output "vpc_cidr" {
  description = "VPC CIDR block"
  value       = module.networking.vpc_cidr
}

output "public_subnet_ids" {
  description = "IDs of the public subnets (for ALB)"
  value       = module.networking.public_subnet_ids
}

output "private_subnet_ids" {
  description = "IDs of the private subnets (for ECS and RDS)"
  value       = module.networking.private_subnet_ids
}

# ============================================
# ECR OUTPUTS
# ============================================

output "ecr_repository_url" {
  description = "ECR repository URL"
  value       = module.ecr.repository_url
}

output "ecr_repository_arn" {
  description = "ECR repository ARN"
  value       = module.ecr.repository_arn
}

output "ecr_repository_name" {
  description = "ECR repository name"
  value       = module.ecr.repository_name
}

# ============================================
# RDS OUTPUTS
# ============================================

output "db_instance_endpoint" {
  description = "Database connection endpoint"
  value       = module.rds.db_instance_endpoint
}

output "db_instance_address" {
  description = "RDS instance hostname"
  value       = module.rds.db_instance_address
}

output "db_instance_port" {
  description = "Database port"
  value       = module.rds.db_instance_port
}

output "db_name" {
  description = "Database name"
  value       = module.rds.db_name
}

output "db_security_group_id" {
  description = "Database security group ID"
  value       = module.rds.db_security_group_id
}

# ============================================
# BASTION OUTPUTS
# ============================================

output "bastion_instance_id" {
  description = "Bastion instance ID for Session Manager"
  value       = module.bastion.bastion_instance_id
}

output "bastion_connection_command" {
  description = "Command to connect via Session Manager"
  value       = "aws ssm start-session --target ${module.bastion.bastion_instance_id}"
}

# ============================================
# ALB OUTPUTS
# ============================================

output "alb_dns_name" {
  description = "Public URL of the Application Load Balancer (to access the API)"
  value       = module.alb.alb_dns_name
}

output "alb_arn" {
  description = "Application Load Balancer ARN"
  value       = module.alb.alb_arn
}

output "alb_target_group_arn" {
  description = "Target Group ARN (used by ECS)"
  value       = module.alb.target_group_arn
}

output "alb_security_group_id" {
  description = "ALB Security Group ID"
  value       = module.alb.alb_security_group_id
}

output "api_url" {
  description = "Full API URL"
  value       = "http://${module.alb.alb_dns_name}"
}

# ============================================
# ECS OUTPUTS
# ============================================

output "ecs_cluster_name" {
  description = "ECS Cluster name"
  value       = module.ecs.cluster_name
}

output "ecs_cluster_arn" {
  description = "ECS Cluster ARN"
  value       = module.ecs.cluster_arn
}

output "ecs_service_name" {
  description = "ECS Service name"
  value       = module.ecs.service_name
}

output "ecs_service_arn" {
  description = "ECS Service ARN"
  value       = module.ecs.service_arn
}

output "ecs_task_definition_arn" {
  description = "Task Definition ARN"
  value       = module.ecs.task_definition_arn
}

output "ecs_task_definition_family" {
  description = "Task Definition family"
  value       = module.ecs.task_definition_family
}

output "ecs_log_group_name" {
  description = "CloudWatch Log Group name"
  value       = module.ecs.log_group_name
}

output "ecs_security_group_id" {
  description = "ECS Tasks Security Group ID"
  value       = module.ecs.ecs_security_group_id
}
