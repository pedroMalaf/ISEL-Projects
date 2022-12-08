--3.2a
select p.noident, p.nif, v.idsistema, v.dtviagem, v.hinicio, v.hfim, v.valfinal
from pessoa p 
inner join pessoa p2 on p.id = p2.id and p.nproprio = 'Antonio' and p.apelido = 'Silva'
join clienteviagem c on c.idpessoa = p.id
join viagem v on v.idsistema = c.viagem

--3.2b
select p3.nproprio , p3.apelido, p3.noident 
from condhabilitado c 
left outer join veiculo v on c.veiculo = v.id
inner join proprietario p on p.idpessoa = v.proprietario 
inner join pessoa p2 on p.idpessoa = p2.id and p2.nproprio = 'Bruno' and p2.apelido = 'Ferreira' and p2.nproprio != 'Carlos' and p2.apelido != 'Brito'
inner join pessoa p3 on c.condutor = p3.id

--3.2c
select v.condutor as id, p.nproprio, p.apelido, p.nif, count(condutor) totalviagens 
from viagem v join pessoa p on v.condutor = p.id and extract(year from v.dtviagem) = '2021'
group by v.condutor, p.nproprio, p.apelido, p.nif
having count (v.condutor) = (
							select max(totalViagensCount)
							from(
								select v2.condutor, count(v2.condutor) totalViagensCount
								from viagem v2
								group by v2.condutor) as A
							)
order by id;

--3.2d
select p.id, p.nproprio, p.apelido, p.nif
from (
	select c.idpessoa  
	from condutor c except select v.condutor from viagem v
) as A 
inner join pessoa p on A.idpessoa = p.id 
			  
--3.2e
select p.nproprio, p.apelido, count(v.idsistema)
from viagem v 
right outer join pessoa p on p.id = v.condutor and extract(year from v.dtviagem) = '2021'
group by p.nproprio, p.apelido

--3.b
select p2.nproprio, p2.apelido, v.id as veículo , count(v2.idsistema) NumViagens
from proprietario p join pessoa p2 on p.dtnascimento notnull and p2.nif notnull and p.idpessoa = p2.id
					join veiculo v on v.proprietario = p.idpessoa
					join viagem v2 on v2.veiculo = v.id 
group by p2.nproprio, p2.apelido, v.id;

--3.c
select distinct  p.nproprio, p.apelido, p.noident, p.morada, sum(v.valfinal) TotalValFinal
from condutor c join pessoa p on c.dtnascimento notnull and p.atrdisc = 'C' and c.idpessoa = p.id 
				join viagem v on v.condutor = c.idpessoa  
group by p.nproprio, p.apelido, p.noident, p.morada;

--3.d
select p.nproprio, p.apelido, count(v.condutor) TotalViagens
from viagem v 
inner join pessoa p on p.id = v.condutor and extract(year from v.dtviagem) = '2021'
group by p.nproprio, p.apelido
having count(v.condutor) < 3;

--3.e
create view CONDUTORVIAGENSPORANO (id, ano, total_viagens, preco_total) as (
	select p.id, extract(year from v.dtviagem) ano, count(p.id) total_viagens, sum(v.valfinal) preco_total
	from pessoa p
	inner join viagem v on p.id = v.condutor
	group by p.id, ano
)

--3.f criação tabela
create table if not exists estatisticas_viagens (
	id serial,
	ano integer,
	total_viagens integer,
	preco_total integer
)

-- inserção dados
insert into estatisticas_viagens (id, ano, total_viagens, preco_total) 
	select p.id, extract(year from v.dtviagem) ano, count(p.id) total_viagens, sum(v.valfinal) preco_total
	from pessoa p
	inner join viagem v on p.id = v.condutor
	group by p.id, ano

--4a
insert into tipoveiculo (tipo, nlugares, designacao, multiplicador)
values(2, 4, 'Luxo', 4);

--4b
update veiculo
set tipo = 2
where marca = 'RangeRover';
