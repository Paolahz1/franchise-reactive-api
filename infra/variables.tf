variable "aws_region" {
  description = "Región de AWS"
  type        = string
}

variable "project" {
  description = "Nombre del proyecto"
  type        = string
}

variable "env" {
  description = "Ambiente (dev, prod)"
  type        = string
}

# ============================================
# NETWORKING VARIABLES
# ============================================

variable "vpc_cidr" {
  description = "CIDR block para la VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "availability_zones" {
  description = "Zonas de disponibilidad para alta disponibilidad"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b"]
}

# ============================================
# RDS/MYSQL VARIABLES
# ============================================

variable "mysql_version" {
  description = "Versión de MySQL"
  type        = string
  default     = "8.0.45"
}

variable "db_instance_class" {
  description = "Clase de instancia RDS"
  type        = string
  default     = "db.t3.micro"
}

variable "db_allocated_storage" {
  description = "Almacenamiento inicial en GB"
  type        = number
  default     = 20
}

variable "db_max_allocated_storage" {
  description = "Almacenamiento máximo para autoscaling en GB"
  type        = number
  default     = 100
}

variable "db_name" {
  description = "Nombre de la base de datos"
  type        = string
  default     = "franchises_db"
}

variable "db_master_username" {
  description = "Usuario maestro de la base de datos"
  type        = string
  sensitive   = true
}

variable "db_master_password" {
  description = "Contraseña maestra de la base de datos"
  type        = string
  sensitive   = true
}

variable "db_backup_retention_period" {
  description = "Días de retención de backups"
  type        = number
  default     = 7
}

variable "db_multi_az" {
  description = "Habilitar despliegue Multi-AZ"
  type        = bool
  default     = false
}

variable "db_skip_final_snapshot" {
  description = "Omitir snapshot final al eliminar"
  type        = bool
  default     = false
}

variable "db_deletion_protection" {
  description = "Habilitar protección contra eliminación"
  type        = bool
  default     = true
}

variable "db_publicly_accessible" {
  description = "Hacer la instancia RDS públicamente accesible"
  type        = bool
  default     = false
}

# ============================================
# ALB VARIABLES
# ============================================

variable "container_port" {
  description = "Puerto donde corre la aplicación en el contenedor (Spring Boot)"
  type        = number
  default     = 8080
}

variable "health_check_path" {
  description = "Path del health check endpoint"
  type        = string
  default     = "/actuator/health"
}

variable "alb_deletion_protection" {
  description = "Protección contra eliminación accidental del ALB"
  type        = bool
  default     = false
}

# ============================================
# ECS VARIABLES
# ============================================

variable "ecs_task_cpu" {
  description = "CPU units para ECS task (256, 512, 1024, 2048, 4096)"
  type        = string
  default     = "512"
}

variable "ecs_task_memory" {
  description = "Memoria para ECS task en MB"
  type        = string
  default     = "1024"
}

variable "container_name" {
  description = "Nombre del contenedor"
  type        = string
  default     = "franchise-api"
}

variable "ecs_desired_count" {
  description = "Número deseado de tasks ECS"
  type        = number
  default     = 2
}

variable "ecs_min_capacity" {
  description = "Mínimo número de tasks para auto-scaling"
  type        = number
  default     = 1
}

variable "ecs_max_capacity" {
  description = "Máximo número de tasks para auto-scaling"
  type        = number
  default     = 4
}

variable "ecs_cpu_target" {
  description = "Target de CPU para auto-scaling (%)"
  type        = number
  default     = 70
}

variable "ecs_memory_target" {
  description = "Target de memoria para auto-scaling (%)"
  type        = number
  default     = 80
}

variable "ecs_log_retention_days" {
  description = "Días de retención de logs en CloudWatch"
  type        = number
  default     = 7
}
