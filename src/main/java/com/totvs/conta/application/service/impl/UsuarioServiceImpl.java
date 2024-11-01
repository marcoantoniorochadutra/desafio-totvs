package com.totvs.conta.application.service.impl;

import com.totvs.conta.shared.constants.MensagemErro;
import com.totvs.conta.shared.dto.LoginContextDto;
import com.totvs.conta.shared.dto.SelecionavelDto;
import com.totvs.conta.domain.model.usuario.NivelUsuario;
import com.totvs.conta.interfaces.usuario.dto.LoginDto;
import com.totvs.conta.interfaces.usuario.dto.LoginWrapperDto;
import com.totvs.conta.interfaces.usuario.dto.RegistroDto;
import com.totvs.conta.interfaces.usuario.dto.UsuarioDto;
import com.totvs.conta.application.exception.AuthError;
import com.totvs.conta.application.exception.AuthException;
import com.totvs.conta.application.service.UsuarioService;
import com.totvs.conta.domain.model.usuario.Usuario;
import com.totvs.conta.domain.model.usuario.UsuarioRepository;
import com.totvs.conta.infra.security.SenhaUtils;
import com.totvs.conta.infra.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public LoginWrapperDto login(LoginDto login) {
        Usuario usuario = buscarUsuarioPorCredenciais(login);
        LoginContextDto context = criarLoginContext(usuario);
        String accessToken = JwtProvider.generate(context);

        return LoginWrapperDto.builder()
                .withAccessToken(accessToken)
                .withLoginContext(context)
                .build();
    }

    @Override
    public UsuarioDto registrar(RegistroDto registro) {
        Usuario usuario = converterUsuarioParaDominio(registro);

        usuario = usuarioRepository.salvar(usuario);
        return converterUsuarioParaDto(usuario);
    }

    private LoginContextDto criarLoginContext(Usuario usuario) {
        return LoginContextDto.builder()
                .withId(usuario.getId())
                .withEmail(usuario.getEmail())
                .withNivel(SelecionavelDto.builder()
                        .withChave(usuario.getNivelUsuario().name())
                        .withValor(usuario.getNivelUsuario().getDescricao())
                        .build())
                .build();
    }

    private Usuario buscarUsuarioPorCredenciais(LoginDto login) {
        Usuario usuario = usuarioRepository.buscarPorEmail(login.email());

        if (Objects.isNull(usuario)) {
            throw new AuthException(AuthError.EMAIL_NAO_ENCONTRADO, MensagemErro.Autenticacao.EMAIL_NAO_ENCONTRADO);
        }

        String senhaCriptografa = SenhaUtils.encodeSenha(login.senha());

        usuario.validarUsuario(senhaCriptografa);
        return usuario;
    }

    private Usuario converterUsuarioParaDominio(RegistroDto registro) {
        SenhaUtils.validarSenha(registro.senha());
        return Usuario.builder()
                .withEmail(registro.email())
                .withSenha(SenhaUtils.encodeSenha(registro.senha()))
                .withNome(registro.nome())
                .withAtivo(true)
                .withNivelUsuario(NivelUsuario.USUARIO)
                .build();
    }

    private UsuarioDto converterUsuarioParaDto(Usuario usuario) {
        return UsuarioDto.builder().withId(usuario.getId())
                .withEmail(usuario.getEmail())
                .withNome(usuario.getNome())
                .withAtivo(usuario.getAtivo())
                .build();
    }
}
