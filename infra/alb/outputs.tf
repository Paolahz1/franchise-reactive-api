# ============================================
# Outputs del módulo ALB
# ============================================

output "alb_arn" {
  description = "ARN del Application Load Balancer"
  value       = aws_lb.main.arn
}

output "alb_dns_name" {
  description = "DNS público del ALB (URL para acceder a la API)"
  value       = aws_lb.main.dns_name
}

output "alb_zone_id" {
  description = "Zone ID del ALB (para Route 53)"
  value       = aws_lb.main.zone_id
}

output "alb_security_group_id" {
  description = "ID del Security Group del ALB"
  value       = aws_security_group.main.id
}

output "target_group_arn" {
  description = "ARN del Target Group (ECS se registrará aquí)"
  value       = aws_lb_target_group.main.arn
}

output "http_listener_arn" {
  description = "ARN del listener HTTP"
  value       = aws_lb_listener.main.arn
}
