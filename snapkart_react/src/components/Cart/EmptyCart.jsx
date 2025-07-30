import { MdShoppingCart } from "react-icons/md";
import { Link } from "react-router-dom";

const EmptyCart = () => {
  return (
    <div className="min-h-[800px] flex flex-col items-center justify-center">
      <div className="flex flex-col items-center">
        <MdShoppingCart size={80} className="mb-4 text-slate-500" />
        <div className="text-4xl font-bold text-slate-700">
          Your
          <span className="text-orange-400"> Snap</span>
          <span className="text-blue-700">Kart</span> Cart is empty
        </div>
        <div className="text-lg font-bold text-slate-500 mt-2">
          Your shopping cart lives to serve. Give it purpose — fill it with
          groceries, clothing, household supplies, electronics, and more.
        </div>
        <div className="text-lg font-bold text-slate-500">
          Continue shopping on the{" "}
          <Link className="font-bold text-slate-600" to="/">
            <span className="text-orange-400"> Snap</span>
            <span className="text-blue-700">Kart</span>
          </Link>{" "}
          homepage or learn about today's deals.
        </div>
      </div>
    </div>
  );
};

export default EmptyCart;

// <div className="flex flex-col items-center mb-12">
//           <h1 className="text-4xl font-bold text-gray-900 flex items-center gap-3 lg:px-14 sm:px-8 px-4 py-10">
//             Your Vintora Cart is empty
//           </h1>
//           <p className="text-lg text-gray-600 mt-2">
//             Your Shopping Cart lives to serve. Give it purpose — fill it with
//             groceries, clothing, household supplies, electronics, and more.
//             Continue shopping on the{" "}
//             <Link className="font-semibold text-slate-800" to="/">
//               Vintora
//             </Link>{" "}
//             homepage or learn about today's deals.
//           </p>
//         </div>
