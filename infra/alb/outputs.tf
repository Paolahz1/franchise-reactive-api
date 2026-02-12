# ============================================
# ALB module outputs
# ============================================

output "alb_arn" {
  description = "Application Load Balancer ARN"
  value       = aws_lb.main.arn
}

output "alb_dns_name" {
  description = "Public ALB DNS name (URL to access the API)"
  value       = aws_lb.main.dns_name
}

output "alb_zone_id" {
  description = "ALB Zone ID (for Route 53)"
  value       = aws_lb.main.zone_id
}

output "alb_security_group_id" {
  description = "ALB Security Group ID"
  value       = aws_security_group.main.id
}

output "target_group_arn" {
  description = "Target Group ARN (ECS will register here)"
  value       = aws_lb_target_group.main.arn
}

output "http_listener_arn" {
  description = "HTTP listener ARN"
  value       = aws_lb_listener.main.arn
}
