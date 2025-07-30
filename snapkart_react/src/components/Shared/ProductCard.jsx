import { useState } from "react";
import { FaShoppingCart } from "react-icons/fa";
import ProductViewModal from "./ProductViewModal";
import truncateText from "../../Utils/truncateText";
import { useDispatch } from "react-redux";
import { addTocart } from "../../store/actions";
import toast from "react-hot-toast";

// Whenever we click a product -> saved in selectedViewProduct
//setOpenProductViewModal will handle whether the model should be displayed or not
const ProductCard = ({
  productId,
  productName,
  image,
  description,
  quantity,
  price,
  discount,
  specialPrice,
  about = false,
}) => {
  const [openProductViewModal, setOpenProductViewModal] = useState(false);
  const buttonLoader = false;
  const [selectedViewProduct, setSelectedViewProduct] = useState("");
  const isAvailable = quantity && Number(quantity) > 0;
  const dispatch = useDispatch();

  const handleProductView = (product) => {
    if (!about) {
      setSelectedViewProduct(product);
      setOpenProductViewModal(true);
    }
  };

  const addToCartHandler = (cartItems) => {
    dispatch(addTocart(cartItems, 1, toast));
  };
  return (
    //Product box
    <div className="border border-none rounded-lg shadow-xl overflow-hidden transition-shadow duration-300">
      <div
        onClick={() => {
          handleProductView({
            id: productId,
            productName,
            image,
            description,
            quantity,
            price,
            discount,
            specialPrice,
          });
        }}
        className="w-full overflow-hidden aspect-[3/2] flex items-center justify-center"
      >
        {/* Image scaling  */}
        <img
          className="max-w-full max-h-full object-contain cursor-pointer transition-transform duration-300 transform hover:scale-105"
          src={image}
          alt={productName}
        />
      </div>
      {/* Product Name */}
      <div className="p-4">
        <h2
          onClick={() => {
            handleProductView({
              id: productId,
              productName,
              image,
              description,
              quantity,
              price,
              discount,
              specialPrice,
            });
          }}
          className="text-lg font-semibold mb-2 cursor-pointer"
        >
          {truncateText(productName, 50)}
        </h2>
        {/* Description */}
        <div className="min-h-20 max-h-20">
          <p className="text-gray-600 text-sm">
            {truncateText(description, 80)}
          </p>
        </div>

        {!about && (
          <div className="flex items-center justify-between">
            {specialPrice ? (
              <div className="flex flex-col">
                <span className="text-gray-400 line-through">
                  ${Number(price).toFixed(2)}
                </span>
                <span className="text-xl font-bold text-slate-700">
                  ${Number(specialPrice).toFixed(2)}
                </span>
              </div>
            ) : (
              <span className="text-gray-400 line-through">
                {" "}
                ${Number(price).toFixed(2)}
              </span>
            )}
            <button
              disabled={!isAvailable || buttonLoader}
              onClick={() =>
                addToCartHandler({
                  image,
                  productName,
                  description,
                  specialPrice,
                  price,
                  quantity,
                  productId,
                })
              }
              className={`bg-blue-500 ${
                isAvailable
                  ? "opacity-100 hover:bg-blue-600 cursor-pointer"
                  : "opacity-70"
              }
            text-white py-2 px-3 rounded-lg flex items-center transition-colors duration-300 w-36 justify-center`}
            >
              <FaShoppingCart className="mr-2" />
              {isAvailable ? "Add to Cart" : "Out of Stock"}
            </button>
          </div>
        )}
      </div>
      <ProductViewModal
        open={openProductViewModal}
        setOpen={setOpenProductViewModal}
        product={selectedViewProduct}
        isAvailable={isAvailable}
      />
    </div>
  );
};

export default ProductCard;
