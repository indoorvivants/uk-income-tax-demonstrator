package example

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import scala.collection.SortedMap

case class TaxBand(
    name: String,
    lower: Int,
    upper: Option[Int],
    taxRate: BigDecimal,
    color: String
)
object TaxBand:
  given Ordering[TaxBand] = Ordering.by(_.lower)

def incomeTaxBands(income: Int) =

  val personalAllowance = 12570
  val paRemaining       = (personalAllowance - (income - 100000) / 2).max(0)
  println(paRemaining)
  Option
    .when(paRemaining > 0) {
      TaxBand(
        "Personal allowance",
        0,
        Some(paRemaining),
        BigDecimal.decimal(0),
        "#37306B"
      )
    }
    .toList ++
    List(
      TaxBand(
        "Basic rate",
        paRemaining,
        Some(50270),
        BigDecimal.decimal(20),
        "#66347F"
      ),
      TaxBand(
        "Higher rate",
        50271,
        Some(150000),
        BigDecimal.decimal(40),
        "#9E4784"
      ),
      TaxBand(
        "Additional rate",
        150001,
        None,
        BigDecimal.decimal(45),
        "#D27685"
      )
    )
end incomeTaxBands

val niTaxBands = List(
  TaxBand(
    "No contributions",
    0,
    Some(12570),
    BigDecimal.decimal(0),
    "#37306B"
  ),
  TaxBand(
    "Basic rate",
    12571,
    Some(50270),
    BigDecimal.decimal(12),
    "#66347F"
  ),
  TaxBand(
    "Higher rate",
    50271,
    None,
    BigDecimal.decimal(2),
    "#9E4784"
  )
)

case class TaxableIncome(
    amount: Int,
    taxes: Int,
    percentOfWhole: BigDecimal
)

def calculateTax(income: Int, bands: List[TaxBand]) =
  val mp = bands
    .foldLeft(Map.empty[TaxBand, TaxableIncome]) { case (accM, band) =>
      val taxableInBand =
        band.upper match
          case None =>
            (income - band.lower).max(0)
          case Some(value) =>
            val width = value - band.lower
            (income - band.lower).min(width).max(0)

      val taxes = band.taxRate./(100).*(taxableInBand).toInt
      accM.updated(
        band,
        TaxableIncome(
          amount = taxableInBand,
          taxes = taxes,
          percentOfWhole = BigDecimal(taxableInBand) / BigDecimal(income)
        )
      )
    }
  SortedMap.from(mp)
end calculateTax

var income = Var(125000)

def taxBreakdown(amount: TaxableIncome, label: String) =
  val taxPercent = (amount.taxes.toFloat / amount.amount.toFloat)
    .*(100)
    .toInt
  val remPercent = 100 - taxPercent
  val remAmount  = amount.amount - amount.taxes
  div(
    cls := "flex gap-2",
    Option.when(taxPercent > 0) {
      div(
        width.percent := taxPercent,
        minWidth      := "100px",
        p(cls := "text-sm", label),
        div(
          p(
            cls := "whitespace-nowrap text-lg font-bold",
            "£",
            amount.taxes
          ),
          cls := "flex-1 p-2 bg-red-800 text-white"
        )
      )
    }
  )
end taxBreakdown

def taxesBreakdown(bands: Int => List[TaxBand], label: String) =

  val calculatedTaxes = income.signal.map(i => calculateTax(i, bands(i)))
  div(
    cls := "flex flex-row gap-4",
    children <-- calculatedTaxes.map(_.toList.filter(_._2.amount > 0).flatMap {
      case (tb, amount) =>
        Option.when(amount.amount > 0) {
          div(
            width.percent := amount.percentOfWhole.*(100).toInt,
            minWidth      := "150px",
            p(tb.name, cls := "whitespace-nowrap"),
            div(
              p(
                cls := "text-white whitespace-nowrap text-2xl font-bold",
                "£",
                amount.amount
              ),
              cls             := "flex-1 p-2",
              backgroundColor := tb.color
            ),
            taxBreakdown(amount, label)
          )
        }
    })
  )
end taxesBreakdown

val myApp =
  div(
    div(
      cls := "inline-flex text-4xl",
      p("Your yearly income is £"),
      input(
        value <-- income.signal.map(_.toString),
        tpe := "text",
        cls := Seq("border-b-2", "border-zinc-500", "text-4xl"),
        onInput.mapToValue.map(_.toInt) --> income.writer
      )
    ),
    p(
      cls := "text-xl my-6",
      "Regardless of the type of tax, your income is split into several \"bands\", and ",
      u("only the amount in that band is taxed at the specified rate")
    ),
    p(
      cls := "text-xl my-6 border-b-2",
      "First, let's tackle ",
      b("income tax")
    ),
    taxesBreakdown(incomeTaxBands, "Income tax"),
    p(
      cls := "text-xl my-6 border-b-2",
      "Moving on to ",
      b("National Insurance contributions")
    ),
    taxesBreakdown(_ => niTaxBands, "NI")
  )
end myApp

@main def main =
  renderOnDomContentLoaded(dom.document.getElementById("appContainer"), myApp)
