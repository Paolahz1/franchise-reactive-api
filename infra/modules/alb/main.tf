# ============================================
# Application Load Balancer
# ============================================
# ALB público que recibe tráfico HTTP/HTTPS
# y lo distribuye a los contenedores ECS

# Security Group para el ALB
resource "aws_security_group" "alb" {
  name        = "${var.project}-${var.env}-alb-sg"
  description = "Security group for Application Load Balancer"
  vpc_id      = var.vpc_id

  # Permite tráfico HTTP desde Internet
  ingress {
    description = "HTTP from Internet"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Permite tráfico HTTPS desde Internet (opcional, para futuro)
  ingress {
    description = "HTTPS from Internet"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Permite todo el tráfico de salida (para healthchecks y conexión a ECS)
  egress {
    description = "All outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "${var.project}-${var.env}-alb-sg"
    Environment = var.env
    Project     = var.project
  }
}

# Application Load Balancer
resource "aws_lb" "this" {
  name               = "${var.project}-${var.env}-alb"
  internal           = false  # ALB público (accesible desde Internet)
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = var.public_subnet_ids  # Debe estar en subnets públicas

  # Protección contra eliminación accidental
  enable_deletion_protection = var.enable_deletion_protection

  # Logs de acceso a S3 (opcional, comentado para ahorrar costos)
  # access_logs {
  #   bucket  = aws_s3_bucket.lb_logs.id
  #   prefix  = "alb"
  #   enabled = true
  # }

  tags = {
    Name        = "${var.project}-${var.env}-alb"
    Environment = var.env
    Project     = var.project
  }
}

# Target Group para ECS
# Grupo de destinos donde el ALB enviará el tráfico
resource "aws_lb_target_group" "ecs" {
  name        = "${var.project}-${var.env}-ecs-tg"
  port        = var.container_port  # Puerto donde corre Spring Boot (8080)
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"  # ECS Fargate usa IP targets

  # Health Check - Verifica que los contenedores estén saludables
  health_check {
    enabled             = true
    path                = var.health_check_path  # /actuator/health
    protocol            = "HTTP"
    port                = "traffic-port"
    healthy_threshold   = 2   # 2 chequeos exitosos = saludable
    unhealthy_threshold = 3   # 3 chequeos fallidos = no saludable
    timeout             = 5   # Timeout de 5 segundos
    interval            = 30  # Chequeo cada 30 segundos
    matcher             = "200"  # Código HTTP esperado
  }

  # Deregistration delay - Tiempo de espera antes de quitar un contenedor
  deregistration_delay = 30

  tags = {
    Name        = "${var.project}-${var.env}-ecs-tg"
    Environment = var.env
    Project     = var.project
  }
}

# Listener HTTP (puerto 80)
# Escucha peticiones HTTP y las redirige al Target Group
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.this.arn
  port              = 80
  protocol          = "HTTP"

  # Acción por defecto: enviar tráfico al Target Group de ECS
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.ecs.arn
  }

  # Si tuvieras HTTPS configurado, podrías redirigir HTTP → HTTPS:
  # default_action {
  #   type = "redirect"
  #   redirect {
  #     port        = "443"
  #     protocol    = "HTTPS"
  #     status_code = "HTTP_301"
  #   }
  # }
}

# Listener HTTPS (puerto 443) - OPCIONAL
# Descomenta esto cuando tengas un certificado SSL en ACM
# resource "aws_lb_listener" "https" {
#   load_balancer_arn = aws_lb.main.arn
#   port              = 443
#   protocol          = "HTTPS"
#   ssl_policy        = "ELBSecurityPolicy-TLS-1-2-2017-01"
#   certificate_arn   = var.ssl_certificate_arn  # ARN del certificado en ACM
#
#   default_action {
#     type             = "forward"
#     target_group_arn = aws_lb_target_group.ecs.arn
#   }
# }
