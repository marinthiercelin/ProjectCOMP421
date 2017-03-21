
import java.sql._
import java.util._

import scala.annotation.tailrec
import scala.io.StdIn._

/**
  * Created by tarek on 16/03/17.
  */
object DbConnection extends App{
  main()

  def main(){

    //Try to load the driver
    try{
      DriverManager.registerDriver(new org.postgresql.Driver())
    } catch {
      case ex : SQLException => println(ex.getMessage)
    }


    //Url for connection
    val url = "jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421"

    //Establish Connection
    val con : Connection = DriverManager.getConnection(url, "cs421g21", "Grp42117;")

    //createPayment(con, 1, 1, 1)
    //makeBrandDiscount(con)
    proposeOptionsAndExecute(con)
    con.close()
  }

  @tailrec
  def proposeOptionsAndExecute(connection: Connection) : Unit = {
    println("Choose an Option : ")
    println("1. Create a new Payment ")
    println("2. See all the payments of a client")
    println("3. Get a ranking of the Salesman of a branch, on average rating")
    println("4. Make a discount on all the product for sale of a brand (not already sold) ")
    println("5. List the total amount received by each branch over a period of time")
    println("6. List all the available product for rent that have a given condition")
    println("7. quit \n")
    print(" Your choice : ")
    try{
      scala.io.StdIn.readInt() match {
        case 1 =>
        case 2 =>
        case 3 =>
        case 4 => makeBrandDiscount(connection)
        case 5 => getAllPaymentsOfBranchOverPeriod(connection)
        case 6 =>
        case 7 => println("Good Bye !") ; return
      }
    }catch{
      case ex : SQLException => {
        println(" Code : " + ex.getErrorCode)
        println(ex.getMessage)
      }
      case ex : Exception => println(ex.getMessage)
    }
    proposeOptionsAndExecute(connection)
  }



  /**
    * Make a discount (in percent) on all the product for sale of a certain brand
    */
  def makeBrandDiscount(connection: Connection): Unit ={
    var brand = askBrand()
    var percentage = 1 - (askPercentage() / 100.0)

    def displayResult(res : ResultSet): Unit ={
      while(res.next()){
        var prId = res.getInt("prId")
        var price = res.getInt("price")
        var brand = res.getString("brand")
        println(prId + " " + price + " " + brand)
      }
    }


    // this is for display
    val sqlQuery =
      " SELECT fS.prId AS prId, fS.price AS price, P.brand AS brand " +
        "FROM forSale fS, product P " +
        "WHERE fS.prID = P.prId AND P.available = true "+
        "AND P.brand = '" + brand + "';"

    // this is for modification
    val sqlModification = "UPDATE forSale AS fS " +
      "SET price = " +  percentage + " * price " +
      "FROM Product AS P " +
      "WHERE fS.prID = P.prId AND P.available = true "+
      "AND P.brand = '" + brand + "';"

    try {
      var beforeQuery = connection.createStatement()
      var resBefore = beforeQuery.executeQuery(sqlQuery)
      println("Before : ")
      displayResult(resBefore)

      var sqlUpdate = connection.createStatement()
      var updatedRows = sqlUpdate.executeUpdate(sqlModification)
      println("Updated : " + updatedRows)

      var afterQuery = connection.createStatement()
      var resAfter = afterQuery.executeQuery(sqlQuery)
      print("After : ")
      displayResult(resAfter)
      beforeQuery.close()
      sqlUpdate.close()
      afterQuery.close()
    }catch {
      case ex : SQLException =>  print("Code : " + ex.getErrorCode + " Message : " + ex.getMessage )
    }


  }

  def askBrand() : String = {
    print("Enter the name of the brand : ")
    scala.io.StdIn.readLine()
  }

  @tailrec
  def askPercentage() : Int = {
    print("Enter the percentage of discount : ")
    var discount = scala.io.StdIn.readInt()
    if (discount >= 0 && discount <= 100) discount else askPercentage()
  }

  def getAllPaymentsOfBranchOverPeriod(connection: Connection): Unit ={
    var branchId = askBranchId()
    var period : Period = askPeriodOfTime()
    println("Date 1 :" + period._1.toString)
    println("Date 2 :" + period._2.toString)
  }

  @tailrec
  def askBranchId(): Int ={
    print("Enter the id of the branch : ")
    try {
      readInt()
    }catch{
      case ex : Exception =>
        println("Wrong format, input an integer")
        askBranchId()
    }
  }


  type Period = (java.sql.Date, java.sql.Date)


  def askPeriodOfTime() : Period = {
    println("Give the period : ")
    println("Enter the start date : ")
    var startDate =  askDate()
    println("Enter the end date :")
    var endDate = askDate()
    (startDate,endDate)
  }

  def askDate() : java.sql.Date = {
    print("Day (DD): ")
    var day = readInt()
    print("Month (MM): ")
    var month  = readInt()
    print("Year (YYYY):")
    var year = readInt()

    var calendar = Calendar.getInstance()
    calendar.set(year, month - 1, day, 0, 0)
    new java.sql.Date(calendar.getTimeInMillis())
  }


}
