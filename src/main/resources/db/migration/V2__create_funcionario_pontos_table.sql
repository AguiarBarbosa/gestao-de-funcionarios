CREATE TABLE funcionario_pontos (
    funcionario_id BIGINT NOT NULL,
    horario_ponto DATETIME2(6) NOT NULL,
    PRIMARY KEY (funcionario_id, horario_ponto),
    FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id)
);