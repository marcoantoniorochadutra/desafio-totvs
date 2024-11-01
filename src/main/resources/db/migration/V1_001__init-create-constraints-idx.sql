
alter table if exists conta
    add constraint fk_conta_usuario
    foreign key (usuario_id)
    references usuario;

create index idx_usuario_id
    on conta (usuario_id);

create index idx_data_vencimento
    on conta (data_vencimento);

create index idx_email
    on usuario (email);

