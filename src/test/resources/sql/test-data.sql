INSERT INTO usuario(ativo, id, email, nome, senha, nivel_usuario) VALUES (true, 9, 'admin@email.com', 'admin', 'e294c293487e3bae8d4469b20afefa4f', 1);

INSERT INTO usuario(ativo, id, email, nome, senha, nivel_usuario) VALUES (true, 10, 'mock_sql@email.com', 'mock_SQL', 'e294c293487e3bae8d4469b20afefa4f', 0);

INSERT INTO usuario(ativo, id, email, nome, senha, nivel_usuario) VALUES (false, 11, 'disabled@email.com', 'Disabilitado', 'e294c293487e3bae8d4469b20afefa4f', 0);


INSERT INTO conta (id, data_pagamento, data_vencimento, situacao, tipo_conta, valor, usuario_id, descricao) VALUES (150, NULL, '2024-11-15', 0, 1, 150.00, 9, 'Conta de Luz');

INSERT INTO conta (id, data_pagamento, data_vencimento, situacao, tipo_conta, valor, usuario_id, descricao) VALUES (160, NULL, now(), 0, 2, 250.00, 10, 'Conta de Internet');

INSERT INTO conta (id, data_pagamento, data_vencimento, situacao, tipo_conta, valor, usuario_id, descricao) VALUES (170, '2024-10-28', '2024-10-30', 2, 1, 320.00, 9, 'Conta de Água');

INSERT INTO conta (id, data_pagamento, data_vencimento, situacao, tipo_conta, valor, usuario_id, descricao) VALUES (190, NULL, now(), 0, 12, 550.00, 10, 'Pagamento de Mercado');

