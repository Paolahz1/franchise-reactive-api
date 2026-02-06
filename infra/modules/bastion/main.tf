# IAM Role para instancia bastion con SSM
resource "aws_iam_role" "bastion_role" {
  name = "${var.project}-${var.env}-bastion-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })

  tags = {
    Name        = "${var.project}-${var.env}-bastion-role"
    Environment = var.env
    Project     = var.project
  }
}

# Attach SSM policy
resource "aws_iam_role_policy_attachment" "bastion_ssm" {
  role       = aws_iam_role.bastion_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

# Instance profile
resource "aws_iam_instance_profile" "bastion_profile" {
  name = "${var.project}-${var.env}-bastion-profile"
  role = aws_iam_role.bastion_role.name
}

# Security group for bastion
resource "aws_security_group" "bastion" {
  name        = "${var.project}-${var.env}-bastion-sg"
  description = "Security group for bastion instance"
  vpc_id      = var.vpc_id

  # No inbound rules needed - SSM uses outbound only
  
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow all outbound traffic"
  }

  tags = {
    Name        = "${var.project}-${var.env}-bastion-sg"
    Environment = var.env
    Project     = var.project
  }
}

# Get latest Amazon Linux 2023 AMI
data "aws_ami" "amazon_linux_2023" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

# Bastion instance
resource "aws_instance" "bastion" {
  ami                    = data.aws_ami.amazon_linux_2023.id
  instance_type          = "t3.micro"
  subnet_id              = var.private_subnet_id
  vpc_security_group_ids = [aws_security_group.bastion.id]
  iam_instance_profile   = aws_iam_instance_profile.bastion_profile.name

  user_data = <<-EOF
              #!/bin/bash
              yum update -y
              yum install -y mysql
              EOF

  tags = {
    Name        = "${var.project}-${var.env}-bastion"
    Environment = var.env
    Project     = var.project
  }
}
