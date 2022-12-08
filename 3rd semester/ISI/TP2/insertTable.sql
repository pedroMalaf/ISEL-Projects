insert into PESSOA (id, noident, nif, nproprio, apelido, morada, codpostal, localidade, atrdisc) 
values (1, '49123', '11122233344', 'Antonio', 'Silva', 'Rua da Alegria', 1111333, 'Lisboa', 'C');
insert into PESSOA (id, noident, nif, nproprio, apelido, morada, codpostal, localidade, atrdisc) 
values (2, '40213', '44433322211', 'Bruno', 'Ferreira', 'Rua Esquerda', 1111444, 'Porto', 'CL');
insert into PESSOA (id, noident, nif, nproprio, apelido, morada, codpostal, localidade, atrdisc) 
values (3, '30112', '99912333344', 'Carlos', 'Brito', 'Rua da Alface', 2444100, 'Leiria', 'P');
insert into PESSOA (id, noident, nif, nproprio, apelido, morada, codpostal, localidade, atrdisc) 
values (4, '11111', '12345678999', 'Afonso', 'Mateus', 'Rua da Alface', 2444100, 'Leiria', 'C');
insert into PESSOA (id, noident, nif, nproprio, apelido, morada, codpostal, localidade, atrdisc) 
values (5, '22222', '22222222222', 'Vasco', 'Constantinople', 'Rua da Donzela', 2444100, 'Alzeicar', 'C');


insert into CONDUTOR (idpessoa, ncconducao, dtnascimento) 
values (1, 'AB-1234567', '03-03-2000');
insert into CONDUTOR (idpessoa, ncconducao, dtnascimento) 
values (4, 'AA-1111111', '03-03-2000');
insert into CONDUTOR (idpessoa, ncconducao, dtnascimento) 
values (5, 'AA-2222222', '04-04-1904');


insert into PROPRIETARIO (idpessoa, dtnascimento) 
values (1, '03-03-2000');
insert into PROPRIETARIO (idpessoa, dtnascimento) 
values (2, '02-01-1999');
insert into PROPRIETARIO (idpessoa, dtnascimento) 
values (3, '03-01-1998');


insert into TIPOVEICULO (tipo, nlugares, multiplicador, designacao) 
values (1, 5, '1', 'Normal');



insert into VEICULO (id, matricula, tipo, modelo, marca, ano, proprietario) 
values (1, 'BB11AA', 1, 'm3', 'BMW', 2000, 1);
insert into VEICULO (id, matricula, tipo, modelo, marca, ano, proprietario) 
values (2, 'BB11BB', 1, 'm1', 'FERRARI', 2001, 1);
insert into VEICULO (id, matricula, tipo, modelo, marca, ano, proprietario) 
values (3, 'BB11AA', 1, 'm2', 'FORD', 2000, 2);
insert into VEICULO (id, matricula, tipo, modelo, marca, ano, proprietario) 
values (4, 'BB11BB', 1, 'm4', 'RangeRover', 2001, 3);


insert into PERIODOACTIVO (veiculo, condutor, dtinicio, dtfim, lat, long) 
values (1, 1, '2004-10-19 10:23:54', '2004-10-19 12:23:54', -3.96932, -140.13047);
insert into PERIODOACTIVO (veiculo, condutor, dtinicio, dtfim, lat, long) 
values (2, 4, '2021-10-19 13:23:54', '2004-10-19 15:23:54', -3.12345, -140.12345);


insert into VIAGEM (idsistema, hinicio, hfim, dtviagem, valestimado, valfinal, latinicio, longinicio, latfim, longfim, classificacao, veiculo, condutor, dtinicio) 
values (1, '10:23:54', '12:23:54', '2004-10-19', 5.0, 7.0, -3.96932, -140.13047, -4.96932, -141.123, 5, 1, 1, '2004-10-19 10:23:54');
insert into VIAGEM (idsistema, hinicio, hfim, dtviagem, valestimado, valfinal, latinicio, longinicio, latfim, longfim, classificacao, veiculo, condutor, dtinicio)
values (2, '13:23:54', '15:23:54', '2021-10-19', 6.0, 8.0, -3.12345, -140.12345, -4.12345, -141.123, 2, 2, 4, '2021-10-19 13:23:54');


insert into CLIENTEVIAGEM (idpessoa, viagem) 
values (1, 1);


insert into CONDHABILITADO (condutor, veiculo) 
values (1,2);
insert into CONDHABILITADO (condutor, veiculo) 
values (4,3);
insert into CONDHABILITADO (condutor, veiculo) 
values (5,4);



insert into CONTACTO (idpessoa, ntelefone) 
values (1, '910333540');
insert into CONTACTO (idpessoa, ntelefone) 
values (2, '962213571');
insert into CONTACTO (idpessoa, ntelefone) 
values (3, '979135021');



insert into CORVEICULO (veiculo, cor) 
values (1, 'preto');