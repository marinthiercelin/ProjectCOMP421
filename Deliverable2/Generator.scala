/**
  * Created by tarek on 21/02/17.
  */

import java.util.Calendar
import java.sql.Date

import sun.util.calendar.Gregorian

import scala.util.Random

class Client(val cid : Int, val cname : String, val address : String, val creationDate: Date)
class Branch(val bid : Int, val address : String, val openingTime : String, val closingTime :String)
class Employee(val eid: Int, val ename : String, val startDate : Date, val salary :Int, val bid :Int, val schedule : String)
class Product(val prid: Int, val brand : String, val pname : String, val ptype : String,val pyear : Int,var pavailabe:Boolean, val bid : Int)
class ForRent(val prid : Int, val prCondition : String)
class ForSale(val prid : Int, val price : Int)
class SalesMan(val eid :Int)
class Manager(val eid :Int,val bid : Int)
class Fee(val fid :Int, val price : Int, val duration : String)
class Payment(val pyid : Int,val discnt :Int,val pyDate : (Date, Int, Int),val mthd :String,val amount : Double,val eid :Int,val cid : Int)
class Rents(val cid : Int,val pyid :Int,val prid:Int,val initcndit :String,val startDate :(Date, Int, Int),val endDate: (Date, Int, Int))
class Buys(val prid : Int,val pyid : Int)
class Rates(val rid: Int, val cid : Int,val  eid :Int,val rating :Int)
class PaysFor(val prid : Int,val fid : Int)



object Generator {
  val numBranches = 10
  val numEmpPerBranch = 20
  val numClients :Int = 300
  val numSalePerBranch = 20
  val numRentPerBranch = 20
  val numPayments = 100

  def main(args: Array[String]) {
    val result = createBranchesEmployeesClients(createNames())
    var productid = 1
    val branches = result._1
    val employees = result._2
    val managers = result._3
    val salesmen = result._4
    val clients = result._5

    result._1.foreach(b => println(branchToString(b)))
    result._2.foreach(e => println(employeeToString(e)))
    result._3.foreach(m => println(managerToString(m)))
    result._4.foreach(s => println(salesmanToString(s)))
    result._5.foreach(c => println(clientToString(c)))

    val lol = createSaleProducts(productid)
    productid = lol._3
    lol._1.foreach(p => println(productToString(p)))
    lol._2.foreach(f => println(forSaleToString(f)))

    val rent = createForRentProducts(productid)
    rent._1.foreach(p=> println(productToString(p)))
    rent._2.foreach(r => println(forrentToString(r)))

    val products : List[Product] = rent._1 ++ lol._1
    val sale = lol._2
    val rental = rent._2

    val fees = createFees()
    fees.foreach(f => println(feeToString(f)))

    val paysfor = createPaysFor(fees, rent._2)
    paysfor.foreach(p => println(paysForToString(p)))

    val paymnts = createPayments(products, clients, employees, sale, rental, paysfor, fees)

    val payments = paymnts._1
    val rents = paymnts._2
    val buys = paymnts._3
    val rates = paymnts._4

    payments.foreach(p => println(paymentToString(p)))
    rents.foreach(r => println(rentsToString(r)))
    buys foreach(b=> println(buysToString(b)))
    rates foreach(r => println(ratesToString(r)))




  }
  def createNames() ={
    val firstnames = List("Delfina",  "Lucie",  "Jess","Reginald","Enola","Allan","Wilford","Ezequiel","Alethia","Katerine","Kit","Cheri","Jacalyn","Kenya","Kazuko","Earnestine","Melinda","Jettie","Cathrine","Ulrike","Keri","Janette","Geraldo","Dominick","Kylee","Louvenia","Vera","Porfirio","Annabell","Tameika")
    val lastnames = List("Smith", "Thompson", "Williams", "Jones", "Anderson", "Jackson", "Robinson", "Clark", "Rodriguez", "Lewis", "Lee", "Walker", "Allen", "Young", "Hernandez", "Wright", "Lopez", "Hill", "Scott", "Green", "Adams", "Baker", "Nelson", "Carter", "Mitchell", "Perez", "Roberts", "Turner", "Parker", "Evans")

    for {f <- firstnames; l <- lastnames} yield "\'" + f + ' ' + l + "\'"
  }
  def branchToString(b : Branch)={
    "INSERT INTO branch VALUES(" + b.bid + "," + b.address + "," + b.openingTime + "," + b.closingTime + ");"
  }

