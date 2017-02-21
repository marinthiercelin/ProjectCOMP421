/* Entities */
/* TODO : Check all the foreign key for their ON DELETE and ON UPDATE statements tell me if you agree*/ 

CREATE TYPE ProductType AS ENUM('Ski', 'Snowboard', 'Poles', 'SkiBoots', 'Snowboots', 'Helmets', 'Skiwear', 'Accessories');

CREATE TYPE PymtMethod AS ENUM ( 'cash', 'credit', 'debit');


CREATE TYPE RentingDuration AS ENUM ('1_HOUR', '1_DAY', '2_DAYS', '1_WEEK', '1_MONTH','1_YEAR');


CREATE TABLE Client(
    cid INTEGER PRIMARY KEY,
    cName VARCHAR(30)  NOT NULL,
    streetNum INTEGER,
    street VARCHAR(30),
    city VARCHAR(30),
    country VARCHAR(20),
    creationDate DATE DEFAULT CURRENT_DATE
);


CREATE TABLE Branch(
    Bid INTEGER PRIMARY KEY,
    StreetNumber INTEGER,
    Street VARCHAR(30),
    City VARCHAR(30),
    Country VARCHAR(20),
    OpeningTime TIME,
    ClosingTime TIME
);


CREATE TABLE Employee( /*Add Names to employees*/
    eid INTEGER PRIMARY KEY,
    eName VARCHAR(30) NOT NULL,
    startDate DATE DEFAULT CURRENT_DATE,
    salary INTEGER NOT NULL, CHECK (salary > 0),
    bid INTEGER,
    workingDays CHAR(7), /*Given in format MTWTFSS*/
    startTime TIME,
    endTime TIME,
    FOREIGN KEY(Bid) REFERENCES Branch ON DELETE CASCADE ON UPDATE CASCADE
);
    


CREATE TABLE Product (
    PrId INTEGER PRIMARY KEY,
    Brand VARCHAR(20) NOT NULL,
    pName VARCHAR(20),
    pType ProductType NOT NULL,
    pYear INTEGER,
    Available BOOLEAN NOT NULL,
    Bid INTEGER,
    FOREIGN KEY(Bid) REFERENCES Branch ON DELETE CASCADE ON UPDATE CASCADE
);



CREATE TABLE ForRent (
    PrId INTEGER PRIMARY KEY,
    prCondition VARCHAR(20) NOT NULL,
    FOREIGN KEY(PrId) REFERENCES Product ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE ForSale (
    prID INTEGER PRIMARY KEY,
    Price INTEGER NOT NULL CHECK(Price >= 0),
    FOREIGN KEY(prID) REFERENCES Product ON DELETE CASCADE ON UPDATE CASCADE
);
    

CREATE TABLE Salesman(
    Eid INTEGER PRIMARY KEY,
    FOREIGN KEY(Eid) REFERENCES Employee ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Manager (
	Eid INTEGER PRIMARY KEY,
	Bid INTEGER NOT NULL,
	FOREIGN KEY( Bid ) REFERENCES Branch ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY( Eid ) REFERENCES Employee ON DELETE CASCADE ON UPDATE CASCADE
);	

CREATE TABLE Fee (
    Fid INTEGER PRIMARY KEY,
    Price INTEGER,
    Duration RentingDuration /*Ex : “week”, “2 hours”*/
);

CREATE TABLE Payment(
    PyId INTEGER PRIMARY KEY,
    Discnt INTEGER CHECK (Discnt >= 0 AND Discnt <= 100),
    pyDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    Method PymtMethod NOT NULL, 
    Amount REAL CHECK ( Amount >= 0 ) NOT NULL,
    Eid INTEGER NOT NULL,
    Cid INTEGER NOT NULL,
    FOREIGN KEY(Eid) REFERENCES Employee ON DELETE SET NULL ON UPDATE CASCADE ,
    FOREIGN KEY(Cid) REFERENCES Client ON DELETE SET NULL ON UPDATE CASCADE
);

/* Relationships */

CREATE TABLE RENTS(
    CONSTRAINT RentId   PRIMARY KEY(Cid,PyId,PrId), 
    Cid     INTEGER     NOT NULL REFERENCES Client(Cid) ON DELETE CASCADE ON UPDATE CASCADE,
    PyId    INTEGER     NOT NULL REFERENCES Payment(PyId) ON DELETE CASCADE ON UPDATE CASCADE,
    PrId    INTEGER     NOT NULL REFERENCES ForRent(PrId) ON DELETE CASCADE ON UPDATE CASCADE,

    InitCndit   VARCHAR(50)    NOT NULL,
    StartDate   TIMESTAMP   NOT NULL    DEFAULT   CURRENT_TIMESTAMP(2),
    EndDate     TIMESTAMP   NOT NULL

);

CREATE TABLE BUYS(
    CONSTRAINT  BuyId       PRIMARY KEY(PrId, PyId),

    PrId        INTEGER     NOT NULL REFERENCES ForSale(PrId) ON DELETE CASCADE ON UPDATE CASCADE, /*Modified to ForSale to be more specific*/
    PyId        INTEGER     NOT NULL REFERENCES Payment(PyId) ON DELETE SET NULL ON UPDATE CASCADE /* TODO : if a payment is deleted, the product should be still considered as sold right ?*/

);

CREATE TABLE RATES(
    CONSTRAINT  RateId      PRIMARY KEY(Cid,Eid),

    Cid         INTEGER     REFERENCES Client(Cid) ON DELETE SET NULL ON UPDATE CASCADE, /* TODO : we want to keep the ratings even if the client gets deleted, right ?*/
    Eid         INTEGER     NOT NULL REFERENCES Employee(Eid) ON DELETE CASCADE ON UPDATE CASCADE,
    Rating      INTEGER     NULL     CHECK(Rating >= 1 and Rating <= 5)

);

CREATE TABLE PAYSFOR(
    CONSTRAINT PaysForId    PRIMARY KEY(PrId,Fid),
    PrId        INTEGER     NOT NULL REFERENCES ForRent(PrId) ON DELETE CASCADE ON UPDATE CASCADE, /*Modified to ForRent to be more specific*/
    Fid         INTEGER     NOT NULL REFERENCES Fee(Fid) ON DELETE CASCADE ON UPDATE CASCADE 

);

