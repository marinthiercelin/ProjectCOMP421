/* View 1 */
CREATE VIEW CurrentRentals AS
SELECT Bid, pName, Brand, prCondition, InitCndit, StartDate, EndDate, RentingDuration, cid, cName
FROM Product, ForRent, RENTS /* Is product necessary? */
WHERE ForRent.PrId = RENTS.PrId
    AND StartDate <= CURRENT_TIMESTAMP(2) 
    AND EndDate > CURRENT_TIMESTAMP(2);

/* View 2 */
CREATE VIEW OpenStores AS
SELECT Bid, City, endTime, eid, eName
FROM Branch, Employee, Manager
WHERE Branch.Bid = Manager.Bid
    AND OpeningTime <= CURRENT_TIME
    AND ClosingTime > CURRENT_TIME
    AND workingDays LIKE CONCAT('%', EXTRACT(DOW FROM TIMESTAMP CURRENT_TIMESTAMP), '%')
    AND startTime <= CURRENT_TIME
    AND endTime > <= CURRENT_TIME;
/* Is the fact that the manager is working at a currently open store implicit? */
/* We can change this view to current employees */


/* Analytic 1 */
CREATE VIEW ProfitableUnhappyClients /* Add no discount condition */
SELECT 

SELECT 

/* Analytic 2 */
/* Employees/Managers with seniority */

/* Analytic 3 */
/* Rent Items prone to degradation */




/* conditions on updatable views (SQL docs) :
https://www.postgresql.org/docs/current/static/sql-createview.html#SQL-CREATEVIEW-UPDATABLE-VIEWS
*/