  def employeeToString(e :Employee)= {
    "INSERT INTO employee VALUES(" + e.eid + "," + e.ename + ",\'" + e.startDate + "\', " +e.salary + ","+ e.bid + ',' + e.schedule + ");"
  }
  def managerToString(m : Manager)= {
    "INSERT INTO manager VALUES(" + m.eid + ','+ m.bid + ");"
  }
  def salesmanToString(s : SalesMan) ={
    "INSERT INTO salesman VALUES(" + s.eid + ");"
  }

  def clientToString(c : Client) = {
    "INSERT INTO client VALUES(" + c.cid + ',' + c.cname + ',' + c.address + ",\'" + c.creationDate + "\');"
  }

  def productToString(p : Product) ={
    "INSERT INTO product VALUES(" + p.prid + ',' + p.brand + ',' + p.pname + ',' + p.ptype + ',' + p.pyear + ',' + p.pavailabe + ',' + p.bid + ");"
  }

  def forSaleToString(f : ForSale)= {
    "INSERT INTO forsale VALUES(" + f.prid + ',' + f.price + ");"
  }

  def forrentToString(r : ForRent) = {
    "INSERT INTO forrent VALUES(" + r.prid + ',' + r.prCondition + ");"
  }

  def feeToString(f : Fee)= {
    "INSERT INTO fee VALUES(" + f.fid + ',' + f.price + ',' + f.duration + ");"
  }

  def paysForToString(p : PaysFor) ={
    "INSERT INTO paysfor VALUES("+ p.prid + ',' + p.fid + ");"
  }

  def paymentToString(p : Payment) ={
    val date = "\'" + p.pyDate._1 + ' ' + p.pyDate._2 + ':' + p.pyDate._3 + "\'"
    "INSERT INTO payment VALUES(" + p.pyid + ',' + p.discnt + ',' + date + ',' + p.mthd +','+ p.amount + ',' + p.eid + ',' + p.cid + ");"
  }

  def rentsToString(r : Rents) ={
    val startDate = "\'" + r.startDate._1 + ' ' + r.startDate._2 + ':'+r.startDate._3 + "\'"
    val endDate = "\'" + r.endDate._1 + ' ' + r.endDate._2 + ':'+r.endDate._3 + "\'"
    "INSERT INTO rents VALUES(" + r.cid + ',' + r.pyid + ',' + r.prid + ','+ r.initcndit + ',' +startDate + ',' + endDate + ");"
  }

  def buysToString(b : Buys)={
    "INSERT INTO buys VALUES(" + b.prid + ',' + b.pyid + ");"
  }
  def ratesToString(r : Rates)={
    "INSERT INTO rates VALUES(" +r.rid +','+ r.cid + ',' + r.eid + ',' + r.rating + ");"
  }


  def createBranchesEmployeesClients(names : List[String]) = {
    var usedNames = List()

    var employees = List.empty[Employee]
    var managers = List.empty[Manager]
    var salesmen = List.empty[SalesMan]
    var clients = List.empty[Client]
    var branches = List.empty[Branch]
    val r = new Random()

    val openingTimes = List("\'08:00\'", "\'09:00\'", "\'07:30\'")
    val closingTimes = List("\' 16:00\'", "\'18:00\'", "\'17:00'")

    var eid = 1
    for (i <- 1 to numBranches){
      branches = new Branch(i, generateRandomAdress(r), openingTimes(r.nextInt(3)), closingTimes(r.nextInt(3))) :: branches

      employees = employees :+ new Employee(eid, generateRandomName(r, names), generateRandomDate(), r.nextInt(1000) + 4000, i, generateRandomSchedule(r))
      managers = managers :+ new Manager(eid, i)
      eid = eid + 1
      for (j <- 1 to numEmpPerBranch) {
        employees = employees :+ new Employee(eid, generateRandomName(r, names), generateRandomDate(), r.nextInt(1000) + 2000, i, generateRandomSchedule(r))
        salesmen = salesmen :+ new SalesMan(eid)
        eid = eid + 1
      }
    }

    for (i <- 1 to numClients){
      clients = clients :+ new Client(i, generateRandomName(r, names), generateRandomAdress(r), generateRandomDate())
    }

    (branches, employees, managers, salesmen, clients)

  }

  def getRandomYear(r : Random)={
     r.nextInt(5) + 2008
  }

