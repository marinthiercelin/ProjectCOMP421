/* View 1 */
CREATE VIEW RentedProducts AS
SELECT Bid, pName, Brand, prCondition, InitCndit, StartDate, EndDate, RentingDuration, cid, cName
FROM ForRent, RENTS
WHERE 



/* View 2 */




/* conditions on updatable views (SQL docs) :
https://www.postgresql.org/docs/current/static/sql-createview.html#SQL-CREATEVIEW-UPDATABLE-VIEWS
*/