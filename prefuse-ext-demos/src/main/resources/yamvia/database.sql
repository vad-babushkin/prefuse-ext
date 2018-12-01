-- Table: "GMInvolvement"
CREATE TABLE "GMInvolvement"
(
  oidmovie oid NOT NULL,
  oidgenre oid NOT NULL
)
WITHOUT OIDS;

-- Index: index_gmi
CREATE INDEX index_gmi
  ON "GMInvolvement"
  USING btree
  (oidmovie, oidgenre);

-- Table: "Genre"
CREATE TABLE "Genre"
(
  gname varchar(64) NOT NULL
)
WITH OIDS;

-- Table: "MPRInvolvement"
CREATE TABLE "MPRInvolvement"
(
  oidmovie oid,
  oidperson oid,
  oidrole oid
)
WITH OIDS;

-- Index: index_oidmovie
CREATE INDEX index_oidmovie
  ON "MPRInvolvement"
  USING btree
  (oidmovie, oidperson, oidrole);

-- Table: "Movie"
CREATE TABLE "Movie"
(
  mname varchar(128) NOT NULL,
  myear int2 NOT NULL,
  mrating float4
)
WITH OIDS;

-- Index: indexmovie
CREATE INDEX indexmovie
  ON "Movie"
  USING btree
  (mname, myear, mrating);

-- Table: "Person"
CREATE TABLE "Person"
(
  pname varchar(64) NOT NULL,
  gender char(1) DEFAULT 'U'::bpchar
)
WITH OIDS;

-- Index: index_pname
CREATE INDEX index_pname
  ON "Person"
  USING btree
  (pname);

-- Table: "Role"
CREATE TABLE "Role"
(
  rname varchar(64) NOT NULL
)
WITH OIDS;
