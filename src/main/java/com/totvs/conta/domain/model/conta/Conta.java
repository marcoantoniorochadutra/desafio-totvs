package com.totvs.conta.domain.model.conta;

import java.time.LocalDate;
import java.util.Objects;


import com.totvs.conta.shared.constants.MensagemErro;
import com.totvs.conta.domain.exception.DomainException;
import com.totvs.conta.domain.model.usuario.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    @NotNull
    @Min(value = 0)
    private Double valor;

    @NotNull
    private String descricao;

    @NotNull
    private Situacao situacao;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", foreignKey = @ForeignKey(name = "fk_conta_usuario"))
    private Usuario usuario;

    @Builder(setterPrefix = "with")
    public Conta(LocalDate dataVencimento, LocalDate dataPagamento, Double valor, String descricao, Situacao situacao, Usuario usuario) {
        this.dataVencimento = dataVencimento;
        this.dataPagamento = dataPagamento;
        this.valor = valor;
        this.descricao = descricao;
        this.situacao = situacao;
        this.usuario = usuario;
        validarCampos();
    }

    public void validarCampos() {
        if (Situacao.EM_ABERTO.equals(this.situacao) && Objects.nonNull(this.dataPagamento)) {
            throw new DomainException(MensagemErro.Conta.CONTA_ABERTA_COM_PAGAMENTO);
        }

        if (Situacao.PAGO.equals(this.situacao) && Objects.isNull(this.dataPagamento)) {
            throw new DomainException(MensagemErro.Conta.CONTA_NAO_PAGA);
        }

        if (valor < 0) {
            throw new DomainException(MensagemErro.Conta.CONTA_VALOR_NEGATIVO);
        }
    }

    public void atualizarSituacao(Situacao situacao) {
        if (transicaoSituacaoValida(situacao)) {
            if (!this.situacao.equals(situacao)) {
                switch (situacao) {
                    case PAGO: marcarComoPaga(); break;
                    case CANCELADO: marcarComoCancelada(); break;
                    case ATRASADO: marcarComoAtrasada(); break;
                }
            }
        } else {
            throw new DomainException(MensagemErro.Conta.TRANISCAO_INVALIDA);
        }
    }

    private void marcarComoPaga() {
        this.dataPagamento = LocalDate.now();
        this.situacao = Situacao.PAGO;
    }

    private void marcarComoAtrasada() {
        if (LocalDate.now().isBefore(this.dataVencimento)) {
            throw new DomainException(String.format(MensagemErro.Conta.CONTA_NAO_ATRASADA, this.dataVencimento));
        }
        this.situacao = Situacao.ATRASADO;
    }

    private void marcarComoCancelada() {
        this.situacao = Situacao.CANCELADO;
    }

    private boolean transicaoSituacaoValida(Situacao situacaoNova) {
        return this.situacao.getHierarquia() <= situacaoNova.getHierarquia();
    }

    public void validarPermissaoUsuario(Usuario usuario) {
        if (usuario.getNivelUsuario().isUsuario() && !this.usuario.getId().equals(usuario.getId())) {
            throw new DomainException(MensagemErro.Genericos.USUARIO_NAO_AUTORIZADO);
        }
    }
}
