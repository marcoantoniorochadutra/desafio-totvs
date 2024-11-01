package com.totvs.conta.domain.model.usuario;

import com.totvs.conta.application.exception.AuthError;
import com.totvs.conta.application.exception.AuthException;
import com.totvs.conta.shared.constants.MensagemErro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder(setterPrefix = "with")
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "email")}, indexes = {@Index(name = "idx_email", columnList = "email")})
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Pattern(regexp = "^\\S+@\\S+\\.\\S+$")
    @Column(length = 200)
    private String email;

    @NotNull
    @Column(length = 100)
    private String nome;

    @NotNull
    @Column(length = 100)
    private String senha;

    @NotNull
    private Boolean ativo;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private NivelUsuario nivelUsuario;


    public void validarUsuario(String senhaInserida) {
        if (!getAtivo()) {
            throw new AuthException(AuthError.USUARIO_DESATIVADO, MensagemErro.Autenticacao.USUARIO_DESATIVADO);
        }

        if (Objects.isNull(senhaInserida) || !senhaInserida.equals(getSenha())) {
            throw new AuthException(AuthError.SENHA_INCORRETA, MensagemErro.Autenticacao.SENHA_INCORRETA);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(nivelUsuario.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

}
