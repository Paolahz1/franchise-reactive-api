# ============================================
# Variables de entrada del módulo ECR
# ============================================

variable "project" {
  description = "Nombre del proyecto"
  type        = string
}

variable "env" {
  description = "Ambiente (dev, staging, prod)"
  type        = string
}
