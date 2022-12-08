create table if not exists PESSOA(
    id serial primary key,
    noident char(12) not null,
    nif char(12) not null,
    nproprio varchar(10) not null,
    apelido varchar(15) not null,
    morada varchar(150) not null,
    codpostal integer not null, -- Contém 7 digitos, sem o hifen, implementado na aplicação
    localidade varchar(150) not null,
    atrdisc char(2) not null CHECK (atrdisc = 'P' or atrdisc = 'C' or atrdisc = 'CL')
);

create table if not exists CONDUTOR (
    idpessoa integer primary key,
    ncconducao varchar(20) not null, -- Tem o formato CC-DDDDDDD, onde C representa uma letra e D um inteiro, implementado na aplicação
    dtnascimento date not null CHECK (age(dtnascimento) >= interval '18' year),
    foreign key (idpessoa) references PESSOA(id)
);

create table if not exists PROPRIETARIO(
    idpessoa integer primary key,
    dtnascimento date not null CHECK (age(dtnascimento) >= interval '18' year),
    foreign key (idpessoa) references PESSOA(id)
);

create table if not exists TIPOVEICULO(
    tipo serial primary key,
    nlugares integer not null CHECK (nlugares < 8), -- o valor e positivo e inferior a 8
    multiplicador numeric(1) not null,
    designacao varchar(10) not null CHECK (designacao = 'Normal' or designacao = 'XL' or designacao = 'Luxo')
);

create table if not exists VEICULO(
    id integer primary key, 
    matricula varchar(10) not null, --Tem o formato “CCDDCC” ou “DDCCDD”, onde C representa uma letra e D um digito, implementado na aplicação
    tipo integer not null,
    modelo varchar(10) not null,
    marca varchar(10) not null,
    ano integer CHECK (ano + 5 <= date_part('year', now())),
    proprietario integer not null,
    foreign key (tipo) references TIPOVEICULO(tipo),
    foreign key (proprietario) references PROPRIETARIO(idpessoa)
);

create table if not exists PERIODOACTIVO(
    veiculo integer primary key,
    condutor integer not null unique,
    dtinicio timestamp not null unique, -- tem o formato “aaaa-mm-dd hh:mm:ss
    dtfim timestamp, -- tem o formato “aaaa-mm-dd hh:mm:ss
    lat decimal(6,2) not null,
    long decimal(6,2) not null,
    foreign key (condutor) references CONDUTOR(idpessoa),
    foreign key (veiculo) references VEICULO(id)
);

create table if not exists VIAGEM(
    idsistema serial primary key,
    hinicio time not null, -- Tem o formato “hh:mm:ss”
    hfim time CHECK (hfim > hinicio), -- Tem o formato “hh:mm:ss”
    dtviagem date not null, -- Tem o formato “aaaa-mm-dd”
    valestimado decimal(5,2) not null, -- Em euros
    valfinal decimal(5,2),  -- Em euros
    latinicio decimal(6,2) not null,
    longinicio decimal(6,2) not null,
    latfim decimal(6,2) CHECK (latfim != latinicio),
    longfim decimal(6,2) CHECK (longinicio != longfim),
    classificacao integer not null CHECK (classificacao >= 1 and  classificacao <= 5),
    veiculo integer not null,
    condutor integer not null,
    dtinicio timestamp not null,
    foreign key (veiculo) references PERIODOACTIVO(veiculo),
    foreign key (condutor) references PERIODOACTIVO(condutor),
    foreign key (dtinicio) references PERIODOACTIVO(dtinicio)
);

create table if not exists CLIENTEVIAGEM (
    idpessoa integer primary key,
    viagem integer not null,
    foreign key (idpessoa) references PESSOA(id),
    foreign key (viagem) references VIAGEM(idsistema)
);

create table if not exists CONDHABILITADO (
    condutor integer primary key,
    veiculo integer,
    foreign key (condutor) references CONDUTOR(idpessoa),
    foreign key (veiculo) references VEICULO(id)
);

create table if not exists CONTACTO (
    idpessoa integer primary key,
    ntelefone varchar(15) not null, -- pode ser fixo ou movel, inclui o prefixo do pais
    foreign key (idpessoa) references PESSOA(id)
);

create table if not exists CORVEICULO(
    veiculo integer primary key,
    cor varchar(10) not null,
    foreign key (veiculo) references VEICULO(id)
);