/* Entities */

CREATE TYPE ProductType AS ENUM('Ski', 'Snowboard', 'Poles', 'SkiBoots', 'Snowboots', 'Helmets', 'Skiwear', 'Accessories');

CREATE TYPE PymtMethod AS ENUM ( 'cash', 'credit', 'debit');

CREATE TABLE Client(
    cid INTEGER PRIMARY KEY,
    Name VARCHAR(20)  NOT NULL,
    streetNum INTEGER,
    street VARCHAR(20),
    city VARCHAR(20),
    country VARCHAR(20),
    creationDate DATE );


CREATE TABLE Branch(
    Bid INTEGER PRIMARY KEY,
    StreetNumber INTEGER,
    Street VARCHAR(20),
    City VARCHAR(20),
    Country VARCHAR(20),
    OpeningTime TIME,
    ClosingTime TIME
);


CREATE TABLE Employee(
    eid INTEGER PRIMARY KEY,
    startDate DATE,
    salary INTEGER NOT NULL, 
CHECK (salary > 0),
    bid INTEGER,
    workingDays CHAR(7), /*Given in format MTWTSS*/
    startTime TIME,
    endTime TIME,
FOREIGN KEY(Bid) REFERENCES Branch);
    


CREATE TABLE Product (
    PrId INTEGER PRIMARY KEY,
    Brand VARCHAR(20) NOT NULL,
    Name VARCHAR(20),
    Type ProductType NOT NULL,
    Year INTEGER,
    Available BOOLEAN NOT NULL,
    Bid INTEGER,
    FOREIGN KEY(Bid) REFERENCES Branch
);



CREATE TABLE ForRent (
    PrId INTEGER PRIMARY KEY,
    Condition VARCHAR(20),
    FOREIGN KEY(PrId) REFERENCES Product 
);


CREATE TABLE ForSale (
    prID INTEGER PRIMARY KEY,
    Condition VARCHAR(20),
    FOREIGN KEY(prID) REFERENCES Product);
    

CREATE TABLE Salesman(
    Eid INTEGER PRIMARY KEY,
    FOREIGN KEY(Eid) REFERENCES Employee
);

CREATE TABLE Manager (
	Eid INTEGER PRIMARY KEY,
	Bid INTEGER,
	FOREIGN KEY( Bid ) REFERENCES Branch,
	FOREIGN KEY( Eid ) REFERENCES Employee
);	

CREATE TABLE Fee (
    Fid INTEGER PRIMARY KEY,
    Price INTEGER,
    Duration VARCHAR(20) /*Ex : “week”, “2 hours”*/
);

CREATE TABLE Payment(
    PyId INTEGER PRIMARY KEY,
    Discnt INTEGER CHECK (Discnt >= 0 AND Discnt <= 100),
    pyDate DATE,
    Mthod PymtMethod , 
    Amount REAL CHECK ( Amount >= 0 ),
    Eid INTEGER,
    Cid INTEGER,
    FOREIGN KEY(Eid) REFERENCES Employee,
FOREIGN KEY(Cid) REFERENCES Client
);

/* Relationships */

CREATE TABLE RENTS(
CONSTRAINT RentId       PRIMARY KEY(Cid,PyId,PrId), 
/* Unsure if CONSTRAINT is necessary */

        Cid INTEGER     NOT NULL REFERENCES Client(Cid),
        PyId    INTEGER NOT NULL REFERENCES Payment(PyId),
    PrId    INTEGER     NOT NULL REFERENCES Product(PrId),

    InitCndit   CHAR(50)    NOT NULL,
    StartDate   TIMESTAMP   NOT NULL    DEFAULT   CURRENT_TIMESTAMP(2),
    EndDate TIMESTAMP   NOT NULL

);

CREATE TABLE BUYS(
    CONSTRAINT  BuyId       PRIMARY KEY(PrId, PyId),

    PrId        INTEGER     NOT NULL REFERENCES Product(PrId),
    PyId        INTEGER     NOT NULL REFERENCES Payment(PyId)

);

CREATE TABLE RATES(
    CONSTRAINT  RateId      PRIMARY KEY(Cid,Eid),

    Cid         INTEGER     NOT NULL REFERENCES Client(Cid),
    Eid         INTEGER     NOT NULL REFERENCES Employee(Eid),

    Rating      INTEGER     NULL     CHECK(Rating >= 1 and Rating <= 5)

);

CREATE TABLE PAYSFOR(
    CONSTRAINT PaysForId    PRIMARY KEY(PrId,Fid),

    PrId        INTEGER     NOT NULL REFERENCES Product(PrId),
    Fid         INTEGER     NOT NULL REFERENCES Fee(Fid)

);

