# ============================================
# Outputs del módulo Bastion
# ============================================

output "bastion_instance_id" {
  description = "ID of the bastion instance"
  value       = aws_instance.main.id
}

output "bastion_private_ip" {
  description = "Private IP of bastion"
  value       = aws_instance.main.private_ip
}

output "bastion_security_group_id" {
  description = "Security group ID of bastion"
  value       = aws_security_group.main.id
}