  def createSaleProducts(startID : Int) = {
    val brands = List("\'Rossignol\'", "\'Salomon\'", "\'Nordica\'", "\'Armada\'", "\'Burton\'")

    val types = List("\'Ski\'", "\'Snowboard\'", "\'Poles\'", "\'SkiBoots\'", "\'Snowboots\'", "\'Helmets\'", "\'Skiwear\'", "\'Accessories\'")
    val r = new Random()

    var products = List.empty[Product]
    var forsale = List.empty[ForSale]

    var productid = startID

    for (i <- 1 to numBranches){
      for(j <- 1 to numSalePerBranch){
        val typ = types(r.nextInt(types.size))
        products = products :+ new Product(productid, brands(r.nextInt(brands.size)), "NULL", typ , getRandomYear(r) , true, i )
        var price = 0
        if ((typ == "\'Ski\'") || (typ == "\'Snowboard\'")) {
          price = r.nextInt(101) + 400
        } else {
          price = r.nextInt(200) + 30
        }

        forsale = forsale :+ new ForSale(productid, price)
        productid = productid + 1
      }
    }
    (products, forsale, productid)
  }

  def createForRentProducts(startID : Int) = {
    val brands = List("\'Rossignol\'", "\'Salomon\'", "\'Nordica\'", "\'Armada\'", "\'Burton\'")
    val conditions = List("\'Good\'", "\'Medium\'", "\'Bad\'")

    val types = List("\'Ski\'", "\'Snowboard\'", "\'Poles\'", "\'SkiBoots\'", "\'Snowboots\'")
    val r = new Random()

    var productid = startID
    var products = List.empty[Product]
    var forrent = List.empty[ForRent]

    for (i <- 1 to numBranches){
      for(j <- 1 to numRentPerBranch){
        val condition = conditions(r.nextInt(3))
        products = products :+ new Product(productid, brands(r.nextInt(brands.size)), "NULL", types(r.nextInt(types.size)), getRandomYear(r) , true, i)

        forrent = forrent :+ new ForRent(productid, condition)
        productid = productid + 1
      }
    }

    (products, forrent)
  }

  def createFees() = {
    val timeRange = List("\'1_YEAR\'", "\'1_MONTH\'", "\'1_WEEK\'", "\'1_DAY\'", "\'1_HOUR\'")
    val prices = List(List(450,400,350, 100, 70, 50), List(250, 200, 150, 50, 40, 30), List(150, 130, 100, 30, 20, 15), List(90, 80, 70, 20, 15, 10), List(20, 15, 10, 8, 6, 3))
    val r = new Random()
    var fees = List.empty[Fee]
    var id = 0
    timeRange.flatMap(range => prices(timeRange.indexOf(range)).map(price => {
      id = id +1
      new Fee(id, price, range)
    }
      ))

  }

  def createPaysFor(fees : List[Fee], forRent : List[ForRent]) = {
    var paysfor = List.empty[PaysFor]
    val r = new Random()

    val yearly = fees.filter(_.duration == "\'1_YEAR\'")
    val monthly = fees.filter(_.duration == "\'1_MONTH\'")
    val weekly = fees.filter(_.duration == "\'1_WEEK\'")
    val daily = fees.filter(_.duration == "\'1_DAY\'")
    val hourly = fees.filter(_.duration == "\'1_HOUR\'")

    for (p <- forRent){
      val y = yearly(r.nextInt(yearly.size))
      val m = monthly(r.nextInt(monthly.size))
      val w = weekly(r.nextInt(weekly.size))
      val d = daily(r.nextInt(daily.size))
      val h = hourly(r.nextInt(hourly.size))

      paysfor = paysfor :+ new PaysFor(p.prid, y.fid)
      paysfor = paysfor :+ new PaysFor(p.prid, m.fid)
      paysfor = paysfor :+ new PaysFor(p.prid, w.fid)
      paysfor = paysfor :+ new PaysFor(p.prid, d.fid)
      paysfor = paysfor :+ new PaysFor(p.prid, h.fid)

    }

    paysfor

  }

