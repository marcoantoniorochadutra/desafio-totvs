INSERT INTO usuario(ativo, id, email, nome, senha, nivel_usuario) VALUES (true, 9, 'admin@email.com', 'admin', 'e294c293487e3bae8d4469b20afefa4f', 1);

INSERT INTO usuario(ativo, id, email, nome, senha, nivel_usuario) VALUES (true, 10, 'mock_sql@email.com', 'mock_SQL', 'e294c293487e3bae8d4469b20afefa4f', 0);

INSERT INTO usuario(ativo, id, email, nome, senha, nivel_usuario) VALUES (false, 11, 'disabled@email.com', 'Disabilitado', 'e294c293487e3bae8d4469b20afefa4f', 0);


INSERT INTO conta (id, data_pagamento, data_vencimento, situacao, valor, usuario_id, descricao) VALUES (15, NULL, '2024-11-15', 0, 150.00, 9, 'Conta de Luz');

INSERT INTO conta (id, data_pagamento, data_vencimento, situacao, valor, usuario_id, descricao) VALUES (16, NULL, now(), 0, 250.00, 10, 'Conta de Internet');

INSERT INTO conta (id, data_pagamento, data_vencimento, situacao, valor, usuario_id, descricao) VALUES (17, '2024-10-28', '2024-10-30', 2, 320.00, 9, 'Conta de Água');
INSERT INTO conta (id, data_pagamento, data_vencimento, situacao, valor, usuario_id, descricao) VALUES (19, NULL, now(), 0, 550.00, 10, 'Pagamento de Mercado');

