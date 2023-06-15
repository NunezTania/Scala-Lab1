package Data
import Chat.ExprTree.Product
import scala.concurrent.duration.*
import scala.concurrent.Future
import Utils.FutureOps
import scala.util.Failure
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global

trait ProductService:
  type BrandName = String
  type ProductName = String

  def getPrice(product: ProductName, brand: BrandName): Double
  def getDefaultBrand(product: ProductName): BrandName
  def getPreparationParameters(
      product: ProductName,
      brand: BrandName
  ): (Duration, Duration, Double)

class ProductImpl extends ProductService:

  var productPrepMap: Map[(String, String), Option[Future[Unit]]] =
    Map[(String, String), Option[Future[Unit]]](
      ("biere", "boxer") -> None,
      ("biere", "farmer") -> None,
      ("biere", "wittekop") -> None,
      ("biere", "punkipa") -> None,
      ("biere", "jackhammer") -> None,
      ("biere", "tenebreuse") -> None,
      ("croissant", "cailler") -> None,
      ("croissant", "maison") -> None
    )
  // TODO - Part 2 Step 2
  def getPrice(product: ProductName, brand: String): Double =
    product match
      case "biere" =>
        brand match
          case "boxer"      => 1.0
          case "farmer"     => 1.0
          case "wittekop"   => 2.0
          case "punkipa"    => 3.0
          case "jackhammer" => 3.0
          case "tenebreuse" => 4.0

      case "croissant" =>
        brand match
          case "cailler" => 2.0
          case "maison"  => 2.0

  def getPreparationParameters(
      product: ProductName,
      brand: String
  ): (Duration, Duration, Double) =
    product match
      case "biere" =>
        brand match
          case "boxer"      => (1.second, 1.second, 0.5)
          case "farmer"     => (2.second, 2.second, 0.6)
          case "wittekop"   => (3.second, 3.second, 0.7)
          case "punkipa"    => (4.second, 4.second, 0.8)
          case "jackhammer" => (5.second, 5.second, 0.9)
          case "tenebreuse" => (6.second, 6.second, 1.0)

      case "croissant" =>
        brand match
          case "cailler" => (1.second, 1.second, 0.5)
          case "maison"  => (4.second, 4.second, 1.0)

  def getDefaultBrand(product: ProductName): BrandName =
    product match
      case "biere"     => "boxer"
      case "croissant" => "maison"

  def makeProducts(products: List[Product]): List[Future[String]] = {
    val futures = List()

    for prod <- products do

      val brand = prod.brand.getOrElse(getDefaultBrand(prod.productType))

      val (mean, std, r) = getPreparationParameters(
        prod.productType,
        brand
      )
      val state = productPrepMap((prod.productType, brand))
      val count = prod.quantity
      val (f, nbSucess) = state match
        case Some(future) =>
          loopFuture(
            (mean, std, r),
            prod.productType,
            brand,
            count,
            future,
            0
          )
        case None => {
          val future = FutureOps.randomSchedule(mean, std, r)
          loopFuture(
            (mean, std, r),
            prod.productType,
            brand,
            count - 1,
            future,
            0
          )
        }

      // update the map with the new future
      productPrepMap = productPrepMap + ((prod.productType, brand) -> Some(f))

      val message =
        if (nbSucess == count) then
          "All products have been prepared successfully"
        else if (nbSucess == 0) then "No product has been prepared successfully"
        else s"Only $nbSucess products have been prepared successfully"

      futures.appended(Future(message))
    futures
  }

  def loopFuture(
      params: (Duration, Duration, Double),
      prodType: String,
      brand: String,
      count: Int,
      acc: Future[Unit],
      nbSuccess: Int
  ): (Future[Unit], Int) =
    if count == 0 then (acc, nbSuccess)
    else
      val f = acc.transformWith {
        case Success(_) => {
          var success = nbSuccess + 1
          FutureOps.randomSchedule(params._1, params._2, params._3)
        }
        case Failure(_) => {
          FutureOps.randomSchedule(params._1, params._2, params._3)
        }
      }

      loopFuture(params, prodType, brand, count - 1, f, nbSuccess)

end ProductImpl
