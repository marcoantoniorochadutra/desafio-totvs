package com.totvs.conta.infra.persistence.repository.jpa;

import com.totvs.conta.domain.model.conta.Conta;
import com.totvs.conta.domain.model.conta.ContaRepository;
import com.totvs.conta.domain.model.conta.Situacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface JpaContaRepository extends ContaRepository, JpaRepository<Conta, Long>, PagingAndSortingRepository<Conta, Long> {

    default Conta salvar(Conta conta) {
        return saveAndFlush(conta);
    }

    default void apagar(Long id) {
        deleteById(id);
    }

    default Conta buscarPorId(Long id) {
        return getReferenceById(id);
    }

    @Query("""
            select c from Conta c
            inner join Usuario u on u.id = c.usuario.id
            where c.dataVencimento between :dataInicial and :dataFinal
            and c.situacao in :situacao
            and c.descricao like CONCAT('%', :descricao ,'%')
            and u.id = :usuarioId
            """)
    Page<Conta> buscarContasPagar(@Param("situacao") List<Situacao> situacao,
                                  @Param("descricao") String descricao,
                                  @Param("dataInicial") LocalDate dataInicial,
                                  @Param("dataFinal") LocalDate dataFinal,
                                  @Param("usuarioId") Long usuarioId,
                                  Pageable pageable);

    default Page<Conta> obterContasPagar(List<Situacao> situacao, String descricao, LocalDate dataInicial, LocalDate dataFinal, Long usuarioId, Pageable pageable) {
        return buscarContasPagar(situacao, descricao, dataInicial, dataFinal, usuarioId, pageable);
    }

    @Query("""
            select sum(c.valor)
            from Conta c
            inner join Usuario u on u.id = c.usuario.id
            where c.dataPagamento between :dataInicial and :dataFinal
            and c.situacao = 2
            and u.id = :usuarioId
            """)
    Double obterValorTotalPorPeriodo(@Param("dataInicial") LocalDate dataInicial,
                                          @Param("dataFinal") LocalDate dataFinal,
                                          @Param("usuarioId") Long usuarioId);

    default Double obterValorTotalPagarPorPeriodo(LocalDate dataInicial, LocalDate dataFinal, Long usuarioId) {
        return obterValorTotalPorPeriodo(dataInicial, dataFinal, usuarioId);
    }


}
