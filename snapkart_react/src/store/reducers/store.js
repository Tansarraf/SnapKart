import { configureStore } from "@reduxjs/toolkit";
import { productReducer } from "./ProductReducer";
import { errorReducer } from "./ErrorReducer";
import { CartReducer } from "./CartReducer";
import { authReducer } from "./authReducer";
import { paymentReducer } from "./paymentReducer";

const cartItems = localStorage.getItem("cartItems")
  ? JSON.parse(localStorage.getItem("cartItems"))
  : [];

const user = localStorage.getItem("auth")
  ? JSON.parse(localStorage.getItem("auth"))
  : null;

const selectUserCheckoutAddress = localStorage.getItem("CHECKOUT_ADDRESS") ? JSON.parse(localStorage.getItem("CHECKOUT_ADDRESS")) : [];

const initialState = {
  carts: { cart: cartItems },
  auth: { user: user, selectUserCheckoutAddress },
};
export const store = configureStore({
  reducer: {
    products: productReducer,
    errors: errorReducer,
    carts: CartReducer,
    auth: authReducer,
    payment: paymentReducer
  },
  preloadedState: initialState,
});

export default store;
