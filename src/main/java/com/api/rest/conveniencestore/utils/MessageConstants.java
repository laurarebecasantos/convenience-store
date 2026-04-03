package com.api.rest.conveniencestore.utils;

public class MessageConstants {

    public static final String USERNAME_NOT_FOUND = "Usuário não encontrado.";
    public static final String CREDENTIALS_INVALID = "Credenciais inválidas: usuário ou senha incorretos.";
    public static final String PRODUCT_ALREADY_EXISTS = "Produto já cadastrado com o nome: ";
    public static final String NO_PRODUCTS_FOUND = "Nenhum produto cadastrado foi encontrado.";
    public static final String INVALID_STATUS = "Status inválido: ";
    public static final String NO_USERS_FOUND ="Nenhum usuário cadastrado foi encontrado.";
    public static final String STATUS_INACTIVE = "O status deve ser alterado apenas para INACTIVE.";
    public static final String STATUS_ACTIVE_OR_INACTIVE = "O status deve ser ACTIVE ou INACTIVE.";
    public static final String STATUS_ACTIVE_OR_INACTIVE_PRODUCT = "O status do produto deve ser ACTIVE ou INACTIVE.";
    public static final String PRODUCT_NOT_FOUND = "Produto com ID: %d não foi encontrado.";
    public static final String USER_NOT_FOUND = "Usuário com ID %d não foi encontrado.";
    public static final String PAYMENT_METHOD_EMPTY = "O método de pagamento não pode ser vazio.";
    public static final String INVALID_PAYMENT_METHOD = "Método de pagamento inválido: ";
    public static final String INVALID_ROLE = "Role inválido: ";
    public static final String ROLE_ADMIN = "A role só pode ser alterada para ADMIN.";
    public static final String INVALID_PRODUCT = "Produto inativo: ";
    public static final String EMAIL_ALREADY_REGISTERED = "Email já cadastrado: ";
    public static final String ERROR_JWT_TOKEN = "Erro ao gerar o token JWT.";
    public static final String STATUS_CANCELLED = "O status deve ser alterado apenas para CANCELLED.";
    public static final String SALE_NOT_FOUND = "Compra de ID: %d não foi encontrada.";
    public static final String CPF_ALREADY_EXISTS = "CPF já está registrado: ";
    public static final String CLIENT_NOT_FOUND_BY_CPF = "Cliente com CPF não encontrado: ";
    public static final String CLIENT_NOT_FOUND_BY_ID = "Cliente com ID %d não foi encontrado.";
    public static final String NO_CLIENTS_FOUND = "Nenhum cliente cadastrado foi encontrado.";
    public static final String INVALID_PRICE = "O preço do produto deve ser maior que zero.";
    public static final String INVALID_STOCK = "A quantidade em estoque não pode ser negativa.";
    public static final String INVALID_EXPIRATION_DATE = "A data de validade não pode ser anterior a hoje.";
    public static final String PRODUCT_EXPIRED_UPDATE = "Produto vencido não pode ser alterado.";
    public static final String PRODUCT_INACTIVE_UPDATE = "Produto inativo não pode ser alterado.";
}