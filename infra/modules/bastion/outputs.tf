output "bastion_instance_id" {
  description = "ID of the bastion instance"
  value       = aws_instance.bastion.id
}

output "bastion_private_ip" {
  description = "Private IP of bastion"
  value       = aws_instance.bastion.private_ip
}
