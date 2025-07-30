import {
  MdArrowBack,
  MdOutlineShoppingCart,
  MdShoppingCartCheckout,
} from "react-icons/md";
import { useDispatch, useSelector } from "react-redux";
import { Link } from "react-router-dom";
import ItemContent from "./ItemContent";
import EmptyCart from "./EmptyCart";
import { formatPrice } from "../../Utils/formatPrice";

const Cart = () => {
  const dispatch = useDispatch();
  const { cart } = useSelector((state) => state.carts);
  const newCart = { ...cart };

  newCart.totalPrice = cart?.reduce(
    (acc, cur) => acc + Number(cur?.specialPrice) * Number(cur?.quantity),
    0
  );

  if (!cart || cart.length === 0) return <EmptyCart />;

  return (
    <div className="lg:px-14 sm:px-8 px-4 py-10">
      <div className="flex flex-col items-center mb-12">
        <h1 className="text-4xl font-bold text-gray-900 flex items-center gap-3">
          <MdOutlineShoppingCart size={36} className="text-gray-900" />
          Your Cart
        </h1>
        <p className="text-lg text-gray-600 mt-2">All your selected products</p>
      </div>
      <div className="grid md:grid-cols-5 grid-cols-4 pb-2 items-center font-semibold gap-4">
        <div className="md:col-span-2 justify-self-start text-lg text-slate-800 lg:ps-4">
          Product
        </div>
        <div className="justify-self-center text-lg text-slate-800">Price</div>
        <div className="justify-self-center text-lg text-slate-800">
          Quantity
        </div>
        <div className="justify-self-center text-lg text-slate-800">Total</div>
      </div>
      <div>
        {cart &&
          cart.length > 0 &&
          cart.map((item, i) => <ItemContent key={i} {...item} />)}
      </div>
      <div className="border-t-[1.5px] border-slate-200 py-4 flex sm:flex-row sm:px-0 px-2 flex-col sm:justify-between gap-4">
        <div></div>
        <div className="flex text-sm gap-1 flex-col">
          <div className="flex justify-between w-full md:text-lg text-sm font-semibold">
            <span>Subtotal</span>
            <span>{formatPrice(newCart?.totalPrice)}</span>
          </div>
          <p className="text-slate-500">
            Taxes and shipping charges calculated at checkout
          </p>
          <Link className="w-full flex justify-end" to="/checkout">
            <button
              onClick={() => {}}
              className="font-semibold w-[300px] py-2 px-4 hover:cursor-pointer hover:text-gray-300 hover:bg-blue-900 transition duration-500 rounded-sm bg-blue-500 text-white flex items-center justify-center gap-2 "
            >
              <MdShoppingCartCheckout size={20} />
              Checkout
            </button>
          </Link>
          <Link
            className="flex gap-2 mt-2 items-center text-slate-500"
            to="/products"
          >
            <MdArrowBack />
            <span>Continue Shopping</span>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Cart;
