package com.totvs.conta.shared.constants;

public class MensagemErro {

    public static final class Conta {
        public static final String CONTA_NAO_ATRASADA = "Conta não está atrasada. Vencimento no dia %s";
        public static final String TRANISCAO_INVALIDA = "Não foi possivel alterar a situção.";
        public static final String CONTA_NAO_PAGA = "Informe a data de pagamento.";
        public static final String CONTA_ABERTA_COM_PAGAMENTO = "Altere a situação da conta para PAGA.";
        public static final String CONTA_VALOR_NEGATIVO = "O valor da conta não pode ser negativo";

    }

    public static final class Autenticacao {
        public static final String SENHA_INCORRETA = "A senha informada está incorreta.";
        public static final String EMAIL_NAO_ENCONTRADO = "O e-mail informado não foi encontrado.";
        public static final String USUARIO_DESATIVADO = "O usuário está desativado.";
        public static final String ACESSO_NEGADO = "Acesso não autorizado.";
    }

    public static final class Genericos {
        public static final String PROBLEMA_SERVIDOR = "Problema no servidor. Tente novamente mais tarde.";
        public static final String INFORMACAO_INVALIDA = "Informação inválida: %s";
        public static final String CAMPO_INVALIDO = "Campo inválido";
        public static final String USUARIO_NAO_AUTORIZADO = "Usuário não tem permissão para acessar este recurso.";
        public static final String CAMPO_NECESSARIO = "Campo deve ser preenchido";
        public static final String TAMANHO_INVALIDO = "Informação com tamanho inválido";
        public static final String REGISTRO_EXISTENTE = "Registro já existente";

    }


}
