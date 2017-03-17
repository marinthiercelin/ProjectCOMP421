/* View 1 */
CREATE VIEW CurrentRentals AS
SELECT Bid, pName, Brand, prCondition, InitCndit, StartDate, EndDate
FROM Product, ForRent, RENTS
WHERE ForRent.PrId = RENTS.PrId
    AND ForRent.PrId = Product.PrId
    AND StartDate <= CURRENT_TIMESTAMP(2) 
    AND EndDate > CURRENT_TIMESTAMP(2);

/* View 1 Use Case */
SELECT * FROM CurrentRentals
WHERE Bid = 7;

/* View 2 */
CREATE VIEW WorkingManagers AS
SELECT Manager.Bid, Employee.eid, eName, endTime
FROM Employee, Manager
WHERE Employee.Eid = Manager.Eid
    AND workingDays LIKE CONCAT('%', EXTRACT(DOW FROM CURRENT_TIMESTAMP), '%')
    AND startTime <= CURRENT_TIME
    AND endTime > CURRENT_TIME;

/* View 2 Use Case */
SELECT *
FROM WorkingManagers, Branch
WHERE Manager.Bid = Branch.Bid
AND City = 'Dubai'

/* Analytical Query 1 */
/* High-Volume clients with a low average discount and low average rating who haven't made a purchase in the last 3 months */
SELECT DISTINCT Client.Cid, Client.cName
FROM Client, RATES
WHERE RATES.Cid = Client.Cid
    AND Client.Cid IN (
        SELECT Cid
        FROM Payment
        WHERE pyDate <= CURRENT_TIMESTAMP - INTERVAL '3 months'
        GROUP BY Cid
        HAVING COUNT(*) >= 10 AND AVG(Discnt) < 15)
    AND Client.Cid IN (
        SELECT Cid
        FROM RATES
        GROUP BY Cid
        HAVING AVG(Rating) < 3);

/* conditions on updatable views (SQL docs) :
https://www.postgresql.org/docs/current/static/sql-createview.html#SQL-CREATEVIEW-UPDATABLE-VIEWS
*/