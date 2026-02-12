# ============================================
# Variables de entrada del módulo ALB
# ============================================

variable "project"{}

variable "env" {}

variable "vpc_id" {
  description = "ID de la VPC donde se creará el ALB"
  type        = string
}

variable "public_subnet_ids" {
  description = "IDs de las subnets públicas donde se desplegará el ALB"
  type        = list(string)
}

variable "container_port" {
  description = "Puerto donde corre la aplicación en el contenedor"
  type        = number
  default     = 8080
}

variable "health_check_path" {
  description = "Path del health check endpoint"
  type        = string
  default     = "/actuator/health"
}

variable "enable_deletion_protection" {
  description = "Protección contra eliminación accidental del ALB"
  type        = bool
  default     = false
}
