BASE_URL=http://localhost:8080

# 1) Login (ADMIN_PLATAFORMA) - use para obter token
POST {{ BASE_URL }}/api/v1/auth/login
Content-Type: application/json

{
  "email": "platform@cardapio.local",
  "password": "Admin@123"
}

# 2) Provisionar novo estabelecimento + admin cardápio
POST {{ BASE_URL }}/api/v1/platform/admin-users/provisionar-novo-estabelecimento
Authorization: Bearer {{TOKEN_ADMIN_PLATAFORMA}}
Content-Type: application/json

{
  "nomeRazaoSocial": "Restaurante Sabor da Casa LTDA",
  "cpfCnpj": "12.345.678/0001-99",
  "nomeResponsavel": "Joao da Silva",
  "telefone": "1133334444",
  "whatsapp": "11999998888",
  "publicSlug": "sabor-da-casa",
  "endereco": {
    "logradouro": "Rua das Flores, 100",
    "bairro": "Centro",
    "cidade": "Sao Paulo",
    "codigoPostal": "01001-000"
  },
  "admin": {
    "name": "Admin Cardapio Sabor",
    "email": "admin.sabor@cardapio.local",
    "password": "AdminCardapio@123"
  }
}

# 3) Criar admin (ADMIN_CARDAPIO ou ADMIN_PLATAFORMA)
POST {{ BASE_URL }}/api/v1/platform/admin-users
Authorization: Bearer {{TOKEN_ADMIN_PLATAFORMA}}
Content-Type: application/json

{
  "name": "Admin Cardapio Secundario",
  "email": "admin.secundario@cardapio.local",
  "password": "AdminCardapio@123",
  "role": "ADMIN_CARDAPIO",
  "estabelecimentoId": "{{ESTABELECIMENTO_ID}}"
}

# 4) Listar admins de cardápio
GET {{ BASE_URL }}/api/v1/platform/admin-users
Authorization: Bearer {{TOKEN_ADMIN_PLATAFORMA}}

# 5) Atualizar admin de cardápio
PUT {{ BASE_URL }}/api/v1/platform/admin-users/{{ADMIN_USER_ID}}
Authorization: Bearer {{TOKEN_ADMIN_PLATAFORMA}}
Content-Type: application/json

{
  "name": "Admin Cardapio Atualizado",
  "active": true
}

# 6) Excluir admin de cardápio
DELETE {{ BASE_URL }}/api/v1/platform/admin-users/{{ADMIN_USER_ID}}
Authorization: Bearer {{TOKEN_ADMIN_PLATAFORMA}}

# 7) Substituir permissões
PUT {{ BASE_URL }}/api/v1/platform/admin-users/{{ADMIN_USER_ID}}/permissions
Authorization: Bearer {{TOKEN_ADMIN_PLATAFORMA}}
Content-Type: application/json

{
  "permissions": ["USER_MANAGEMENT", "MENU_EDIT", "MENU_VIEW"]
}

# 8) Adicionar uma permissão
POST {{ BASE_URL }}/api/v1/platform/admin-users/{{ADMIN_USER_ID}}/permissions
Authorization: Bearer {{TOKEN_ADMIN_PLATAFORMA}}
Content-Type: application/json

{
  "permission": "AUDIT_VIEW"
}

# 9) Remover uma permissão
DELETE {{ BASE_URL }}/api/v1/platform/admin-users/{{ADMIN_USER_ID}}/permissions/AUDIT_VIEW
Authorization: Bearer {{TOKEN_ADMIN_PLATAFORMA}}

# 10) Listar admins por estabelecimento
GET {{ BASE_URL }}/api/v1/platform/estabelecimentos/{{ESTABELECIMENTO_ID}}/admin-users?onlyMenuAdmins=true
Authorization: Bearer {{TOKEN_ADMIN_PLATAFORMA}}

# 11) Login (ADMIN_CARDAPIO) - para testar escopo
POST {{ BASE_URL }}/api/v1/auth/login
Content-Type: application/json

{
  "email": "admin.sabor@cardapio.local",
  "password": "AdminCardapio@123"
}

# 12) Verificar escopo do admin de cardápio
GET {{ BASE_URL }}/api/v1/menu-admin/estabelecimentos/{{ESTABELECIMENTO_ID}}/scope-check
Authorization: Bearer {{TOKEN_ADMIN_CARDAPIO}}
