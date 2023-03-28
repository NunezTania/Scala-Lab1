package Data

trait ProductService:
  type BrandName = String
  type ProductName = String

  def getPrice(product: ProductName, brand: BrandName): Double
  def getDefaultBrand(product: ProductName): BrandName

class ProductImpl extends ProductService:
  // TODO - Part 2 Step 2
  def getPrice(product: ProductName, brand: String): Double =
    product match
      case "Bieres" =>
        brand match
          case "Boxer"      => 1.0
          case "Farmer"     => 1.0
          case "Wittekop"   => 2.0
          case "PunkIPA"    => 3.0
          case "Jackhammer" => 3.0
          case "Tenebreuse" => 4.0

      case "Croissants" =>
        brand match
          case "Cailler" => 2.0
          case "Maison"  => 2.0

  def getDefaultBrand(product: ProductName): BrandName =
    product match
      case "Bieres"     => "Boxer"
      case "Croissants" => "Cailler"

end ProductImpl
