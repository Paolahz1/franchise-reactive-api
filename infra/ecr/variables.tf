# ============================================
# Variables de entrada del módulo ECR
# ============================================

variable "project" {
  description = "Project name"
  type        = string
}

variable "env" {
  description = "Enviroment (dev, prod)"
  type        = string
}
