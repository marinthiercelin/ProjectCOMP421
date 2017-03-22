
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
    println("3. Get the average rankings of the employees of a branch")
    println("4. Make a discount on all the product for sale of a brand (not already sold) ")
    println("5. Rank the branches by the amount they received over a period of time")
    println("6. List all the available product for rent that have a given condition")
    println("7. quit ")
    try{
      var choice = askInteger(1,7,"Input your choice : ")
      choice match {
        case 1 => createPayment(connection)
        case 2 => getPaymentsofClient(connection)
        case 3 => getAverageRankingOfEmployees(connection)
        case 4 => makeBrandDiscount(connection)
        case 5 => orderBranchesByPaymentOverPeriod(connection)
        case 6 => getAllRentalProductOfCondition(connection)
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

  def getAllRentalProductOfCondition(connection : Connection): Unit ={
    def displayResult(res : ResultSet): Unit ={
      while(res.next()){
        var prId = res.getInt("prId")
        var brand = res.getString("brand")
        var name = res.getString("name")
        var condition = res.getString("condition")
        var branchId = res.getInt("bid")
        println("Product : "+ prId + " Brand :" + brand + " Name : " + name + " " +
          "Condition : " + condition + " Branch : " + branchId)

      }
    }
    println("\nThis section displays all the rental equipment of a certain condition in a given branch\n")
    var branchId = askInteger(1, Integer.MAX_VALUE, "Input the branch id : ")
    var condition = askCondition()
    try{
      var statement = connection.createStatement()
      var res = statement.executeQuery("SELECT fR.prId AS prId, P.brand AS brand, P.pname AS name, " +
        "fR.PrCondition AS condition, P.bid AS bid " +
        "FROM forRent fR , product P " +
        "WHERE P.prId = fr.prId " +
        "AND P.bid = "+ branchId + " " +
        "AND P.available = true " +
        "AND fR.PrCondition = '" + condition +"' " +
        "ORDER BY prId;")
      displayResult(res)
      statement.close()
    }catch{
      case ex : java.sql.SQLException => print("Code : " + ex.getErrorCode + " Message : " + ex.getMessage )
      case ex : Exception => ex.printStackTrace()
    }
  }

  def askCondition() : String = {
    println("Different conditions : ")
    println("1. Good")
    println("2. Medium")
    println("3. Bad")
    askInteger(1,3,"Input your choice : ") match {
      case 1 => "Good"
      case 2 => "Medium"
      case 3 => "Bad"
    }
  }

  def displayPaymentSet(resultSet: ResultSet, clientId : Int): Unit ={
    val rsmd = resultSet.getMetaData()
    val numCol = rsmd.getColumnCount()

    println("Sorted payments for client " + clientId+'\n')


    while (resultSet.next()){
      for (i <- 1 to numCol){

        i match {
          case 1 => print("Payment id : "+resultSet.getInt(i) + " ")
          case 2 => print("Discount : "+resultSet.getInt(i) + "% ")
          case 3 => print("Payment timestamp : " +resultSet.getTimestamp(i).toString + " ")
          case 4 => print("Payment method : " + resultSet.getString(i) + " ")
          case 5 => print("Total Amount : " +resultSet.getFloat(i) + " ")
          case 6 => print("Employee ID : "+ +resultSet.getInt(i) + " ")
          case 7 =>
        }
      }
      println()

    }
  }

  //Returns a result set
  def getPaymentsofClient(connection: Connection) : Unit ={
    val stat = connection.createStatement()

    print("Please enter a client name : ")
    val cname = readLine()
    val clientId = getClientIdFromName(cname, stat)

    val sqlStatement = "SELECT * FROM payment WHERE cid = "+clientId + " ORDER BY pydate"



    try {
      val resultSet = stat.executeQuery(sqlStatement)
      displayPaymentSet(resultSet, clientId)
    } catch {
      case ex : SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }

    stat.close()
  }


  def displayAverageRankings(resultSet: ResultSet, branchid : Int): Unit ={
    val rsmd = resultSet.getMetaData()
    val numCol = rsmd.getColumnCount()

    println("Sorted employee averages for branch " + branchid + "\n")

    while (resultSet.next()){
      for (i<- 1 to numCol){
        i match {
          case 1 => println("Employee id : " + resultSet.getInt(i) +" ")
          case 2 => println("Employee name : " + resultSet.getString(i) + " ")
          case 3 => println("Average rating : " + resultSet.getFloat(i) + " ")
        }
      }
      println()
    }

  }

  def getAverageRankingOfEmployees( connection: Connection) : Unit = {
    val statement = connection.createStatement()

    print("Enter the branch ID : ")
    val branchid = readInt()

    val sqlQuery = "SELECT E.eid, E.ename,  A.rating FROM employee E, (" +
      "SELECT eid, AVG(rating) AS rating " +
      "FROM rates " +
      "GROUP By eid) AS A " +
      "WHERE E.eid = A.eid AND E.bid = " + branchid + " ORDER BY A.rating DESC"

    try {
      val resultSet = statement.executeQuery(sqlQuery)
      displayAverageRankings(resultSet, branchid)
    } catch {
      case ex : SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }
    statement.close()
  }

  def getNextCid(statement: Statement): Int = {
    var cid = 1
    try {
      val resultSet = statement.executeQuery("SELECT MAX(cid) FROM client")
      resultSet.next()
      cid = resultSet.getInt(1) + 1
    } catch {
      case ex: SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }
    cid
  }

  def getClientIdFromName(cname : String, statement: Statement): Int ={

    val sqlQuery = "SELECT cid FROM client WHERE cname = \'" + cname + "\'"

    var cid = 0
    try {
      val resultSet = statement.executeQuery(sqlQuery)
      if (resultSet.next()) {
        cid = resultSet.getInt(1)
      } else {
        throw new SQLException("Client ID not found.")
      }

    } catch {
      case ex : SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }

    cid


  }

  //Creates a new Client
  def newClient(cName : String, streetNum : Int, street: String, city: String, country : String, statement: Statement): Int ={
    val cid: Int = getNextCid(statement)

    val sqlQuery = "INSERT INTO client VALUES("+ cid + ",\'" + cName + "\',\'" + streetNum + "\',\'" + street + "\',\'" + city + "\',\'"+ country + "\')"

    try {
      statement.executeUpdate(sqlQuery)
    } catch {
      case ex : SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }

    println("Client created successfully.")

    cid
  }

  def getNextPaymentId(statement: Statement): Int ={
    var pyid = 1
    try {
      val resultSet = statement.executeQuery("SELECT MAX(pyid) FROM payment")
      resultSet.next()
      pyid = resultSet.getInt(1) + 1
    } catch {
      case ex: SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }
    pyid
  }

  def promptEmployeeId(): Int = {
    print("Please enter the employee ID :")
    val id = readInt()
    id
  }

  def promptClientId(statement: Statement): Int ={
    println("Is the client already registered ?(y/n)")
    var ans = readChar()

    while (ans != 'y' && ans != 'n'){
      println("Please enter a valid answer (y/n):")
      ans = readChar()
    }

    print("Enter the client's name : ")
    val name = readLine()

    if (ans == 'n') {

      print("Enter the client's street number : ")
      val snum = readInt()
      print("Enter the street name : ")
      val sname = readLine()
      print("Enter the city of residence : ")
      val city = readLine()
      print("Enter the country of residence : ")
      val country = readLine()
      newClient(name, snum, sname, city, country, statement)
    } else {
      getClientIdFromName(name, statement)

    }



  }

  def getBidFromEid(eid : Int, statement: Statement): Int = {
    val query = "SELECT bid FROM employee WHERE eid="+eid

    var bid = 0
    try {
      val res = statement.executeQuery(query)
      if (res.next()){
        bid = res.getInt(1)
      }
    } catch {
      case ex :SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }
    bid
  }

  def createPayment(connection: Connection): Unit ={
    val statement = connection.createStatement()

    val eid = promptEmployeeId()
    val cid = promptClientId(statement)
    val bid = getBidFromEid(eid, statement)

    print("Enter the payment method (cash, debit or credit) : ")
    val mthd = readLine()

    println()

    val pyid = getNextPaymentId(statement)
    var choice = 0
    var amount = 0f
    var statements : List[String] = List()



    while (choice != 3) {
      println("What would you like to do now ? : \n 1.Add a for sale product \n 2.Add a for rent product \n 3.Finish payment\n")
      print("Your choice : ")
      choice = readInt()

      choice match {
        case 1 => {val res = addForSaleProduct(bid, statement, pyid)
          amount = amount + res._1
          statements = res._2 ++ statements}
        case 2 => { val res = addForRentProduct(bid, statement, pyid, cid)
          amount = amount + res._1
          statements = res._2 ++ statements
        }
        case 3 =>
        case _ => println("Please enter a valid option.")
      }
    }

    if (statements.isEmpty){
      println("Payment is empty, aborting creation..")
    } else {

      print("Total is " + amount+"$ .What would be the discount percentage ? : ")
      val discount = readInt()

      val paymentQuery = "INSERT INTO payment VALUES("+pyid+','+discount+",DEFAULT,\'"+mthd+"\',"+amount+','+eid+','+cid+")"

      statements = paymentQuery :: statements

      try{
        statements.foreach(statement.executeUpdate(_))
      } catch {
        case ex : SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
      }
    }


    println("Payment created successfully.\n")
    statement.close()
  }

  //Displays all available for sale product and returns a list of [prodID, price]
  def displayAvailableForSaleProd(statement: Statement, bid : Int): List[(Int, Int)] ={
    val query = "SELECT * FROM product P, forsale F WHERE P.prid = F.prid AND P.available AND P.bid=" + bid
    var productIds : List[(Int, Int)] = List()

    println("List of available forsale products in branch :\n")
    try {
      val resultSet = statement.executeQuery(query)

      while (resultSet.next()){
        productIds = (resultSet.getInt(1), resultSet.getInt(9)) :: productIds
        for (i <- 1 to 9){
          i match {
            case 1 => print("ID : " + resultSet.getInt(i) + " ")
            case 2 => print("Brand : " + resultSet.getString(i) + " ")
            case 4 => print("Type : " + resultSet.getString(i) + " ")
            case 9 => print("Price : " + resultSet.getInt(i) + "$ ")
            case _ =>
          }
        }
        println()
      }
    } catch {
      case ex : SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }
    //statement.close()
    productIds
  }


  //Creates a Buys relationship between the product and the payment, and sets the product to unavailable
  def addForSaleProduct(bid : Int,statement: Statement, pyid : Int): (Float,List[String]) ={
    println("Select a product ID from the following list\n")
    val availableIds = displayAvailableForSaleProd(statement, bid)

    print("Product ID : ")

    var productId = readInt()

    while(!availableIds.exists(_._1 == productId)){
      println("Please select a product ID from the list : ")
      productId = readInt()
    }

    val price = availableIds.find(_._1==productId).get._2

    println("Product added successfully.\n")

    (price, List("INSERT INTO buys VALUES("+productId + ',' + pyid +")", "UPDATE product SET available=false WHERE prid="+productId))

  }

  //Returns a list of available for rent products in the given branch and returns a list of (prid, condition)
  def displayAvailableForRentProducts(statement: Statement, bid : Int) : List[(Int, String)] = {
    val query = "SELECT * FROM product P, forrent F WHERE P.prid = F.prid AND P.available AND P.bid=" + bid
    var productIds : List [(Int, String)] = List()

    println("List of all available for rent products in branch : \n")

    try {
      val resultSet = statement.executeQuery(query)

      while (resultSet.next()){
        productIds = (resultSet.getInt(1),resultSet.getString(9))  :: productIds
        for (i <- 1 to 9){
          i match {
            case 1 => print("ID : " + resultSet.getInt(i) + " ")
            case 2 => print("Brand : " + resultSet.getString(i) + " ")
            case 4 => print("Type : " + resultSet.getString(i) + " ")
            case 9 => print("Condition : " + resultSet.getString(i) + " ")
            case _ =>
          }
        }
        println()
      }
    } catch {
      case ex : SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }
    println()
    productIds

  }

  def displayFeesForProduct(statement: Statement, prodId : Int) : List[(Int,Int, String)]= {
    val query = "SELECT * FROM paysfor P, fee F WHERE P.fid=F.fid AND P.prid="+prodId

    var fees : List[(Int, Int,  String)] = List()
    try {
      val resultSet = statement.executeQuery(query)

      while (resultSet.next()){
        fees = (resultSet.getInt(2), resultSet.getInt(4), resultSet.getString(5)) :: fees
        for (i<- 1 to 5){

          i match {
            case 2 => print("Fee ID : " + resultSet.getInt(i) + " ")
            case 4 => print("Price : " + resultSet.getInt(i) + "$ ")
            case 5 => print("Duration : " + resultSet.getString(i) + " ")
            case _ =>
          }

        }
        println()
      }
    } catch {
      case ex : SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }
    print('\n')
    fees
  }

  def addForRentProduct(bid : Int, statement: Statement, pyid : Int, cid : Int) : (Int, List[String]) = {
    println("Select a product ID from the following list\n")
    val availableIds = displayAvailableForRentProducts(statement, bid)

    //Prompt product ID
    print("Selected Product ID : ")
    var productId = readInt()

    while(!availableIds.exists(_._1 == productId)){
      println("Please select a product ID from the list : ")
      productId = readInt()
    }

    //initial condition of the product
    val initCondition = availableIds.find(_._1 == productId).get._2

    println("Select a rental duration from the following list\n")
    val fees = displayFeesForProduct(statement, productId)

    //Prompt Fee ID
    print("Selected Fee ID : ")
    var feeId = readInt()

    while(!fees.exists(_._1 == feeId)){
      println("Please select a fee ID from the list : ")
      feeId = readInt()
    }

    val fee = fees.find(_._1 == feeId).get

    val duration = getDuration(fee._3)

    val rentsQuery = "INSERT INTO rents VALUES("+cid + "," + pyid + ","+productId +",\'"+initCondition+"\', DEFAULT, CURRENT_TIMESTAMP(2) + interval "+duration +" )"

    val updateQuery = "UPDATE product SET available=false WHERE prid="+productId

    println("Product added successfully.\n")
    (fee._2, List(rentsQuery, updateQuery))



  }

  def getDuration(fee : String): String = {
    fee match {
      case "1_HOUR"=> "\'1 hour\'"
      case "1_DAY" => "\'1 day\'"
      case "2_DAYS" => "\'2 day\'"
      case "1_WEEK" => "\'7 day\'"
      case "1_MONTH" => "\'1 month\'"
      case "1_YEAR" => "\'1 year\'"
      case _ => ""
    }
  }

}
