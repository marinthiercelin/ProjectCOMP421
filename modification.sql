/* 1 delete all clients that 
were created more than 3years ago 
and that haven't made any payement in the last year, (check that they have no currently going renting) delete all payments, and ratings associated with them*/

/* 2 delete all salesman that have a rating below 1, deletes all their ratings and put all their payments to the best seller */

/* 3  makes the 1-year fees 10% more expensive, on all the skis that are less than one years old */


/* 4 delete all snowboots for rent that are more than 4 years old (check that they are not currently rented) 
	check that no fees have 0 products, delete the ones who do
*/

SELECT *
FROM Product p, ForRent f
WHERE p.PrId = f.PrId AND p.pType = 'Snowboots' AND p.Available = true AND 2017-p.pYear > 3;

DELETE FROM Product p
USING Forrent f
WHERE p.PrId = f.PrId AND p.pType = 'Snowboots' AND p.Available = true AND 2017-p.pYear > 3; 

/* 5 
look for the cheapest skis (and possibly most recent) available in a certain branch and make a client buy them starting now
and make them pay for it to a salesman 
*/

CREATE VIEW temp1 AS
SELECT s.PrId PrId, s.Price Price, p.pYear pYear
FROM ForSale s, Product p
WHERE p.Bid = 1 AND s.PrId = p.PrId AND p.pType = 'Ski' AND p.Available = true 
ORDER BY Price ASC, pYear DESC
LIMIT 1;

WITH temp1
INSERT INTO Payment VALUES(12, 0, 'cash', temp1.Price, 2, 4);/* PyId dscnt, mthd, amnt, eid, cid*/





/* 6 
merge 2 branches (bid1, bid2) in bi1 delete the manager of branch bid2 and the branch bid2  
*/

SELECT *
FROM Employee
WHERE Bid = 2;

SELECT *
FROM Product
WHERE Bid = 2;

UPDATE Product
SET Bid = 1 
WHERE  Bid = 2;

UPDATE Employee
SET Bid = 1
WHERE Bid = 2;

SELECT *
FROM Manager
WHERE Bid = 2;

DELETE FROM Employee e
USING Manager man
WHERE e.eid = man.eid and man.Bid = 2;

SELECT *
FROM Branch
WHERE Bid = 2;

DELETE FROM Branch 
WHERE Bid = 2;









