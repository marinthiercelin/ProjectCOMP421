/* #1: Get the names and brands of the products that are overdue and the customers that hold them */

SELECT p.Brand, p.pName, c.cName
FROM RENTS r JOIN Product p
ON (r.PrId = p.PrId AND r.EndDate > CURRENT_DATE)
JOIN Client c
ON (r.Cid = c.Cid); 

/* #2: Get the countries of custumers who have spent $500 buying products at the stores, show the number of people of each country group,
rank them according to this number  */

CREATE VIEW CustmrWithAmt(Cid, amt)
AS SELECT p.Cid, SUM(p.Amount) as amt
FROM BUYS b, Payment p
WHERE  b.PyId = p.PyId
GROUP BY p.Cid;

SELECT c.country, COUNT(*) as num_people
FROM CustmrWithAmt cm, Client c
WHERE cm.Cid = c.Cid AND cm.amt > 500
GROUP BY c.country
ORDER BY num_people;

/* #3: Get the types of the 'forRent' products where over 20 of the products are of condition "Poor" */

SELECT p.pType
FROM ForRent f, Product p
WHERE f.PrId = p.PrId AND f.prCondition = 'poor'
GROUP BY p.pType
HAVING COUNT(*) >= 20;

/* #4: Get the brands of products which the total revenu is over 1000 in the year 2016, rank them according to revenu, show revenu too  */

SELECT pr.Brand, SUM(py.Amount) as amt
FROM BUYS b JOIN Product pr
ON b.PrId = pr.PrId
JOIN Payment py
ON b.PyId = py.PyId AND to_char(py.pyDate, 'YYYY') = '2016'
GROUP BY pr.Brand
HAVING SUM(py.Amount) > 1000
ORDER BY amt;

/* #5: For each type of 'forRent' product, get the age of the majority  */

SELECT f.pType, 