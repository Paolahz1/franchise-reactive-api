output "repository_url" {
  description = "URL del repositorio ECR"
  value       = module.ecr.repository_url
}

output "repository_arn" {
  description = "ARN del repositorio ECR"
  value       = module.ecr.repository_arn
}

output "repository_name" {
  description = "Nombre del repositorio"
  value       = module.ecr.repository_name
}