  def createPayments(products : List[Product], clients : List[Client], employees : List[Employee], forSale : List[ForSale], forRent :List[ForRent], paysfor : List[PaysFor], fees : List[Fee])= {
    val r = new Random()
    var payments = List.empty[Payment]
    var rents = List.empty[Rents]
    var buys = List.empty[Buys]
    var ratings = List.empty[Rates]

    val mthds = List("\'cash\'", "\'credit\'", "\'debit\'")

    var pyID = 1

    for (i <- 1 to numPayments) {
      val c = clients(r.nextInt(clients.size))
      val e = employees(r.nextInt(employees.size))

      val prods = products filter (_.bid == e.bid)
      val prods_by_id = prods.map(_.prid)
      val forsale = forSale filter (f => prods_by_id.contains(f.prid)) //verify product in branch
      val forrent = forRent filter (r => prods_by_id.contains(r.prid))

      val timeStamp = generateRandomTimeStamp() //timestamp
      val method = mthds(r.nextInt(3)) //pymthd
      val numItems = r.nextInt(4)+1 //number of items

      var amount = 0

      var taken_prods = List.empty[Int]
      for (i <- 1 to numItems) {
        val p = prods(r.nextInt(prods.size)) //Choose random product
        if (p.pavailabe && !taken_prods.contains(p.prid))  {
          taken_prods = taken_prods :+ p.prid
          p.pavailabe = false
          if (forsale.exists(f => f.prid == p.prid)) {
            //Add buys element
            buys = buys :+ new Buys(p.prid, pyID)
            amount = amount + forsale.find(f => f.prid == p.prid).get.price
          } else {
            val startDate = timeStamp
            val initcntion = forRent.find(_.prid == p.prid).get.prCondition
            val duration = r.nextInt(5) // for each duration
            val feeid = paysfor.filter(g => g.prid == p.prid)(duration).fid

            val price = fees.find(_.fid == feeid).get.price
            amount = amount + price
            val endDate = getEndDate(startDate, duration)

            if (endDate._1.compareTo(Date.valueOf("2017-02-21")) == -1) {
              p.pavailabe = true
            }
            rents = rents :+ new Rents(c.cid, pyID, p.prid, initcntion, startDate, endDate)
          }
        }
      }
      if (amount > 0) {
        payments = payments :+ new Payment(pyID, r.nextInt(50), timeStamp, method, amount, e.eid, c.cid)
        ratings = ratings :+ new Rates(pyID, c.cid, e.eid, r.nextInt(5) + 1)
        pyID  = pyID + 1
      }


    }

    (payments, rents, buys, ratings)
  }

  def getEndDate(startDate : (Date, Int, Int), duration : Int) ={
    val c = Calendar.getInstance()
    c.setTime(startDate._1)
    if (duration == 0) { //Year
      c.add(Calendar.YEAR, 1)
      (dateFromCalendar(c), startDate._2, startDate._3)
    } else if (duration == 1) { //Month
      c.add(Calendar.MONTH, 1)
      (dateFromCalendar(c), startDate._2, startDate._3)
    } else if (duration == 2){ // Week
      c.add(Calendar.DAY_OF_YEAR, 7)
      (dateFromCalendar(c), startDate._2, startDate._3)
    } else if (duration == 3) { //day
      c.add(Calendar.DAY_OF_YEAR, 1)
      (dateFromCalendar(c), startDate._2, startDate._3)
    } else {
      (startDate._1, startDate._2 + 1, startDate._3)
    }
  }
  def generateRandomTimeStamp() : (Date, Int, Int) ={
    val r = new Random()
    val date = generateRandomDate()
    val hour = r.nextInt(10) + 8
    val min = r.nextInt(60)

    (date, hour, min)
  }

  def dateFromCalendar(date: Calendar) ={
    Date.valueOf(date.get(Calendar.YEAR)+"-" + (date.get(Calendar.MONTH) + 1) + "-"+date.get(Calendar.DAY_OF_MONTH))
  }
  def generateRandomAdress(r :Random)={
    val streetNames = List("Strawberry Lane", "Anne street", "Sunset Avenue", "Eagle street", "Sun Street", "Amherst west", "Phairview", "Hill street")
    val cities = List("Paris","Hong Kong", "Montreal", "Toronto", "Jeddah", "New Delhi", "Dubai", "Beijing", "Ankara")
    val countries = List("UAE", "Canada", "USA", "China", "India", "Turkey", "France")

    val streetnum = r.nextInt(10000)

    streetnum + ",\'" + streetNames(r.nextInt(streetNames.size)) + "\',\'" + cities(r.nextInt(cities.size)) + "\',\'" + countries(r.nextInt(countries.size)) + "\'";
  }

  def generateRandomSchedule(r :Random)={
    val days = List("\'12345__\'", "\'1_3_560\'", "\'123__60\'", "\'_234_60\'", "\'123_56_\'")
    val startTimes = List("\'08:00\'", "\'09:00\'", "\'07:30\'")
    val endTimes = List("\'16:00\'", "\'18:00\'", "\'17:00'")
    days(r.nextInt(days.size)) + ',' + startTimes(r.nextInt(startTimes.size)) + ',' + endTimes(r.nextInt(endTimes.size))

  }

  def generateRandomDate()= {
    val r = new Random()
    val date = Calendar.getInstance(); // the current date/time
    val numberOfDaysToAdd = -r.nextInt(3650);  // i.e. up to approximately ten years
    date.add(Calendar.DAY_OF_YEAR, numberOfDaysToAdd); // add to the current date

    val month = date.get(Calendar.MONTH) + 1
    Date.valueOf(date.get(Calendar.YEAR)+"-" + month + "-"+date.get(Calendar.DAY_OF_MONTH))
  }
  def generateRandomName(r: Random, names : List[String])={
    names(r.nextInt(names.size))
  }
}