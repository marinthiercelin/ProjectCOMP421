﻿/* 1 delete all clients that 
were created more than 7years ago 
and that haven't made any payement in the last 2 years , (check that they have no currently going renting) delete all payments, and ratings associated with them*/

/*SELECT *
FROM client 
WHERE CURRENT_DATE - 6*365 >  creationDate  AND cid NOT IN (

SELECT c.cid
FROM client c, payment p
WHERE c.cid = p.cid AND CURRENT_DATE - 2*365  < p.pyDate

) AND cid NOT IN (

SELECT c.cid
FROM client c , rents r 
WHERE c.cid = r.cid AND r.endDate >= CURRENT_DATE
);*/

DELETE 
FROM client 
WHERE CURRENT_DATE - 6*365 >  creationDate  AND cid NOT IN (

SELECT c.cid
FROM client c, payment p
WHERE c.cid = p.cid AND CURRENT_DATE - 2*365  < p.pyDate

) AND cid NOT IN (

SELECT c.cid
FROM client c , rents r 
WHERE c.cid = r.cid AND r.endDate >= CURRENT_DATE
);


/* 2 give a 10% raise to all the salesman that have a rating average greater than 4.5/5 */

/*SELECT eid, ename, salary FROM Employee 
WHERE eid IN (
	SELECT s.eid
	FROM Employee e, Salesman s, Rates r
	WHERE s.eid = r.eid
	GROUP BY (s.eid)
	HAVING AVG(rating) >= 4.5
)
ORDER BY salary;
*/

UPDATE Employee
SET salary = 1.1*salary
WHERE eid IN (

	SELECT s.eid
	FROM Salesman s, Rates r
	WHERE s.eid = r.eid
	GROUP BY (s.eid)
	HAVING AVG(rating) >= 4.5

);



/* 3 delete all snowboots for rent that are more than 4 years old (check that they are not currently rented) 
	check that no fees have 0 products, delete the ones who do
*/

/*
SELECT *
FROM Product p, ForRent f
WHERE p.PrId = f.PrId AND p.pType = 'Snowboots' AND p.Available = true AND 2017-p.pYear > 3;
*/ 

DELETE FROM Product p
USING Forrent f
WHERE p.PrId = f.PrId 
	AND p.pType = 'Snowboots' 
	AND p.Available = true 
	AND EXTRACT(YEAR FROM CURRENT_DATE) - p.pYear > 3; 



/* 4
merge 2 branches (bid1, bid2) in bi1 delete the manager of branch bid2 and the branch bid2  
*/

/*
SELECT *
FROM Employee
WHERE Bid = 2;

SELECT *
FROM Product
WHERE Bid = 2;

SELECT *
FROM Manager
WHERE Bid = 2;

SELECT *
FROM Branch
WHERE Bid = 2;
*/


UPDATE Product
SET Bid = 1 
WHERE  Bid = 2;

UPDATE Employee
SET Bid = 1 
WHERE Bid = 2;

DELETE FROM Employee e
USING Manager man
WHERE e.eid = man.eid and man.Bid = 2;

DELETE FROM Branch 
WHERE Bid = 2;








