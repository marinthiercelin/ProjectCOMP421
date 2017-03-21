
import java.sql._
import java.util._

import scala.List
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
    println("\nChoose an Option : ")
    println("1. Create a new Payment ")
    println("2. See all the payments of a client")
    println("3. Get a ranking of the Salesman of a branch, on average rating")
    println("4. Make a discount on all the product for sale of a brand (not already sold) ")
    println("5. Rank the branches by the amount they received over a period of time")
    println("6. List all the available product for rent that have a given condition")
    println("7. quit ")
    try{
      var choice = askInteger(1,7,"Input your choice : ")
      choice match {
        case 1 =>
        case 2 =>
        case 3 =>
        case 4 => makeBrandDiscount(connection)
        case 5 => orderBranchesByPaymentOverPeriod(connection)
        case 6 =>
        case 7 => println("Good Bye !") ; return
        case _ => println("You need to choose between options 1 to 7 !")
      }
    }catch{
      case ex : java.lang.NumberFormatException =>
        println("You need to input an integer !")
      case ex : SQLException => {
        println(" Code : " + ex.getErrorCode)
        println(ex.getMessage)
      }
      case ex : Exception => ex.printStackTrace()
    }
    proposeOptionsAndExecute(connection)
  }


  /**
    * Make a discount (in percent) on all the product for sale of a certain brand
    */
  def makeBrandDiscount(connection: Connection): Unit ={
    println("\nThis section allow you to create a discount\n" +
      " over all the products for sale of a specific brand")
    var brand = askBrand()
    var percentage = 1 - (askInteger(0, 100, "Input a discount percentage : ") / 100.0)

    def displayResult(resBefore : ResultSet, resAfter : ResultSet ): Unit = {
      def getListFromResultSet(res : ResultSet): List[(Int, Int)] ={
        if(res.next()){
          var prId = res.getInt("prId")
          var price = res.getInt("price")
          (prId,price) :: getListFromResultSet(res)
        }else{
          List[(Int,Int)]()
        }
      }
      var before = getListFromResultSet(resBefore)
      var after = getListFromResultSet(resAfter)
      for{
        (prId, price) <- before
        (prIdAfter, priceAfter) <- after
        if(prId == prIdAfter)
      } println("Product : " + prId + " Price before : " + price + " after : " + priceAfter)
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
      var sqlUpdate = connection.createStatement()
      var updatedRows = sqlUpdate.executeUpdate(sqlModification)
      println("\nUpdated : " + updatedRows+"\n")
      var afterQuery = connection.createStatement()
      var resAfter = afterQuery.executeQuery(sqlQuery)
      displayResult(resBefore,resAfter)
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


  def orderBranchesByPaymentOverPeriod(connection: Connection): Unit ={
    println("\nThis section returns a ranking of all branches" +
      " based on their revenue over a period \n")

    var period : Period = askPeriodOfTime()

    def displayResult(result : ResultSet): Unit = {
      println("\nRanking : ")
      var i = 0
      while(result.next()){
       i += 1
       var branchId = result.getInt("bId")
       var total = result.getInt("total")
       println( i + " : Branch " + branchId + " with " + total + "$ over that period ")
      }
    }

    try {
      var statement = connection.createStatement()
      var result = statement.executeQuery("SELECT e.bid AS bId , SUM(p.amount) AS total " +
        "FROM Payment p, Employee e " +
        "WHERE DATE(p.PyDate) > '"+ period._1.toString + "' " +
        "AND DATE(p.PyDate) < '"+ period._2.toString + "' " +
        "AND e.eid = p.eid " +
        "GROUP BY e.bid " +
        "ORDER BY total DESC;")
      displayResult(result)
      statement.close()
    } catch {
      case  ex : SQLException => ex.printStackTrace()
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
    var day = askInteger(1, 31, "Give a day (DD) : ")
    var month = askInteger(1,12, "Give a month (MM) : ")
    var year = askInteger(0,3000, "Give a year (YYYY) : ")

    var calendar = Calendar.getInstance()
    calendar.set(year, month - 1, day, 0, 0)
    new java.sql.Date(calendar.getTimeInMillis())
  }

  def askInteger(bound1 : Int, bound2 : Int, message : String) : Int = {
    var int = 0
    println(message)
    try {
      int = readInt()
      if (int >= bound1  && int <= bound2){
        int
      } else{
        println("It has to be between "+ bound1 + " and "+ bound2 + " !")
        askInteger(bound1, bound2, message)
      }
    } catch {
      case  ex : java.lang.NumberFormatException =>
        println("You need to input an integer ! ")
        askInteger(bound1,bound2, message)
    }
  }


}
