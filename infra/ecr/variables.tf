# ============================================
# Variables de entrada del módulo ECR
# ============================================

variable "project" {
  description = "Project name"
  type        = string
}

variable "env" {
  description = "Environment (dev, staging, prod)"
  type        = string
}
