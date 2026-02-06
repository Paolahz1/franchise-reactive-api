# ============================================
# Variables de entrada del módulo ALB
# ============================================

variable "project" {
  description = "Nombre del proyecto"
  type        = string
}

variable "env" {
  description = "Ambiente (dev, staging, prod)"
  type        = string
}

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
  default     = false  # false en dev, true en prod
}

# variable "ssl_certificate_arn" {
#   description = "ARN del certificado SSL en AWS Certificate Manager (para HTTPS)"
#   type        = string
#   default     = ""
# }
