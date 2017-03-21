SELECT fS.prId AS prId, fS.price AS price, P.brand AS brand 
FROM forSale fS, product P 
WHERE fS.prID = P.prId AND P.available = true 
AND P.brand = 'Rossignol';

UPDATE forSale AS fS 
SET price = 0.6* price 
FROM Product AS P 
WHERE fS.prID = P.prId AND P.available = true 
AND P.brand = 'Rossignol';

SELECT *
FROM Payment 
WHERE DATE_TRUNC(PyDate) > '2014-08-06'
