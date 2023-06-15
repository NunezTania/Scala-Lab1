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
  def prepareProducts(products : List[Product]) : Future[List[Int]]

class ProductImpl extends ProductService:

  var productPrepMap: Map[(String, String), Option[Future[Int]]] =
    Map[(String, String), Option[Future[Int]]](
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

  def prepareProducts(products : List[Product]) : Future[List[Int]] =
    def loop(product : Product, acc : Future[Int], n : Int) : Future[Int] =
      n match
        case 0 => {
          productPrepMap = productPrepMap + ((product.productType, product.brand.getOrElse(product.productType)) -> Some(acc))
          acc
        }
        case x => {
          acc flatMap { 
            case m => {
              val (mean, std, r) = getPreparationParameters(product.productType, product.brand.getOrElse(getDefaultBrand(product.productType)))
              FutureOps.randomSchedule(mean, std, r).transformWith( f => f match
                case Success(value) => loop(product, Future(m + 1), n - 1)
                case Failure(_) => loop(product, Future(m), n -1)
            )}
          }
        }
    Future.sequence(products.map( p => {
        productPrepMap((p.productType, p.brand.getOrElse(getDefaultBrand(p.productType)))) match
          case None => loop(p, Future(0), p.quantity)
          case Some(f) => loop(p, f flatMap {case _ => Future(0)}, p.quantity)
      }
    ))

end ProductImpl
