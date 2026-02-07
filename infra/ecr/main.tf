# ============================================
# ECR - Elastic Container Registry
# ============================================
# Repositorio Docker para almacenar imágenes de la aplicación

resource "aws_ecr_repository" "main" {
  name                 = "${var.project}-${var.env}"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  encryption_configuration {
    encryption_type = "AES256"
  }

  tags = {
    Name        = "${var.project}-${var.env}"
    Environment = var.env
    Project     = var.project
  }
}

# Lifecycle Policy - Mantener solo las últimas 10 imágenes
resource "aws_ecr_lifecycle_policy" "main" {
  repository = aws_ecr_repository.main.name

  policy = jsonencode({
    rules = [{
      rulePriority = 1
      description  = "Keep last 10 images"
      selection = {
        tagStatus   = "any"
        countType   = "imageCountMoreThan"
        countNumber = 10
      }
      action = {
        type = "expire"
      }
    }]
  })
}
