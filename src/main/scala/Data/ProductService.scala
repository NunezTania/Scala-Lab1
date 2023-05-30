package Data

import scala.concurrent.duration.*

trait ProductService:
  type BrandName = String
  type ProductName = String

  def getPrice(product: ProductName, brand: BrandName): Double
  def getDefaultBrand(product: ProductName): BrandName
  def getPreparationParameters(product: ProductName, brand: BrandName): (Duration, Duration, Double)

class ProductImpl extends ProductService:
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

  def getPreparationParameters(product: ProductName, brand: String): (Duration, Duration, Double) =
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

end ProductImpl
